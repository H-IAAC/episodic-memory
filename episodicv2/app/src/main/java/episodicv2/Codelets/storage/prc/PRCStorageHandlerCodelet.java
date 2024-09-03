///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package episodicv2.Codelets.storage.prc;
//
//import br.unicamp.cst.core.entities.Codelet;
//import br.unicamp.cst.core.entities.MemoryObject;
//import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
//import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;
//import br.unicamp.cst.representation.idea.Idea;
//import episodicv2.core.configuration.Configuration;
//import static episodicv2.core.configuration.Configuration.*;
//import episodicv2.core.storage.ActivationFunctions;
//import episodicv2.emotions.EmotionalDecay;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.SortedSet;
//import java.util.TreeSet;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// *
// * @author karenlima
// */
//public class PRCStorageHandlerCodelet extends Codelet {
//    
//    MemoryObject rootMO;
//    Idea rootIdea;
//
//    MemoryObject pRCMidTermMemoryObjectRelationsMO;
//    Idea pRCMidTermMemoryObjectRelationsIdea;
//    
//    MemoryObject requestObjectRelationMO;
//    Idea requestObjectRelationIdea;
//    
//    MemoryObject retrievedObjectRelationsMO;
//    Idea objectRelationsIdea;
//    
//    MemoryObject rootOutputMO;
//    Idea rootOutputIdea;
//    
//    Map<Integer, ConcurrentHashMap<Integer, Idea>> midTermMemoryObjectRelations;
//    private static Map<Integer, Boolean> midTermMemoryObjectRelationsQueried;
//    
//    Map<Integer, Idea> midTermMemoryObjectRelationsById;
//    
//    Map<Integer, ConcurrentHashMap<Integer, Idea>> fileContent = null;
//    
//    Integer savedTimes = 0;
//    
//    Integer currentFrameSaved = 0;
//
//    private static final String FILENAME = Configuration.PRC_STORAGE_BASEFILENAME;
//    private static final String INDEX_FILE = Configuration.STORAGE_BASE_PATH + "/prc/index.txt";
//    private static final String STORAGE_PATH = Configuration.STORAGE_BASE_PATH + "/prc/";
//    
//    private long startTime;
//    private double affectIntensity = 0.0;
//    private EmotionalDecay emotionalDecay = null;
//    
//    public PRCStorageHandlerCodelet() {
//        try {
//            setThreshold(0.5);
//            
//        } catch(CodeletThresholdBoundsException  ex){
//            System.out.println("Threshold Bounds exception");
//        }
//        
//        startTime = System.currentTimeMillis();
//        if (emotionalDecay == null) {
//            emotionalDecay = new EmotionalDecay(this.getClass().getName());
//            emotionalDecay.start();
//        }
//        
//    }
//
//    @Override
//    public void accessMemoryObjects() {
//        
//        rootMO = (MemoryObject) getInput(ROOT_MO);
//        pRCMidTermMemoryObjectRelationsMO = (MemoryObject) getInput(PRC_MID_TERM_OBJECT_RELATIONS_MO);
//        rootOutputMO = (MemoryObject) getOutput(ROOT_MO);
//        pRCMidTermMemoryObjectRelationsIdea = (Idea) pRCMidTermMemoryObjectRelationsMO.getI();
//        
//        requestObjectRelationMO = (MemoryObject) getInput(REQUEST_OBJECT_RELATION_MO);
//        requestObjectRelationIdea = (Idea) requestObjectRelationMO.getI();
//                
//        try {
//            rootIdea = (Idea) rootMO.getI();
//            rootOutputIdea = (Idea) rootOutputMO.getI();
//            
//            fileContent = (Map<Integer, ConcurrentHashMap<Integer, Idea>>) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS).getValue();
//        } catch (NullPointerException ex) {
//        }
//    }
//    
//    @Override
//    public void proc() {
//        if (pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS) == null ) {
//            System.out.println("MID_TERM_MEMORY_OBJECT_RELATIONS is null");
//        } else {
//            persistRelations();
//            persistRelationsFile();
//            savedTimes+=1;
//            System.out.println("[PRC] Object relations from PRC persisted on rootMO" + savedTimes);
//        }
//        
//        if (requestObjectRelationIdea.get(OBJECT_ID_IDEA) == null ) {
//            System.out.println("requestObjectRelationIdea value is null");
//        } else {
//            Integer objectIdToRetrieveRelations = (Integer) requestObjectRelationIdea.get(OBJECT_ID_IDEA).getValue();
//            affectIntensity = emotionalDecay.getActivation();
//            Idea objectRelations = retrieveObjectRelations(objectIdToRetrieveRelations, affectIntensity, System.currentTimeMillis());
//            retrievedObjectRelationsMO.setI(objectRelations);
//            ArrayList<Integer> relations = (ArrayList<Integer>) objectRelations.get(OBJECT_RELATIONS_IDEA).get(RELATIONS_IDEA).getValue();
//            System.out.println("[PRC] Object id " + objectIdToRetrieveRelations + " relations retrieved: " + relations.toString());
//        }
//    }
//    
//    @Override
//    public void calculateActivation(){
//        Double activationValue = 0.0;
//        
//        if(Boolean.TRUE.equals(hasTimerExpired())) {
//            activationValue = 1.0;
//            startTime = System.currentTimeMillis();
//            System.out.println("Timer expired! Executing PRC Storage");
//        } 
//        try {
//            setActivation(activationValue);
//
//        } catch(CodeletActivationBoundsException  ex){
//            System.out.println("Activation Bounds exception");
//        }
//    }
//    
//    private Boolean hasTimerExpired() {
//        
//        long currentTime = System.currentTimeMillis();
//        long elapsedTime = currentTime - startTime;
//        return elapsedTime >= CONSOLIDATION_INTERVAL;
//    }
//    
//    
//    public void persistRelations() {
//        //fazer o objectrelations e o objectrelationsbyid        
//
//        midTermMemoryObjectRelations = (Map<Integer, ConcurrentHashMap<Integer, Idea>>) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS).getValue();
//        SortedSet<Integer> relationsIDs = new TreeSet<>(midTermMemoryObjectRelations.keySet());
//        Idea prcDataIdea = rootIdea.get(PRC_DATA_IDEA);
//        try {
//            ArrayList<String> indexFileData = (ArrayList<String>) prcDataIdea.get(INDEX_FILE).getValue();
//            ArrayList<String> newIndexFileData = new ArrayList<String>();
//            
//            //TRASPASA LOS INDICES YA EXISTENTES A UN NUEVO ARCHIVO 
//            for (String indexLineString: indexFileData) {
//                String indexParts[] = indexLineString.split(",");
//
//                int index = Integer.parseInt(indexParts[0]);
//
//                if (relationsIDs.contains(index)) {
//                    relationsIDs.remove(index);
//                }
//                newIndexFileData.add(index + "," + indexParts[1]);
//            }
//            
////            ArrayList<Idea> relationFiles = (ArrayList<Idea>) dgDataIdea.get(RELATION_FILES_PRC).getValue();
//            
//            //LOS NUEVOS INDICES QUE SON LOS QUE NO SE ELIMINARON DE relationsIDs SE AGREGAN AL FINAL Y SE CREA SU ARCHIVO DE RELACIONES VACIO
//            for (Integer relationID : relationsIDs) {
//                Idea newRelationFileIdea = new Idea(FILENAME + relationID, null, CATEGORY_PROPERTY, 1);
//                prcDataIdea.add(newRelationFileIdea);
//                newIndexFileData.add(relationID + "," + FILENAME + relationID);
//            }
//            
//            //REEMPLAZA EL ARCHIVO DE INDICES
//            prcDataIdea.get(INDEX_FILE).setValue(newIndexFileData);
//
//        } catch (NullPointerException ex){
//            System.err.println("[PRC] PRC Data or Index Idea does not exist on root");
//        }
//        
//        //ACTUALIZA LOS ARCHIVOS DE RELACIONES
//        HashMap<Integer, ConcurrentHashMap<Integer, Idea>> relationsToUpdate = new HashMap<>();
//        relationsToUpdate.putAll(midTermMemoryObjectRelations);
//        relationsIDs = new TreeSet<>(relationsToUpdate.keySet());
//        
//        
//        try {
//            //SE PROCEDE A ACTUALIZAR TODAS LAS RELACIONES QUE FUERON MODIFICADAS
//            for (Integer relationID : relationsIDs) {
//
//                //LEEMOS EL CONTENIDO DEL ARCHIVO ORIGINAL
//                //get the prc_id strings
//                ArrayList<String> fileStringRelationsValue = (ArrayList<String>) prcDataIdea.get(FILENAME + relationID).getValue();
//
//                //SE CARGAN TODAS LAS ESCENAS QUE NO ESTEN EN MID-TERM PARA QUE SE GENERE UN NUEVO ARCHIVO
//                //ES UN PROCESO PESADO DE LECTURA DE ARCHIVOS PERO NO SE PUEDE HACER DE OTRA FORMA SIN UNA BASE DE DATOS COMO SQLITE
//                for (String relationStr: fileStringRelationsValue) {
//
//                    Idea sceneRelation = createObjectRelationIdeaFromString(relationStr);
//                    Integer object1Id = (Integer) sceneRelation.get(OBJECT_1_ID_IDEA).getValue();
//                    Integer object2Id = (Integer) sceneRelation.get(OBJECT_2_ID_IDEA).getValue();
//                    
//
//
//                    //SI NO LO CONTIENE LO CARGA PARA REEMPLAZAR EL ARCHIVO
//                    //SI SI LO CONTIENE, LO IGNORA PORQUE YA ESTA CARGADO Y ACTUALIZADO
//                    if (!relationsToUpdate.get(object2Id).containsKey(object1Id)) {
//                        relationsToUpdate.get(object2Id).put(object1Id, sceneRelation);
//                    }
//
//                }
//
//                //SE SUSTITUYE EL ARCHIVO DE RELACIONES CON LAS NUEVAS
//                Idea newRelationFileIdea = prcDataIdea.get(FILENAME + relationID);
//                ArrayList<String> relationsArray = (ArrayList<String>) newRelationFileIdea.getValue();
//                SortedSet<Integer> destinationIDs = new TreeSet<>(relationsToUpdate.get(relationID).keySet());
//
//                for (Integer destinationID : destinationIDs) {
//
//                    Idea newRelation = (Idea) relationsToUpdate.get(relationID).get(destinationID);
//
//                    //SI ESTA MUY ACTIVA SI SE PERSISTE
//                    Double activationValue = (Double) newRelation.get(ACTIVATION_IDEA).getValue();
//                    if (activationValue >= CONSOLIDATION_THRESHOLD) {
//                        relationsArray.add(ideaToString(newRelation));
//                    }
//                }
//
//                newRelationFileIdea.setValue(relationsArray);
//                prcDataIdea.get(FILENAME + relationID).setValue(relationsArray);
//
//
//            }
//        } catch ( NullPointerException ex) {
//            prcDataIdea = new Idea(DG_DATA_IDEA, null, CATEGORY_PROPERTY, 1); 
//        }
//        rootOutputIdea.add(prcDataIdea);
//        rootOutputMO.setI(rootOutputIdea);
//
//        Idea dgDataIdeaOutput = rootOutputIdea.get(DG_DATA_IDEA);
//        countMtm();
//    }
//    
//    private Idea createObjectRelationIdeaFromString(String str) {
//        System.out.println("Str from createObjectRelationIdeaFromString: " + str);
//        String data[] = str.split(",");
//        Idea objectRelationIdea = new Idea(OBJECT_RELATION_IDEA, null,CATEGORY_PROPERTY, 1);
//        Idea object1IdIdea = new Idea(OBJECT_1_ID_IDEA, Integer.valueOf(data[0]),CATEGORY_PROPERTY, 1);
//        Idea object2IdIdea = new Idea(OBJECT_2_ID_IDEA, Integer.valueOf(data[1]),CATEGORY_PROPERTY, 1);
//        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, Integer.valueOf(data[2]),CATEGORY_PROPERTY, 1);
//        Idea activationIdea = new Idea(ACTIVATION_IDEA, Double.valueOf(data[3]),CATEGORY_PROPERTY, 1);
//        Idea timeIdea = new Idea(TIME_IDEA, Integer.valueOf(data[4]),CATEGORY_PROPERTY, 1);
//        Idea timestampIdea = new Idea(TIMESTAMP_IDEA, Long.valueOf(data[5]),CATEGORY_PROPERTY, 1);
//        Idea recentIdea = new Idea(RECENT_IDEA, false,CATEGORY_PROPERTY, 1);
//        Idea updatedIdea = new Idea(UPDATED_IDEA, false,CATEGORY_PROPERTY, 1);
//
//        objectRelationIdea.add(object1IdIdea);
//        objectRelationIdea.add(object2IdIdea);
//        objectRelationIdea.add(timeIdea);
//        objectRelationIdea.add(activationIdea);
//        objectRelationIdea.add(timestampIdea);
//        objectRelationIdea.add(recentIdea);
//        objectRelationIdea.add(updatedIdea);
//        objectRelationIdea.add(repetitionsIdea);
//        return objectRelationIdea;
//    }
//    
//    private String ideaToString(Idea idea) {
//        Integer object1Id = (Integer) idea.get(OBJECT_1_ID_IDEA).getValue();
//        Integer object2Id = (Integer) idea.get(OBJECT_2_ID_IDEA).getValue();
//        Integer repetitions = (Integer) idea.get(REPETITIONS_IDEA).getValue();
//        Double activation = (Double) idea.get(ACTIVATION_IDEA).getValue();
//        Integer time = (Integer) idea.get(TIME_IDEA).getValue();
//        Long timeStamp = (Long) idea.get(TIMESTAMP_IDEA).getValue();
//        return object1Id + "," + object2Id + "," + String.format("%010d", repetitions) + "," + String.format("%.5f", activation) + "," + String.format("%020d", time) + "," + timeStamp;
//    }
//    
//    public void countMtm() {
//        int total = 0;
//        for (Integer vertex : midTermMemoryObjectRelations.keySet()) {
//
//            ConcurrentHashMap<Integer, Idea> relations = midTermMemoryObjectRelations.get(vertex);
//
//            for (Idea sr : relations.values()) {
//                total++;
//            }
//        }
//        System.out.println("[PRC] total OBJ relations: "+total);
//    }
//    
//    public void createStoragePaths() {
//
//        File path = new File(STORAGE_PATH);
//
//        if (!path.exists()) {
//            path.mkdirs();
//        }
//    }
//    public void persistRelationsFile() {
//        System.out.println("entered persistRelationsFile on PRC");
//        try {
//
//            createStoragePaths();
//
//            File previousIndexFile = new File(INDEX_FILE);
//
//            if (!previousIndexFile.exists()) {
//                previousIndexFile.createNewFile();
//            }
//
//            BufferedReader previousIndexFileReader = new BufferedReader(new FileReader(previousIndexFile));
//
//            File updatedIndexFile = new File(STORAGE_PATH + "index_temp.txt");
//            PrintWriter updatedIndexFileWriter = new PrintWriter(new FileWriter(updatedIndexFile));
//
//            SortedSet<Integer> relationsIDs = new TreeSet<>(midTermMemoryObjectRelations.keySet());
//
//            String indexLineString = null;
//
//            //TRASPASA LOS INDICES YA EXISTENTES A UN NUEVO ARCHIVO 
//            while ((indexLineString = previousIndexFileReader.readLine()) != null) {
//
//                String indexParts[] = indexLineString.split(",");
//
//                int index = Integer.parseInt(indexParts[0]);
//
//                if (relationsIDs.contains(index)) {
//                    relationsIDs.remove(index);
//                }
//
//                updatedIndexFileWriter.println(index + "," + indexParts[1]);
//                updatedIndexFileWriter.flush();
//
//            }
//
//            //LOS NUEVOS INDICES QUE SON LOS QUE NO SE ELIMINARON DE relationsIDs SE AGREGAN AL FINAL Y SE CREA SU ARCHIVO DE RELACIONES VACIO
//            for (Integer relationID : relationsIDs) {
//
//                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");
//                newRelationFile.close();
//
//                updatedIndexFileWriter.println(relationID + "," + FILENAME + relationID + ".txt");
//                updatedIndexFileWriter.flush();
//            }
//
//            updatedIndexFileWriter.close();
//            previousIndexFileReader.close();
//
//            //REEMPLAZA EL ARCHIVO DE INDICES
//            if (previousIndexFile.delete()) {
//                if (!updatedIndexFile.renameTo(previousIndexFile)) {
//                    System.out.println("Error on updating file");
//                }
//            } else {
//                System.out.println("Error on updating file");
//            }
//
//            //ACTUALIZA LOS ARCHIVOS DE RELACIONES
//            HashMap<Integer, ConcurrentHashMap<Integer, Idea>> relationsToUpdate = new HashMap<>();
//            relationsToUpdate.putAll(midTermMemoryObjectRelations);
//
//            relationsIDs = new TreeSet<>(relationsToUpdate.keySet());
//
//            //SE PROCEDE A ACTUALIZAR TODAS LAS RELACIONES QUE FUERON MODIFICADAS
//            for (Integer relationID : relationsIDs) {
//
//                //LEEMOS EL CONTENIDO DEL ARCHIVO ORIGINAL
//                BufferedReader originalRelationsReader = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME + relationID + ".txt"));
//                String relationStr;
//
//                //SE CARGAN TODAS LAS ESCENAS QUE NO ESTEN EN MID-TERM PARA QUE SE GENERE UN NUEVO ARCHIVO
//                //ES UN PROCESO PESADO DE LECTURA DE ARCHIVOS PERO NO SE PUEDE HACER DE OTRA FORMA SIN UNA BASE DE DATOS COMO SQLITE
//                while ((relationStr = originalRelationsReader.readLine()) != null) {
//                    System.out.println("Relation str from file: " + relationStr);
//                    Idea sceneRelation = createObjectRelationIdeaFromString(relationStr);
//                    Integer object1Id = (Integer) sceneRelation.get(OBJECT_1_ID_IDEA).getValue();
//                    Integer object2Id = (Integer) sceneRelation.get(OBJECT_2_ID_IDEA).getValue();
//                    
////                    ObjectRelation sceneRelation = new ObjectRelation(relationStr);
//
////                    int objectId = sceneRelation.getObject2Id();
//
//                    //SI NO LO CONTIENE LO CARGA PARA REEMPLAZAR EL ARCHIVO
//                    //SI SI LO CONTIENE, LO IGNORA PORQUE YA ESTA CARGADO Y ACTUALIZADO
//                    if (!relationsToUpdate.get(object2Id).containsKey(object1Id)) {
//                        relationsToUpdate.get(object2Id).put(object1Id, sceneRelation);
//                    }
//
//                }
//
//                originalRelationsReader.close();
//
//                //SE SUSTITUYE EL ARCHIVO DE RELACIONES CON LAS NUEVAS
//                PrintWriter newRelationFile = new PrintWriter(STORAGE_PATH + FILENAME + relationID + ".txt");
//
//                SortedSet<Integer> destinationIDs = new TreeSet<>(relationsToUpdate.get(relationID).keySet());
//
//                for (Integer destinationID : destinationIDs) {
//                    
//                    Idea newRelation = (Idea) relationsToUpdate.get(relationID).get(destinationID);
//
////                    ObjectRelation newRelation = relationsToUpdate.get(relationID).get(destinationID);
//
//                    //SI ESTA MUY ACTIVA SI SE PERSISTE
//                    Double activationValue = (Double) newRelation.get(ACTIVATION_IDEA).getValue();
//                    if (activationValue >= Configuration.CONSOLIDATION_THRESHOLD) {
////                        newRelationFile.println(newRelation.toString());
//                        newRelationFile.println(ideaToString(newRelation));
//                        newRelationFile.flush();
//                    }
//                }
//
//                newRelationFile.close();
//
//            }
//            
//            countMtm();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    
//    
//    //Retrieve
//    public Idea retrieveObjectRelations(int objectId, double affect, long timeStamp) {
//
////        ObjectRelations relations;
//        Idea relations;
//
////        ConcurrentHashMap<Integer, ObjectRelation> sourceRelations = midTermMemoryObjectRelations.get(objectId);
//        ConcurrentHashMap<Integer, Idea> sourceRelations = midTermMemoryObjectRelations.get(objectId);
////        ArrayList<ObjectRelation> retrievedRelations = new ArrayList<>();
//        ArrayList<Idea> retrievedRelations = new ArrayList<>();
//        ArrayList<Integer> retrievedRelationsClasses = new ArrayList<>();
//
//        if (sourceRelations != null) {
//
////            for (ObjectRelation relation : sourceRelations.values()) {
//            for (Idea relation : sourceRelations.values()) {
//                Long timeStampFromRelation = (Long) relation.get(TIMESTAMP_IDEA).getValue();
//                double elapsedTime = (timeStamp - timeStamp);
//
//                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias
//
//                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, timeStampFromRelation);
//
//                //AND THE OBJ RELATION HAS NOT DECAY
//                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {
//                    
//                    relation = createNewIdeaUpdateAffectTimestamp(relation, 0.0, timeStamp);
//
//                    retrievedRelations.add(relation);
//                    Integer object1Id = (Integer) relation.get(OBJECT_1_ID_IDEA).getValue();
//                    retrievedRelationsClasses.add(object1Id);
//
//                    //Relacion simetrica
//                    //ObjectRelation simetricRelation = adjancencyList.get(relation.getObject1Id()).get(objectId);
//                    //simetricRelation.incrementRepetitions();
//                    //simetricRelation.setTimeStamp(timeStamp);
//                }
//
//            }
//            relations = createSimpleObjectRelationsIdea(objectId, retrievedRelationsClasses);
//
//            //DEBIDO A QUE LAS NUEVAS RELACIONES CREADAS Y SU RELACION SIMETRICA SE AGREGAN A MID-TERM MEMORY
//            //ESTO PUEDE EVITAR QUE SE REALICE EL QUERY A LTM A PESAR DE AUN NO SE HAN CARGADO LAS MEMORIAS QUE SI DEBEN CARGARSE
//            //PARA EVITAR ESO SE USA LA VARIABLE DE CONTROL SOBRE QUE ESCENAS YA SE HAN CONSULTADO A LTM
//            //SI EXISTEN ESCENAS EN MID-TERM QUE SON NUEVAS, HAY QUE CONSULTAR A LTM
//            if (!midTermMemoryObjectRelationsQueried.containsKey(objectId)) {
//
////                ObjectRelations tmpRelations = retrieveObjectRelationsLTM(objectId, affect, timeStamp);
//                Idea tmpRelations = retrieveObjectRelationsLTM(objectId, affect, timeStamp);
//                ArrayList<Integer> relationsFromTmpRelationsIdea = (ArrayList<Integer>) tmpRelations.get(RELATIONS_IDEA).getValue();
//                retrievedRelationsClasses.addAll(relationsFromTmpRelationsIdea);
//                
//                relations = createSimpleObjectRelationsIdea(objectId, retrievedRelationsClasses);
//
//                midTermMemoryObjectRelationsQueried.put(objectId, Boolean.TRUE);
//            }
//
//        } else {
//
//            //SimpleLogger.log(this, "No hay relaciones en mid-term memory, buscando en long-term");
//            relations = retrieveObjectRelationsLTM(objectId, affect, timeStamp);
//
//        }
//
//        return relations;
//
//    }
//    
//    //SEARCH IN LTM
//    public Idea retrieveObjectRelationsLTM(int objectId, double affect, long timeStamp) {
//        try {
//
//            ArrayList<Idea> retrievedRelations = new ArrayList<>();
//            ArrayList<Integer> retrievedRelationsClasses = new ArrayList<>();
//
//            String relationsFile = STORAGE_PATH + "prc_" + objectId + ".txt";
//
//            File relationFile = new File(relationsFile);
//
//            if (!relationFile.exists()) {
//                createStoragePaths();
//                return createSimpleObjectRelationsIdea(objectId, new ArrayList<>());
//            }
//
//            BufferedReader relationsBr = new BufferedReader(new FileReader(relationsFile));
//            String relationStr;
//
//            while ((relationStr = relationsBr.readLine()) != null) {
//                //TODO: ver se isso está funcionando corretamente
//                Idea relation = createObjectRelationIdeaFromString(relationStr);
////                ObjectRelation relation = new ObjectRelation(relationStr);
//                double timeStampRelationIdea = (Long) relation.get(TIMESTAMP_IDEA).getValue();
//                double elapsedTime = (timeStamp - timeStampRelationIdea);
//
//                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias
//                
//                double activationRelationIdea = (Double) relation.get(ACTIVATION_IDEA).getValue();
//
//                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, activationRelationIdea);
//
//                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {
//
//                    //INICIALIZA LAS POSICIONES DONDE SE AGREGARAN EL NODO Y SU RELACION
//                    Integer object2IdRelation = (Integer) relation.get(OBJECT_2_ID_IDEA).getValue();
//                    createIfNotExists(object2IdRelation);
//
//                    relation = createNewIdeaUpdateAffectTimestamp(relation, affect, timeStamp);
//
//                    //SI YA EXISTE LA TUPLA DEBIDO A QUE LO VIO EL USUARIO ANTES DE RECUPERARLA NO SE AGREGA
//                    //POR QUE YA ESTA CARGADA
//                    Integer object1IdRelation = (Integer) relation.get(OBJECT_1_ID_IDEA).getValue();
//                    if (!midTermMemoryObjectRelations.get(object2IdRelation).containsKey(object1IdRelation)) {
//                        retrievedRelations.add(relation);
//                        retrievedRelationsClasses.add(object1IdRelation);
//                    }
//
//                    //SE AGREGA LA RELACION A MID-TERM MEMORY
//                    midTermMemoryObjectRelations.get(object2IdRelation).put(object1IdRelation, relation);
//
//                }
//
//            }
//
//            relationsBr.close();
//            Idea relations = createSimpleObjectRelationsIdea(objectId, retrievedRelationsClasses);
//
//            return relations;
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return createSimpleObjectRelationsIdea(objectId, new ArrayList<>());
//    }
//    
//    private void createIfNotExists(int key) {
//        if (!midTermMemoryObjectRelations.containsKey(key)) {
//            midTermMemoryObjectRelations.put(key, new ConcurrentHashMap<Integer, Idea>());
//        }
//    }
//    
//    public Idea createNewIdeaUpdateAffectTimestamp(Idea idea, double affectIntensity, long timeStamp) {
//        int time = (int) idea.get(TIME_IDEA).getValue();
//        boolean updated = (boolean) idea.get(UPDATED_IDEA).getValue();
//        return incrementRepetitions(idea, affectIntensity, time, timeStamp, updated);
//    }
//    
//    private Idea incrementRepetitions(Idea idea, double affectIntensity, int time, long timeStamp, boolean updated) {
//        int scene1Id = (int) idea.get(OBJECT_ID_1_IDEA).getValue();
//        int scene2Id = (int) idea.get(OBJECT_ID_2_IDEA).getValue();
//        int repetitions = (int) idea.get(REPETITIONS_IDEA).getValue();
//        boolean recent = (boolean) idea.get(RECENT_IDEA).getValue();
//        double activation = (double) idea.get(ACTIVATION_IDEA).getValue();
//        return createCompleteObjectRelationIdea(scene1Id, scene2Id, calculateNewActivation(affectIntensity, activation, repetitions), repetitions + 1, time, timeStamp, recent, updated);
//    }
//    
//    private double calculateNewActivation(double affectIntensity, double activation, int repetitions){
//        
//        double scale = SIGMOID_SCALE;
//        double affect = affectIntensity;
//        double alpha = ALPHA_MEMORY_INC_RELEVANCE;
//        double beta = BETA_AFFECT_RELEVANCE;
//
//        double weight = ActivationFunctions.weight(repetitions, scale, affect, alpha, beta);
//
//        double currentActivation = ActivationFunctions.sigmoid(weight);
//
//        activation = (activation + currentActivation) / 2.0;
//        return activation;
//    }
//    
//    private Idea createSimpleObjectRelationsIdea(int objectId, ArrayList<Integer> relations) {
//        Idea objectRelationsIdea = new Idea(OBJECT_RELATIONS_IDEA, null, CATEGORY_PROPERTY, 1);        
//        Idea objectIdIdea = new Idea(OBJECT_ID_IDEA, objectId, CATEGORY_PROPERTY, 1);
//        Idea relationsIdea = new Idea(OBJECT_RELATIONS_IDEA, relations, CATEGORY_PROPERTY, 1);
//
//        
//        objectRelationsIdea.add(objectIdIdea);
//        objectRelationsIdea.add(relationsIdea);
//            
//        return objectRelationsIdea;
//    }
//    
//    private Idea createCompleteObjectRelationIdea(int object1Id, int object2Id, double activation, int repetitions, int time, long timeStamp, boolean recent, boolean updated){
//        Idea objectRelationIdea = new Idea(OBJECT_RELATION_IDEA, null, CATEGORY_PROPERTY, 1);        
//        Idea id1Idea = new Idea(OBJECT_ID_1_IDEA, object1Id, CATEGORY_PROPERTY, 1);
//        Idea id2Idea = new Idea(OBJECT_ID_2_IDEA, object2Id, CATEGORY_PROPERTY, 1);
//        Idea activationIdea = new Idea(ACTIVATION_IDEA, activation, CATEGORY_PROPERTY, 1);
//        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, repetitions, CATEGORY_PROPERTY, 1);
//        Idea timeIdea = new Idea(TIME_IDEA,time, CATEGORY_PROPERTY, 1);
//        Idea timestampIdea = new Idea(TIMESTAMP_IDEA,timeStamp, CATEGORY_PROPERTY, 1);
//        Idea recentIdea = new Idea(RECENT_IDEA, recent, CATEGORY_PROPERTY, 1);
//        Idea updatedIdea = new Idea(UPDATED_IDEA, updated, CATEGORY_PROPERTY, 1);
//        
//        objectRelationIdea.add(id1Idea);
//        objectRelationIdea.add(id2Idea);
//        objectRelationIdea.add(repetitionsIdea);
//        objectRelationIdea.add(activationIdea);
//        objectRelationIdea.add(timeIdea);
//        objectRelationIdea.add(timestampIdea);
//        objectRelationIdea.add(recentIdea);
//        objectRelationIdea.add(updatedIdea);
//            
//        return objectRelationIdea;
//    }
//
//}
