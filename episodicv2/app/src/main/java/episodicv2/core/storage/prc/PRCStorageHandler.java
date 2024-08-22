/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage.prc;

import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.ObjectRelation;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.storage.ActivationFunctions;
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

/**
 *
 * @author luis_
 */
public class PRCStorageHandler {

    private static final String INDEX_FILE = Configuration.STORAGE_BASE_PATH + "/prc/index.txt";
    private static final String STORAGE_PATH = Configuration.STORAGE_BASE_PATH + "/prc/";
    private static final String FILENAME = Configuration.PRC_STORAGE_BASEFILENAME;

    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;
    private static final double RETRIEVAL_SIMILARITY_THRESHOLD = Configuration.RETRIEVAL_SIMILARITY_THRESHOLD;

    private static Map<Integer, ConcurrentHashMap<Integer, ObjectRelation>> midTermMemoryObjectRelations;
    private static Map<Integer, Boolean> midTermMemoryObjectRelationsQueried;
    private static Map<Integer, Boolean> storedKeys;

    private static PRCStorageHandler instance = null;

    private Timer consolidationTimer;

    private PRCStorageHandler() {

        midTermMemoryObjectRelations = new ConcurrentHashMap<>();
        midTermMemoryObjectRelationsQueried = new ConcurrentHashMap<>();
        storedKeys = new ConcurrentHashMap<>();

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();

    }

    private PRCStorageHandler(boolean load) {

        midTermMemoryObjectRelations = new ConcurrentHashMap<>();
        midTermMemoryObjectRelationsQueried = new ConcurrentHashMap<>();
        storedKeys = new ConcurrentHashMap<>();

        if (load) {
            load();
        }

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();
    }

    public static PRCStorageHandler getInstance() {

        if (instance == null) {
            instance = new PRCStorageHandler();
        }

        return instance;
    }

