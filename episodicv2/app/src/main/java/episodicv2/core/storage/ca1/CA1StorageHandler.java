/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage.ca1;

import java.io.RandomAccessFile;
import episodicv2.utils.SimpleLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.entities.SceneRelation;

/**
 *
 * @author luis_
 */
public class CA1StorageHandler {

    private static final String INDEX_FILE = Configuration.STORAGE_BASE_PATH + "/ca1/index.txt";
    private static final String STORAGE_PATH = Configuration.STORAGE_BASE_PATH + "/ca1/";
    private static final String FILENAME = Configuration.CA1_STORAGE_BASEFILENAME;

    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;
    private static final double RETRIEVAL_SIMILARITY_THRESHOLD = Configuration.RETRIEVAL_SIMILARITY_THRESHOLD;

    private static Map<Integer, ConcurrentHashMap<Integer, SceneRelation>> midTermMemorySceneRelations;
    private static Map<Integer, Boolean> midTermMemorySceneRelationsQueried;

    private static Map<Integer, Boolean> storedKeys;

    private static CA1StorageHandler instance = null;

    private Timer consolidationTimer;

    private CA1StorageHandler() {

        midTermMemorySceneRelations = new ConcurrentHashMap<>();
        midTermMemorySceneRelationsQueried = new ConcurrentHashMap<>();

        storedKeys = new ConcurrentHashMap<>();

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();

    }

    private CA1StorageHandler(boolean load) {

        midTermMemorySceneRelations = new ConcurrentHashMap<>();
        midTermMemorySceneRelationsQueried = new ConcurrentHashMap<>();

        storedKeys = new ConcurrentHashMap<>();

        if (load) {
            load();
        }

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();
    }

    public static CA1StorageHandler getInstance() {

        if (instance == null) {
            instance = new CA1StorageHandler();
        }

        return instance;
    }

    public static CA1StorageHandler getInstance(boolean load) {
        if (instance == null) {
            instance = new CA1StorageHandler(load);
        }
        return instance;
    }

