/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.storage;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;
import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.core.configuration.Configuration.*;
import episodicv2.core.storage.ActivationFunctions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author karenlima
 */
public class CA1StorageHandlerCodelet extends Codelet {
    
    
    private static final String INDEX_FILE = STORAGE_BASE_PATH + "/ca1/index.txt";
    private static final String STORAGE_PATH = STORAGE_BASE_PATH + "/ca1/";
    private static final String FILENAME = CA1_STORAGE_BASEFILENAME;
    
    private static Map<Integer, ConcurrentHashMap<Integer, Idea>> midTermMemorySceneRelations = new ConcurrentHashMap<>();
    private static Map<Integer, Boolean> midTermMemorySceneRelationsQueried = new ConcurrentHashMap<>();
    private static Map<Integer, Boolean> storedKeys = new ConcurrentHashMap<>();
    private Timer consolidationTimer;
    private static CA1StorageHandlerCodelet instance = null;
    private long startTime;
    
    MemoryObject rootInputMO;
    Idea rootInputIdea;

    MemoryObject sceneRelationVertexToStoreMO;
    Idea sceneRelationVertexToStoreIdea;
    
    MemoryObject rootOutputMO;
    Idea rootOutputIdea;
    
    public CA1StorageHandlerCodelet() {
        try {
            setThreshold(0.5);
        } catch(CodeletThresholdBoundsException  ex){
            System.out.println("Threshold Bounds exception");
        }
        startTime = System.currentTimeMillis();
        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());
    }
    
    public CA1StorageHandlerCodelet(boolean load) {

        if (load) {
            load();
        }

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());
    }
    
    public static CA1StorageHandlerCodelet getInstance() {

        if (instance == null) {
            instance = new CA1StorageHandlerCodelet();
        }

        return instance;
    }

    public static CA1StorageHandlerCodelet getInstance(boolean load) {
        if (instance == null) {
            instance = new CA1StorageHandlerCodelet(load);
        }
        return instance;
    }
    
    @Override
    public void accessMemoryObjects() {
        rootInputMO = (MemoryObject) getInput(ROOT_MO);
        sceneRelationVertexToStoreMO = (MemoryObject) getInput(SCENE_RELATION_VERTEX_TO_STORE_MO);
        rootOutputMO = (MemoryObject) getOutput(ROOT_MO);
        try {
            rootInputIdea = (Idea) rootInputMO.getI();
            sceneRelationVertexToStoreIdea = (Idea) sceneRelationVertexToStoreMO.getI();
            rootOutputIdea = (Idea) rootOutputMO.getI();
        } catch (NullPointerException ex) {
        }
    }
    
    @Override
    public void proc() {
        if (sceneRelationVertexToStoreIdea != null && sceneRelationVertexToStoreIdea.get(SCENE_RELATION_IDEA) != null) {
            double affectIntensity = (double) sceneRelationVertexToStoreIdea.get(AFFECT_INTENSITY_IDEA).getValue();
            addVertex(sceneRelationVertexToStoreIdea.get(SCENE_RELATION_IDEA), affectIntensity);
            persistRelations();
            saveRelationsOnRoot();
        }
    }
    
    @Override
    public void calculateActivation(){
        Double activationValue = 0.0;
        
        if(Boolean.TRUE.equals(hasTimerExpired())) {
            activationValue = 1.0;
            startTime = System.currentTimeMillis();
            System.out.println("Timer expired! Executing CA1 Storage");
        } 
        try {
            setActivation(activationValue);

        } catch(CodeletActivationBoundsException  ex){
            System.out.println("Activation Bounds exception");
        }
    }
    
    private Boolean hasTimerExpired() {
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime >= CONSOLIDATION_INTERVAL;
    }
    
    private void saveRelationsOnRoot(){
        
    }
    /**
     * method persistRelations:
    store at rootMO
    verify if there is data
    scenes to update is the midTermScenesByID
    from each previous data verify if has to update, 
    * if yes, replace with new data, 
    * else, just save again
    if still have scenes to update, save them
    save DGSize     
    **/
    public void persistRelations() {
        try {
            System.out.println("CA1StorageHandler 1 ");
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
            System.out.println("CA1StorageHandler 2 ");
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
            System.out.println("CA1StorageHandler 3 ");
            

            //LOS NUEVOS INDICES QUE SON LOS QUE NO SE ELIMINARON DE relationsIDs SE AGREGAN AL FINAL Y SE CREA SU ARCHIVO DE RELACIONES VACIO
            for (Integer relationID : relationsIDs) {

                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");
                newRelationFile.close();

                updatedIndexFileWriter.println(relationID + "," + FILENAME + relationID + ".txt");
                updatedIndexFileWriter.flush();
            }

            updatedIndexFileWriter.close();
            previousIndexFileReader.close();
            System.out.println("CA1StorageHandler 4 ");
            //REEMPLAZA EL ARCHIVO DE INDICES
            if (previousIndexFile.delete()) {
                if (!updatedIndexFile.renameTo(previousIndexFile)) {
                    System.out.println("Error on updating file");
                }
            } else {
                System.out.println("Error on updating file");
            }

            //ACTUALIZA LOS ARCHIVOS DE RELACIONES
            HashMap<Integer, ConcurrentHashMap<Integer, Idea>> relationsToUpdate = new HashMap<>();
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

//                    SceneRelation sceneRelation = new SceneRelation(relationStr);
                    Idea sceneRelation = storedSceneRelationToIdea(relationStr);

//                    int sceneId2 = sceneRelation.getScene2Id();
                    int sceneId2 = (int) sceneRelation.get(SCENE_ID_2_IDEA).getValue();
                    int sceneId1 = (int) sceneRelation.get(SCENE_ID_1_IDEA).getValue();

                    //SI NO LO CONTIENE LO CARGA PARA REEMPLAZAR EL ARCHIVO
                    //SI SI LO CONTIENE, LO IGNORA PORQUE YA ESTA CARGADO Y ACTUALIZADO
                    if (!relationsToUpdate.get(sceneId2).containsKey(sceneId1)) {
                        relationsToUpdate.get(sceneId2).put(sceneId1, sceneRelation);
                    }

                }

                originalRelationsReader.close();

                //SE SUSTITUYE EL ARCHIVO DE RELACIONES CON LAS NUEVAS
                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");

                SortedSet<Integer> destinationIDs = new TreeSet<>(relationsToUpdate.get(relationID).keySet());

                for (Integer destinationID : destinationIDs) {

                    Idea newRelation = relationsToUpdate.get(relationID).get(destinationID);
                    double newRelationActivation = (double) newRelation.get(ACTIVATION_IDEA).getValue();

                    //SI TIENE LA ACTIVACION SUFICIENTE SE ALMACENA
                    if (newRelationActivation >= CONSOLIDATION_THRESHOLD) {

                        newRelationFile.println(newRelation.toString());
                        newRelationFile.flush();

                    }

                }

                newRelationFile.close();

            }
            System.out.println("CA1StorageHandler 5 ");
            countMtm();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void countMtm() {
        int total = 0;
        for (Integer vertex : midTermMemorySceneRelations.keySet()) {

            ConcurrentHashMap<Integer, Idea> relations = midTermMemorySceneRelations.get(vertex);

            for (Idea sr : relations.values()) {
                total++;
            }

        }

        System.out.println("total SC relations: " + total);
    }

    public void printGraph() {

        for (Integer vertex : midTermMemorySceneRelations.keySet()) {

            ConcurrentHashMap<Integer, Idea> relations = midTermMemorySceneRelations.get(vertex);

            System.out.println("Vertex " + vertex);

            for (Idea sr : relations.values()) {
                System.out.println(sr.toString());
            }

        }

    }


    public void addVertex(Idea edge, double affectIntensity) {
        System.out.println("Adding vertex");
        int source = (int) edge.get(SCENE_ID_1_IDEA).getValue();
        int destination = (int) edge.get(SCENE_ID_2_IDEA).getValue();
        System.out.println("source id: "+source+"destination id: "+destination);
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

        ConcurrentHashMap<Integer, Idea> sourceRelations = midTermMemorySceneRelations.get(source);
        ConcurrentHashMap<Integer, Idea> destinationRelations = midTermMemorySceneRelations.get(destination);

        Idea sourceRelation = sourceRelations.get(destination);
        Idea destinationRelation = destinationRelations.get(source);

        if (sourceRelation == null) {
            int time = (int) edge.get(TIME_IDEA).getValue();
            sourceRelations.put(destination, createSceneRelationIdea(destination, source, time));
        } else {
            int time = (int) edge.get(TIME_IDEA).getValue();
            sourceRelation = incrementRepetitions(sourceRelation, affectIntensity, time, timeStamp, true);
        }

        if (destinationRelation == null) {
            int time = (int) edge.get(TIME_IDEA).getValue();
            destinationRelations.put(source, createSceneRelationIdea(source, destination, time));
        } else {
            int time = (int) edge.get(TIME_IDEA).getValue();
            destinationRelation = incrementRepetitions(destinationRelation, affectIntensity, time, timeStamp, true);
        }
        System.out.println("Added in vertex");
    }

    private void createIfNotExists(int key) {
        if (!midTermMemorySceneRelations.containsKey(key)) {
            midTermMemorySceneRelations.put(key, new ConcurrentHashMap<Integer, Idea>());
        }
        System.out.print("Create if not exist");
        printMapDetails(midTermMemorySceneRelations);
    }
    
    public static void printMapDetails(Map<Integer, ConcurrentHashMap<Integer, Idea>> map) {
        // Tamanho do mapa externo
        int outerMapSize = map.size();
        System.out.println("Tamanho do mapa externo: " + outerMapSize);

        // Número total de entradas nos mapas internos
        int totalEntries = 0;
        for (Map.Entry<Integer, ConcurrentHashMap<Integer, Idea>> entry : map.entrySet()) {
            totalEntries += entry.getValue().size();
        }
        System.out.println("Número total de entradas nos mapas internos: " + totalEntries);

        // Imprimir conteúdo dos mapas
        for (Map.Entry<Integer, ConcurrentHashMap<Integer, Idea>> entry : map.entrySet()) {
            int outerKey = entry.getKey();
            ConcurrentHashMap<Integer, Idea> innerMap = entry.getValue();
            System.out.println("Chave externa: " + outerKey);
            for (Map.Entry<Integer, Idea> innerEntry : innerMap.entrySet()) {
                System.out.println("  Chave interna: " + innerEntry.getKey() + ", Valor: " + innerEntry.getValue());
            }
        }
    }

    public ArrayList<Idea> retrieveSceneRelations(int sceneId, long timeStamp) {

        ConcurrentHashMap<Integer, Idea> sourceRelations = midTermMemorySceneRelations.get(sceneId);

        ArrayList<Idea> retrievedRelations = new ArrayList<>();

        if (sourceRelations != null) {

            //CAMBIAR A ITERATOR POR PROBLEMAS DE CONCURRENCIA
            for (Idea relation : sourceRelations.values()) {
                
                double relationTimestamp = (double) relation.get(TIMESTAMP_IDEA).getValue();

                double elapsedTime = (timeStamp - relationTimestamp);

                elapsedTime = elapsedTime / (RETRIEVABILITY_DECREASING_SCALE); //en dias
                double activation = (double) relation.get(ACTIVATION_IDEA).getValue();
                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, activation);

                //AND THE SCENE HAS NOT DECAY
                if (retrievability >= RETRIEVABILITY_THRESHOLD) {
                    relation = incrementRepetitions(relation, 0.0, timeStamp);

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
    
    private double calculateNewActivation(double affectIntensity, double activation, int repetitions){
        
        double scale = SIGMOID_SCALE;
        double affect = affectIntensity;
        double alpha = ALPHA_MEMORY_INC_RELEVANCE;
        double beta = BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, affect, alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);

        activation = (activation + currentActivation) / 2.0;
        return activation;
    }
    
    public Idea incrementRepetitions(Idea idea, double affectIntensity, long timeStamp) {
        int time = (int) idea.get(TIME_IDEA).getValue();
        boolean updated = (boolean) idea.get(UPDATED_IDEA).getValue();
        return incrementRepetitions(idea, affectIntensity, time, timeStamp, updated);
    }
    
    private Idea incrementRepetitions(Idea idea, double affectIntensity, int time, long timeStamp, boolean updated) {
        int scene1Id = (int) idea.get(SCENE_ID_1_IDEA).getValue();
        int scene2Id = (int) idea.get(SCENE_ID_2_IDEA).getValue();
        int repetitions = (int) idea.get(REPETITIONS_IDEA).getValue();
        boolean recent = (boolean) idea.get(RECENT_IDEA).getValue();
        double activation = (double) idea.get(ACTIVATION_IDEA).getValue();
        return createCompleteSceneRelationIdea(scene1Id, scene2Id, calculateNewActivation(affectIntensity, activation, repetitions), repetitions + 1, time, timeStamp, recent, updated);
    }
    
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

                        Idea sr = storedSceneRelationToIdea(relationStr);
                        int scene1Id = (int) sr.get(ID_IDEA).getValue();

                        midTermMemorySceneRelations.get(index).put(scene1Id, sr);

                        relationsCount++;
                    }

                    storedKeys.put(index, true);

                    relationsBr.close();
                }

                System.out.println("Relaciones existentes en CA1 " + relationsCount);

            } else {
                System.out.println("Archivo no existe, primera ejecucion");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        printGraph();

    }
    
    public void createStoragePaths() {

        File path = new File(STORAGE_PATH);

        if (!path.exists()) {
            path.mkdirs();
        }
    }
    
    
    public ArrayList<Idea> retrieveSceneRelationsLTM(int sceneId, long timeStamp) {

        ArrayList<Idea> retrievedRelations = new ArrayList<>();

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

                Idea relation = storedSceneRelationToIdea(relationStr);
                double relationTimestamp = (double) relation.get(TIMESTAMP_IDEA).getValue();

                double elapsedTime = (timeStamp - relationTimestamp);

                elapsedTime = elapsedTime / (RETRIEVABILITY_DECREASING_SCALE); //en dias
                
                double relationActivation = (double) relation.get(ACTIVATION_IDEA).getValue();

                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, relationActivation);

                //AND THE SCENE HAS NOT DECAY
                if (retrievability >= RETRIEVABILITY_THRESHOLD) {

                    //INICIALIZA LAS POSICIONES DONDE SE AGREGARAN EL NODO Y SU RELACION SIMETRICA EN EL HASHMAP
                    //EJ. (1,2) Y (2,1)2
                    int scene2Id = (int) relation.get(SCENE_ID_2_IDEA).getValue();
                    int scene1Id = (int) relation.get(SCENE_ID_1_IDEA).getValue();
                    createIfNotExists(scene2Id);

                    relation = incrementRepetitions(relation, 0.0, timeStamp);

                    //SI YA EXISTE LA TUPLA DEBIDO A QUE LO VIO EL USUARIO ANTES DE RECUPERARLA NO SE AGREGA
                    //POR QUE YA ESTA CARGADA
                    if (!midTermMemorySceneRelations.get(scene2Id).containsKey(scene1Id)) {
                        retrievedRelations.add(relation);
                    }

                    //SE AGREGA LA RELACION A MID-TERM MEMORY
                    midTermMemorySceneRelations.get(scene2Id).put(scene1Id, relation);

                }

            }

            relationsBr.close();

            return retrievedRelations;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retrievedRelations;
    }
    
    private Idea storedSceneRelationToIdea(String storedRelationString) {
        String data[] = storedRelationString.split(",");
        return createCompleteSceneRelationIdea(
                Integer.parseInt(data[0]), 
                Integer.parseInt(data[1]), 
                Double.parseDouble(data[3]),
                Integer.parseInt(data[2]),
                Integer.parseInt(data[4]),
                Integer.parseInt(data[5]),
                false,
                false);        
    }
    
    public Idea createSceneRelationIdea(int scene1Id, int scene2Id, int time) {
        return createCompleteSceneRelationIdea(scene1Id, scene2Id, activation, 0, time, System.currentTimeMillis(), true, false);
    }
    
    private Idea createCompleteSceneRelationIdea(int scene1Id, int scene2Id, double activation, int repetitions, int time, long timeStamp, boolean recent, boolean updated){
        Idea storedSceneRelationIdea = new Idea(STORED_SCENE_RELATION_IDEA, null, CATEGORY_PROPERTY, 1);        
        Idea id1Idea = new Idea(SCENE_ID_1_IDEA, scene1Id, CATEGORY_PROPERTY, 1);
        Idea id2Idea = new Idea(SCENE_ID_2_IDEA, scene2Id, CATEGORY_PROPERTY, 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, activation, CATEGORY_PROPERTY, 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA,repetitions, CATEGORY_PROPERTY, 1);
        Idea timeIdea = new Idea(TIME_IDEA,time, CATEGORY_PROPERTY, 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA,timeStamp, CATEGORY_PROPERTY, 1);
        Idea recentIdea = new Idea(RECENT_IDEA,recent, CATEGORY_PROPERTY, 1);
        Idea updatedIdea = new Idea(UPDATED_IDEA,updated, CATEGORY_PROPERTY, 1);
        
        storedSceneRelationIdea.add(id1Idea);
        storedSceneRelationIdea.add(id2Idea);
        storedSceneRelationIdea.add(repetitionsIdea);
        storedSceneRelationIdea.add(activationIdea);
        storedSceneRelationIdea.add(timeIdea);
        storedSceneRelationIdea.add(timestampIdea);
        storedSceneRelationIdea.add(recentIdea);
        storedSceneRelationIdea.add(updatedIdea);
            
        return storedSceneRelationIdea;
    }

}