    public static PRCStorageHandler getInstance(boolean load) {
        if (instance == null) {
            instance = new PRCStorageHandler(load);
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
                System.out.println("Storing...");
                persistRelations();
            }
        }, delay, delay);
    }

    /**
     * *
     * Add
     *
     * @param relations
     * @param affectIntensity
     */
    public void addVertex(ArrayList<ObjectRelation> relations, double affectIntensity) {
        for (ObjectRelation or : relations) {
            addVertex(or, affectIntensity);
        }
    }

    public void addVertex(ObjectRelation edge, double affectIntensity) {

        int source = edge.getObject1Id();
        int destination = edge.getObject2Id();
        long timeStamp = System.currentTimeMillis();

        //No se guardan las relaciones incompletas
        if (source == 0 || destination == 0) {
            return;
        }

        createIfNotExists(source);
        createIfNotExists(destination);

        ConcurrentHashMap<Integer, ObjectRelation> sourceRelations = midTermMemoryObjectRelations.get(source);
        ConcurrentHashMap<Integer, ObjectRelation> destinationRelations = midTermMemoryObjectRelations.get(destination);

        ObjectRelation sourceRelation = sourceRelations.get(destination);
        ObjectRelation destinationRelation = destinationRelations.get(source);

        if (sourceRelation == null) {
            sourceRelations.put(destination, new ObjectRelation(destination, source, edge.getTime()));
        } else {
            sourceRelation.incrementRepetitions(affectIntensity);
            sourceRelation.setTime(edge.getTime());
            sourceRelation.setTimeStamp(timeStamp);
            sourceRelation.setUpdated(true);

            //reinforceRelation(sourceRelation);
        }

        if (destinationRelation == null) {
            destinationRelations.put(source, new ObjectRelation(source, destination, edge.getTime()));
        } else {
            destinationRelation.incrementRepetitions(affectIntensity);
            destinationRelation.setTime(edge.getTime());
            destinationRelation.setTimeStamp(timeStamp);
            destinationRelation.setUpdated(true);

            //reinforceRelation(destinationRelation);
        }

    }

    //Retrieve
    public ObjectRelations retrieveObjectRelations(int objectId, double affect, long timeStamp) {

        ObjectRelations relations;

        ConcurrentHashMap<Integer, ObjectRelation> sourceRelations = midTermMemoryObjectRelations.get(objectId);

        ArrayList<ObjectRelation> retrievedRelations = new ArrayList<>();
        ArrayList<Integer> retrievedRelationsClasses = new ArrayList<>();

        if (sourceRelations != null) {

            for (ObjectRelation relation : sourceRelations.values()) {

                double elapsedTime = (timeStamp - relation.getTimeStamp());

                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, relation.getActivation());

                //AND THE OBJ RELATION HAS NOT DECAY
                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                    relation.incrementRepetitions(affect);
                    relation.setTimeStamp(timeStamp);

                    retrievedRelations.add(relation);
                    retrievedRelationsClasses.add(relation.getObject1Id());

                    //Relacion simetrica
                    //ObjectRelation simetricRelation = adjancencyList.get(relation.getObject1Id()).get(objectId);
                    //simetricRelation.incrementRepetitions();
                    //simetricRelation.setTimeStamp(timeStamp);
                }

            }

            relations = new ObjectRelations(objectId, retrievedRelationsClasses);

            //DEBIDO A QUE LAS NUEVAS RELACIONES CREADAS Y SU RELACION SIMETRICA SE AGREGAN A MID-TERM MEMORY
            //ESTO PUEDE EVITAR QUE SE REALICE EL QUERY A LTM A PESAR DE AUN NO SE HAN CARGADO LAS MEMORIAS QUE SI DEBEN CARGARSE
            //PARA EVITAR ESO SE USA LA VARIABLE DE CONTROL SOBRE QUE ESCENAS YA SE HAN CONSULTADO A LTM
            //SI EXISTEN ESCENAS EN MID-TERM QUE SON NUEVAS, HAY QUE CONSULTAR A LTM
            if (!midTermMemoryObjectRelationsQueried.containsKey(objectId)) {

                ObjectRelations tmpRelations = retrieveObjectRelationsLTM(objectId, affect, timeStamp);

                retrievedRelationsClasses.addAll(tmpRelations.getRelations());

                relations = new ObjectRelations(objectId, retrievedRelationsClasses);

                midTermMemoryObjectRelationsQueried.put(objectId, Boolean.TRUE);
            }

        } else {

            //SimpleLogger.log(this, "No hay relaciones en mid-term memory, buscando en long-term");
            relations = retrieveObjectRelationsLTM(objectId, affect, timeStamp);

        }

        return relations;

    }

    //SEARCH IN LTM
    public ObjectRelations retrieveObjectRelationsLTM(int objectId, double affect, long timeStamp) {
        try {

            ArrayList<ObjectRelation> retrievedRelations = new ArrayList<>();
            ArrayList<Integer> retrievedRelationsClasses = new ArrayList<>();

            String relationsFile = STORAGE_PATH + "prc_" + objectId + ".txt";

            File relationFile = new File(relationsFile);

            if (!relationFile.exists()) {
                createStoragePaths();
                return new ObjectRelations(objectId);
            }

            BufferedReader relationsBr = new BufferedReader(new FileReader(relationsFile));
            String relationStr;

            while ((relationStr = relationsBr.readLine()) != null) {

                ObjectRelation relation = new ObjectRelation(relationStr);

                double elapsedTime = (timeStamp - relation.getTimeStamp());

                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, relation.getActivation());

                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                    //INICIALIZA LAS POSICIONES DONDE SE AGREGARAN EL NODO Y SU RELACION
                    createIfNotExists(relation.getObject2Id());

                    relation.incrementRepetitions(affect);
                    relation.setTimeStamp(timeStamp);

                    //SI YA EXISTE LA TUPLA DEBIDO A QUE LO VIO EL USUARIO ANTES DE RECUPERARLA NO SE AGREGA
                    //POR QUE YA ESTA CARGADA
                    if (!midTermMemoryObjectRelations.get(relation.getObject2Id()).containsKey(relation.getObject1Id())) {
                        retrievedRelations.add(relation);
                        retrievedRelationsClasses.add(relation.getObject1Id());
                    }

                    //SE AGREGA LA RELACION A MID-TERM MEMORY
                    midTermMemoryObjectRelations.get(relation.getObject2Id()).put(relation.getObject1Id(), relation);

                }

            }

            relationsBr.close();

            ObjectRelations relations = new ObjectRelations(objectId, retrievedRelationsClasses);

            return relations;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ObjectRelations(objectId, new ArrayList<>());
    }

    //
    public void load() {
        try {

            int relationsCount = 0;
            File file = new File(INDEX_FILE);

            storedKeys = new HashMap<>();

            if (file.exists() && midTermMemoryObjectRelations.isEmpty()) {
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

                        ObjectRelation sr = new ObjectRelation(relationStr);

                        midTermMemoryObjectRelations.get(index).put(sr.getObject1Id(), sr);

                        relationsCount++;

                    }

                    storedKeys.put(index, true);

                    relationsBr.close();
                }

                System.out.println("Relaciones existentes en PRC " + relationsCount);

            } else {
                System.out.println("Archivo no existe, primera ejecucion");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //printGraph();
    }

    //
    public void createStoragePaths() {

        File path = new File(STORAGE_PATH);

        if (!path.exists()) {
            path.mkdirs();
        }
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

            SortedSet<Integer> relationsIDs = new TreeSet<>(midTermMemoryObjectRelations.keySet());

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
                    System.out.println("Error on updating file");
                }
            } else {
                System.out.println("Error on updating file");
            }

            //ACTUALIZA LOS ARCHIVOS DE RELACIONES
            HashMap<Integer, ConcurrentHashMap<Integer, ObjectRelation>> relationsToUpdate = new HashMap<>();
            relationsToUpdate.putAll(midTermMemoryObjectRelations);

            relationsIDs = new TreeSet<>(relationsToUpdate.keySet());

            //SE PROCEDE A ACTUALIZAR TODAS LAS RELACIONES QUE FUERON MODIFICADAS
            for (Integer relationID : relationsIDs) {

                //LEEMOS EL CONTENIDO DEL ARCHIVO ORIGINAL
                BufferedReader originalRelationsReader = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME + relationID + ".txt"));
                String relationStr;

                //SE CARGAN TODAS LAS ESCENAS QUE NO ESTEN EN MID-TERM PARA QUE SE GENERE UN NUEVO ARCHIVO
                //ES UN PROCESO PESADO DE LECTURA DE ARCHIVOS PERO NO SE PUEDE HACER DE OTRA FORMA SIN UNA BASE DE DATOS COMO SQLITE
                while ((relationStr = originalRelationsReader.readLine()) != null) {

                    ObjectRelation sceneRelation = new ObjectRelation(relationStr);

                    int objectId = sceneRelation.getObject2Id();

                    //SI NO LO CONTIENE LO CARGA PARA REEMPLAZAR EL ARCHIVO
                    //SI SI LO CONTIENE, LO IGNORA PORQUE YA ESTA CARGADO Y ACTUALIZADO
                    if (!relationsToUpdate.get(objectId).containsKey(sceneRelation.getObject1Id())) {
                        relationsToUpdate.get(objectId).put(sceneRelation.getObject1Id(), sceneRelation);
                    }

                }

                originalRelationsReader.close();

                //SE SUSTITUYE EL ARCHIVO DE RELACIONES CON LAS NUEVAS
                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");

                SortedSet<Integer> destinationIDs = new TreeSet<>(relationsToUpdate.get(relationID).keySet());

                for (Integer destinationID : destinationIDs) {

                    ObjectRelation newRelation = relationsToUpdate.get(relationID).get(destinationID);

                    //SI ESTA MUY ACTIVA SI SE PERSISTE
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

    private void createIfNotExists(int key) {
        if (!midTermMemoryObjectRelations.containsKey(key)) {
            midTermMemoryObjectRelations.put(key, new ConcurrentHashMap<Integer, ObjectRelation>());
        }
    }

    public void countMtm() {
        int total = 0;
        for (Integer vertex : midTermMemoryObjectRelations.keySet()) {

            ConcurrentHashMap<Integer, ObjectRelation> relations = midTermMemoryObjectRelations.get(vertex);


            for (ObjectRelation sr : relations.values()) {
                total++;
            }

        }
        
        System.out.println("total OBJ relations: "+total);
    }

    public void printGraph() {

        for (Integer vertex : midTermMemoryObjectRelations.keySet()) {

            ConcurrentHashMap<Integer, ObjectRelation> relations = midTermMemoryObjectRelations.get(vertex);

            System.out.println("Vertex " + vertex);

            for (ObjectRelation sr : relations.values()) {
                System.out.println(sr.toString());
            }

        }

    }
}