    /**
     *
     */
    public void startConsolidation() {

        long delay = Configuration.CONSOLIDATION_INTERVAL;

        consolidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SimpleLogger.log(this, "Storing...");
                persistRelations();
            }
        }, delay, delay);
    }

    public void addVertex(SceneRelation edge, double affectIntensity) {

        int source = edge.getScene1Id();
        int destination = edge.getScene2Id();
        long timeStamp = System.currentTimeMillis();

        //No se guardan las relaciones incompletas
        if (source == 0 || destination == 0) {
            return;
        }

        //No se guardan las relaciones a si mismo
        if (source == destination) {
            return;
        }

        //SimpleLogger.log(this, "Associated pair " + edge.getScene2Id() + "," + edge.getScene1Id());
        createIfNotExists(source);
        createIfNotExists(destination);

        ConcurrentHashMap<Integer, SceneRelation> sourceRelations = midTermMemorySceneRelations.get(source);
        ConcurrentHashMap<Integer, SceneRelation> destinationRelations = midTermMemorySceneRelations.get(destination);

        SceneRelation sourceRelation = sourceRelations.get(destination);
        SceneRelation destinationRelation = destinationRelations.get(source);

        if (sourceRelation == null) {
            sourceRelations.put(destination, new SceneRelation(destination, source, edge.getTime()));
        } else {
            sourceRelation.incrementRepetitions(affectIntensity);
            sourceRelation.setTime(edge.getTime());
            sourceRelation.setTimeStamp(timeStamp);
            sourceRelation.setUpdated(true);

        }

        if (destinationRelation == null) {
            destinationRelations.put(source, new SceneRelation(source, destination, edge.getTime()));
        } else {
            destinationRelation.incrementRepetitions(affectIntensity);
            destinationRelation.setTime(edge.getTime());
            destinationRelation.setTimeStamp(timeStamp);
            destinationRelation.setUpdated(true);

        }

    }

    private void createIfNotExists(int key) {
        if (!midTermMemorySceneRelations.containsKey(key)) {
            midTermMemorySceneRelations.put(key, new ConcurrentHashMap<Integer, SceneRelation>());
        }
    }

    public ArrayList<SceneRelation> retrieveSceneRelations(int sceneId, long timeStamp) {

        ConcurrentHashMap<Integer, SceneRelation> sourceRelations = midTermMemorySceneRelations.get(sceneId);

        ArrayList<SceneRelation> retrievedRelations = new ArrayList<>();

        if (sourceRelations != null) {

            //CAMBIAR A ITERATOR POR PROBLEMAS DE CONCURRENCIA
            for (SceneRelation relation : sourceRelations.values()) {

                double elapsedTime = (timeStamp - relation.getTimeStamp());

                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, relation.getActivation());

                //AND THE SCENE HAS NOT DECAY
                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                    relation.incrementRepetitions(0.0);
                    relation.setTimeStamp(timeStamp);

                    retrievedRelations.add(relation);

                }

            }

            //DEBIDO A QUE LAS NUEVAS RELACIONES CREADAS Y SU RELACION SIMETRICA SE AGREGAN A MID-TERM MEMORY
            //ESTO PUEDE EVITAR QUE SE REALICE EL QUERY A LTM A PESAR DE AUN NO SE HAN CARGADO LAS MEMORIAS QUE SI DEBEN CARGARSE
            //PARA EVITAR ESO SE USA LA VARIABLE DE CONTROL SOBRE QUE ESCENAS YA SE HAN CONSULTADO A LTM
            //SI EXISTEN ESCENAS EN MID-TERM QUE SON NUEVAS, HAY QUE CONSULTAR A LTM
            if (!midTermMemorySceneRelationsQueried.containsKey(sceneId)) {

                retrievedRelations.addAll(retrieveSceneRelationsLTM(sceneId, timeStamp));

                midTermMemorySceneRelationsQueried.put(sceneId, Boolean.TRUE);
            }

        } else {

            //SimpleLogger.log(this, "No hay relaciones en mid-term memory, buscando en long-term");
            retrievedRelations = retrieveSceneRelationsLTM(sceneId, timeStamp);

        }

        return retrievedRelations;

    }

    public void createStoragePaths() {

        File path = new File(STORAGE_PATH);

        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public ArrayList<SceneRelation> retrieveSceneRelationsLTM(int sceneId, long timeStamp) {

        ArrayList<SceneRelation> retrievedRelations = new ArrayList<>();

        try {
            String relationsFileName = STORAGE_PATH + "ca1_" + sceneId + ".txt";

            File relationsFile = new File(relationsFileName);

            if (!relationsFile.exists()) {

                createStoragePaths();

                relationsFile.createNewFile();
            }

            BufferedReader relationsBr = new BufferedReader(new FileReader(relationsFileName));
            String relationStr;

            while ((relationStr = relationsBr.readLine()) != null) {

                SceneRelation relation = new SceneRelation(relationStr);

                double elapsedTime = (timeStamp - relation.getTimeStamp());

                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, relation.getActivation());

                //AND THE SCENE HAS NOT DECAY
                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                    //INICIALIZA LAS POSICIONES DONDE SE AGREGARAN EL NODO Y SU RELACION SIMETRICA EN EL HASHMAP
                    //EJ. (1,2) Y (2,1)2
                    createIfNotExists(relation.getScene2Id());

                    relation.incrementRepetitions(0.0);
                    relation.setTimeStamp(timeStamp);

                    //SI YA EXISTE LA TUPLA DEBIDO A QUE LO VIO EL USUARIO ANTES DE RECUPERARLA NO SE AGREGA
                    //POR QUE YA ESTA CARGADA
                    if (!midTermMemorySceneRelations.get(relation.getScene2Id()).containsKey(relation.getScene1Id())) {
                        retrievedRelations.add(relation);
                    }

                    //SE AGREGA LA RELACION A MID-TERM MEMORY
                    midTermMemorySceneRelations.get(relation.getScene2Id()).put(relation.getScene1Id(), relation);

                }

            }

            relationsBr.close();

            return retrievedRelations;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retrievedRelations;
    }

    /**
     *
     * UPDATE A SCENE VALUE
     */
    public void reinforceRelation(SceneRelation updatedScene) {

        try {
            File file = new File(STORAGE_PATH + FILENAME + updatedScene.getScene2Id() + ".txt");

            if (file.exists()) {

                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                long pointer = raf.getFilePointer();
                String lineData = "";

                while ((lineData = raf.readLine()) != null) {
                    pointer = raf.getFilePointer() - lineData.length() - 2;

                    SceneRelation scene = new SceneRelation(lineData);

                    //ESTO SE DEBE OPTIMIZAR CON UN ARCHIVO DE BYTES Y BUSCANDO POR TAMAÑO DEL STRUCT E ID DE RELACION
                    //ASIGNAR UN AUTOINCREMENTABLE A LA RELACION, LUEGO USAR UN
                    //DATAOUPUTSTREAM ESCRIBIENDO BYTES DE TAMANOS FIJOS Y LUEGO CON UN RANDOMACCESS FILE
                    //RECUPERAR MULTIPLICANDO EL TAMANIO DE LOS BYTES POR EL DEL ID ASIGNADO Y ASI LEER DIRECTO
                    if (scene.getScene1Id() == updatedScene.getScene1Id()) {

                        System.out.println("Changing string in file " + file);

                        String replacingString = updatedScene.toString();

                        raf.seek(pointer);
                        raf.writeBytes(replacingString);

                        //if the replacingString has less number of characters than the matching string line then enter blank spaces.
                        if (replacingString.length() == lineData.length()) {

                            raf.seek(raf.getFilePointer() + 2);

                        } else if (replacingString.length() < lineData.length()) {

                            int difference = (lineData.length() - replacingString.length()) + 1;

                            for (int i = 0; i < difference; i++) {
                                raf.writeBytes(" ");
                            }

                            raf.writeBytes(System.getProperty("line.separator"));
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    public void load() {
        try {

            int relationsCount = 0;

            File file = new File(INDEX_FILE);

            storedKeys = new HashMap<>();

            if (file.exists() && midTermMemorySceneRelations.isEmpty()) {
                BufferedReader br = new BufferedReader(new FileReader(INDEX_FILE));

                String line;

                while ((line = br.readLine()) != null) {
                    String indexFile[] = line.split(",");

                    int index = Integer.parseInt(indexFile[0]);
                    String relationsFile = STORAGE_PATH + indexFile[1];

                    createIfNotExists(index);

                    BufferedReader relationsBr = new BufferedReader(new FileReader(relationsFile));
                    String relationStr;

                    while ((relationStr = relationsBr.readLine()) != null) {

                        SceneRelation sr = new SceneRelation(relationStr);

                        midTermMemorySceneRelations.get(index).put(sr.getScene1Id(), sr);

                        relationsCount++;
                    }

                    storedKeys.put(index, true);

                    relationsBr.close();
                }

                SimpleLogger.log(this, "Relaciones existentes en CA1 " + relationsCount);

            } else {
                SimpleLogger.log(this, "Archivo no existe, primera ejecucion");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        printGraph();

    }

    //
    public void persistRelations() {
        try {

            createStoragePaths();

            File previousIndexFile = new File(INDEX_FILE);

            if (!previousIndexFile.exists()) {
                previousIndexFile.createNewFile();
            }

            BufferedReader previousIndexFileReader = new BufferedReader(new FileReader(previousIndexFile));

            File updatedIndexFile = new File(STORAGE_PATH + "index_temp.txt");
            PrintWriter updatedIndexFileWriter = new PrintWriter(new FileWriter(updatedIndexFile));

            SortedSet<Integer> relationsIDs = new TreeSet<>(midTermMemorySceneRelations.keySet());

            String indexLineString = null;

            //TRASPASA LOS INDICES YA EXISTENTES A UN NUEVO ARCHIVO 
            while ((indexLineString = previousIndexFileReader.readLine()) != null) {

                String indexParts[] = indexLineString.split(",");

                int index = Integer.parseInt(indexParts[0]);

                if (relationsIDs.contains(index)) {
                    relationsIDs.remove(index);
                }

                updatedIndexFileWriter.println(index + "," + indexParts[1]);
                updatedIndexFileWriter.flush();

            }

            //LOS NUEVOS INDICES QUE SON LOS QUE NO SE ELIMINARON DE relationsIDs SE AGREGAN AL FINAL Y SE CREA SU ARCHIVO DE RELACIONES VACIO
            for (Integer relationID : relationsIDs) {

                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");
                newRelationFile.close();

                updatedIndexFileWriter.println(relationID + "," + FILENAME + relationID + ".txt");
                updatedIndexFileWriter.flush();
            }

            updatedIndexFileWriter.close();
            previousIndexFileReader.close();

            //REEMPLAZA EL ARCHIVO DE INDICES
            if (previousIndexFile.delete()) {
                if (!updatedIndexFile.renameTo(previousIndexFile)) {
                    SimpleLogger.log(this, "Error on updating file");
                }
            } else {
                SimpleLogger.log(this, "Error on updating file");
            }

            //ACTUALIZA LOS ARCHIVOS DE RELACIONES
            HashMap<Integer, ConcurrentHashMap<Integer, SceneRelation>> relationsToUpdate = new HashMap<>();
            relationsToUpdate.putAll(midTermMemorySceneRelations);

            relationsIDs = new TreeSet<>(relationsToUpdate.keySet());

            //SE PROCEDE A ACTUALIZAR TODAS LAS RELACIONES QUE FUERON MODIFICADAS
            for (Integer relationID : relationsIDs) {

                //LEEMOS EL CONTENIDO DEL ARCHIVO ORIGINAL
                BufferedReader originalRelationsReader = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME + relationID + ".txt"));
                String relationStr;

                //SE CARGAN TODAS LAS ESCENAS QUE NO ESTEN EN MID-TERM PARA QUE SE GENERE UN NUEVO ARCHIVO
                //ES UN PROCESO PESADO DE LECTURA DE ARCHIVOS PERO NO SE PUEDE HACER DE OTRA FORMA SIN UNA BASE DE DATOS COMO SQLITE
                while ((relationStr = originalRelationsReader.readLine()) != null) {

                    SceneRelation sceneRelation = new SceneRelation(relationStr);

                    int sceneId = sceneRelation.getScene2Id();

                    //SI NO LO CONTIENE LO CARGA PARA REEMPLAZAR EL ARCHIVO
                    //SI SI LO CONTIENE, LO IGNORA PORQUE YA ESTA CARGADO Y ACTUALIZADO
                    if (!relationsToUpdate.get(sceneId).containsKey(sceneRelation.getScene1Id())) {
                        relationsToUpdate.get(sceneId).put(sceneRelation.getScene1Id(), sceneRelation);
                    }

                }

                originalRelationsReader.close();

                //SE SUSTITUYE EL ARCHIVO DE RELACIONES CON LAS NUEVAS
                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");

                SortedSet<Integer> destinationIDs = new TreeSet<>(relationsToUpdate.get(relationID).keySet());

                for (Integer destinationID : destinationIDs) {

                    SceneRelation newRelation = relationsToUpdate.get(relationID).get(destinationID);

                    //SI TIENE LA ACTIVACION SUFICIENTE SE ALMACENA
                    if (newRelation.getActivation() >= Configuration.CONSOLIDATION_THRESHOLD) {

                        newRelationFile.println(newRelation.toString());
                        newRelationFile.flush();

                    }

                }

                newRelationFile.close();

            }
            
            countMtm();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void countMtm() {
        int total = 0;
        for (Integer vertex : midTermMemorySceneRelations.keySet()) {

            ConcurrentHashMap<Integer, SceneRelation> relations = midTermMemorySceneRelations.get(vertex);

            for (SceneRelation sr : relations.values()) {
                total++;
            }

        }

        System.out.println("total SC relations: " + total);
    }

    public void printGraph() {

        for (Integer vertex : midTermMemorySceneRelations.keySet()) {

            ConcurrentHashMap<Integer, SceneRelation> relations = midTermMemorySceneRelations.get(vertex);

            System.out.println("Vertex " + vertex);

            for (SceneRelation sr : relations.values()) {
                System.out.println(sr.toString());
            }

        }

    }

}
