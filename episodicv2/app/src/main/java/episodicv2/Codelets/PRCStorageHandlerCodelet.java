/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;
import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.configuration.Configuration.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author karenlima
 */
public class PRCStorageHandlerCodelet extends Codelet {
    
    MemoryObject rootMO;
    Idea rootIdea;

    MemoryObject pRCMidTermMemoryObjectRelationsMO;
    Idea pRCMidTermMemoryObjectRelationsIdea;
    
    MemoryObject rootOutputMO;
    Idea rootOutputIdea;
    
    Map<Integer, ConcurrentHashMap<Integer, Idea>> midTermMemoryObjectRelations;
    
    Map<Integer, Idea> midTermMemoryObjectRelationsById;
    
    Map<Integer, ConcurrentHashMap<Integer, Idea>> fileContent = null;
    
    Integer savedTimes = 0;
    
    Integer currentFrameSaved = 0;

    private static final String FILENAME = "prc_";
    
    private long startTime;
    
    public PRCStorageHandlerCodelet() {
        try {
            setThreshold(0.5);
            
        } catch(CodeletThresholdBoundsException  ex){
            System.out.println("Threshold Bounds exception");
        }
        
        startTime = System.currentTimeMillis();
        
    }

    @Override
    public void accessMemoryObjects() {
        
        rootMO = (MemoryObject) getInput(ROOT_MO);
        pRCMidTermMemoryObjectRelationsMO = (MemoryObject) getInput(PRC_MID_TERM_OBJECT_RELATIONS_MO);
        rootOutputMO = (MemoryObject) getOutput(ROOT_MO);
        pRCMidTermMemoryObjectRelationsIdea = (Idea) pRCMidTermMemoryObjectRelationsMO.getI();
        try {
            rootIdea = (Idea) rootMO.getI();
            rootOutputIdea = (Idea) rootOutputMO.getI();
            
            fileContent = (Map<Integer, ConcurrentHashMap<Integer, Idea>>) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS).getValue();
        } catch (NullPointerException ex) {
//            System.out.println("[PRC] Root MO is null");
        }
    }
    
    @Override
    public void proc() {
        if (pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS) == null ) return;
        persistRelations();
        savedTimes+=1;
        System.out.println("[PRC] Object relations from PRC persisted on rootMO" + savedTimes);
    }
    
    @Override
    public void calculateActivation(){
        Double activationValue = 0.0;
        
        if(hasTimerExpired()) {
            activationValue = 1.0;
            startTime = System.currentTimeMillis();
            System.out.println("Timer expired! Executing PRC Storage");
        } 
        try {
            setActivation(activationValue);

        } catch(CodeletActivationBoundsException  ex){
            System.out.println("Activation Bounds exception");
        }
    }
//        if (pRCMidTermMemoryObjectRelationsIdea != null) {
//            if (pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS_TOTAL_FRAME) != null) {
//                Integer objectRelationsFrame = (Integer) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS_TOTAL_FRAME).getValue();
//                if (currentFrameSaved < objectRelationsFrame) {
//                    currentFrameSaved = objectRelationsFrame;
//                    activationValue = 1.0;
//                } 
//                try {
//                    setActivation(activationValue);
//
//                } catch(CodeletActivationBoundsException  ex){
//                    System.out.println("Activation Bounds exception");
//                }
//            }
//        }

       
//        Double activationValue = 0.0;
//        try {
////            String fileContent = (String) pRCMidTermMemoryObjectRelationsIdea.get(INDEX_FILE).getValue();
//
//            Map<Integer, ConcurrentHashMap<Integer, Idea>> fileContent = (Map<Integer, ConcurrentHashMap<Integer, Idea>>) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS).getValue();
//            if (fileContent != null) {
//                activationValue = 1.0;
//                setTimeStep(CONSOLIDATION_INTERVAL);
//            } 
//        } catch (NullPointerException ex) {
////            System.out.println("[PRC] pRCMidTermMemoryObjectRelationsIdea is null");
//        } 
//        
//        try {
//            setActivation(activationValue);
//            
//        } catch(CodeletActivationBoundsException  ex){
////            System.out.println("Activation Bounds exception");
//        }
    
    private Boolean hasTimerExpired() {
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime >= CONSOLIDATION_INTERVAL;
    }
    
    
    public void persistRelations() {
        
        //fazer o objectrelations e o objectrelationsbyid        

        midTermMemoryObjectRelations = (Map<Integer, ConcurrentHashMap<Integer, Idea>>) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS).getValue();
        SortedSet<Integer> relationsIDs = new TreeSet<>(midTermMemoryObjectRelations.keySet());
        Idea prcDataIdea = rootIdea.get(PRC_DATA_IDEA);
        try {
            ArrayList<String> indexFileData = (ArrayList<String>) prcDataIdea.get(INDEX_FILE).getValue();
            ArrayList<String> newIndexFileData = new ArrayList<String>();
            
            //TRASPASA LOS INDICES YA EXISTENTES A UN NUEVO ARCHIVO 
            for (String indexLineString: indexFileData) {
                String indexParts[] = indexLineString.split(",");

                int index = Integer.parseInt(indexParts[0]);

                if (relationsIDs.contains(index)) {
                    relationsIDs.remove(index);
                }
                newIndexFileData.add(index + "," + indexParts[1]);
            }
            
//            ArrayList<Idea> relationFiles = (ArrayList<Idea>) dgDataIdea.get(RELATION_FILES_PRC).getValue();
            
            //LOS NUEVOS INDICES QUE SON LOS QUE NO SE ELIMINARON DE relationsIDs SE AGREGAN AL FINAL Y SE CREA SU ARCHIVO DE RELACIONES VACIO
            for (Integer relationID : relationsIDs) {
                Idea newRelationFileIdea = new Idea(FILENAME + relationID, null, "Property", 1);
                prcDataIdea.add(newRelationFileIdea);
                newIndexFileData.add(relationID + "," + FILENAME + relationID);
            }
            
            //REEMPLAZA EL ARCHIVO DE INDICES
            prcDataIdea.get(INDEX_FILE).setValue(newIndexFileData);

        } catch (NullPointerException ex){
            System.err.println("[PRC] PRC Data or Index Idea does not exist on root");
        }
        
        //ACTUALIZA LOS ARCHIVOS DE RELACIONES
        HashMap<Integer, ConcurrentHashMap<Integer, Idea>> relationsToUpdate = new HashMap<>();
        relationsToUpdate.putAll(midTermMemoryObjectRelations);
        relationsIDs = new TreeSet<>(relationsToUpdate.keySet());
        
        
        try {
            //SE PROCEDE A ACTUALIZAR TODAS LAS RELACIONES QUE FUERON MODIFICADAS
            for (Integer relationID : relationsIDs) {

                //LEEMOS EL CONTENIDO DEL ARCHIVO ORIGINAL
                //get the prc_id strings
                ArrayList<String> fileStringRelationsValue = (ArrayList<String>) prcDataIdea.get(FILENAME + relationID).getValue();

                //SE CARGAN TODAS LAS ESCENAS QUE NO ESTEN EN MID-TERM PARA QUE SE GENERE UN NUEVO ARCHIVO
                //ES UN PROCESO PESADO DE LECTURA DE ARCHIVOS PERO NO SE PUEDE HACER DE OTRA FORMA SIN UNA BASE DE DATOS COMO SQLITE
                for (String relationStr: fileStringRelationsValue) {

                    Idea sceneRelation = createObjectRelationIdeaFromString(relationStr);
                    Integer object1Id = (Integer) sceneRelation.get(OBJECT_1_ID_IDEA).getValue();
                    Integer object2Id = (Integer) sceneRelation.get(OBJECT_2_ID_IDEA).getValue();
                    


                    //SI NO LO CONTIENE LO CARGA PARA REEMPLAZAR EL ARCHIVO
                    //SI SI LO CONTIENE, LO IGNORA PORQUE YA ESTA CARGADO Y ACTUALIZADO
                    if (!relationsToUpdate.get(object2Id).containsKey(object1Id)) {
                        relationsToUpdate.get(object2Id).put(object1Id, sceneRelation);
                    }

                }

                //SE SUSTITUYE EL ARCHIVO DE RELACIONES CON LAS NUEVAS
                Idea newRelationFileIdea = prcDataIdea.get(FILENAME + relationID);
                ArrayList<String> relationsArray = (ArrayList<String>) newRelationFileIdea.getValue();
                SortedSet<Integer> destinationIDs = new TreeSet<>(relationsToUpdate.get(relationID).keySet());

                for (Integer destinationID : destinationIDs) {

                    Idea newRelation = (Idea) relationsToUpdate.get(relationID).get(destinationID);

                    //SI ESTA MUY ACTIVA SI SE PERSISTE
                    Double activationValue = (Double) newRelation.get(ACTIVATION_IDEA).getValue();
                    if (activationValue >= CONSOLIDATION_THRESHOLD) {
                        relationsArray.add(ideaToString(newRelation));
                    }
                }

                newRelationFileIdea.setValue(relationsArray);
                prcDataIdea.get(FILENAME + relationID).setValue(relationsArray);


            }
        } catch ( NullPointerException ex) {
            prcDataIdea = new Idea(DG_DATA_IDEA, null, "Property", 1); 
        }
        
        
        //remove dgDataIdea

//        rootOutputIdea.get(PRC_DATA_IDEA).setValue(prcDataIdea.getValue());
        rootOutputIdea.add(prcDataIdea);
        rootOutputMO.setI(rootOutputIdea);

        Idea dgDataIdeaOutput = rootOutputIdea.get(DG_DATA_IDEA);
        System.out.println("Root Idea on PRCStorage output ideas: " + dgDataIdeaOutput.getL());
        System.out.println("Root Idea on PRCStorage output DGSize Idea: " + dgDataIdeaOutput.get(DG_SIZE_IDEA).getValue());
        countMtm();
        
//        persistAffect();
    }
    
//    
//    /**
//     * STORES ALL THE TEMPORAL PATTERNS
//     */
//    public void persistAffect() {
//        try {
//            midTermMemoryObjectRelationsById = (Map<Integer, ConcurrentHashMap<Integer, Idea>>) pRCMidTermMemoryObjectRelationsIdea.get(MID_TERM_MEMORY_OBJECT_RELATIONS_BY_ID).getValue();
//            File path = new File(STORAGE_PATH);
//
//            //VERIFICA QUE EXISTA LA CARPETA DE ALMACENAMIENTO, SINO LA CREA
//            if (!path.exists()) {
//                path.mkdirs();
//            }
//
//            File previousKnowledge = new File(STORAGE_PATH + FILENAME);
//
//            if (!previousKnowledge.exists()) {
//                previousKnowledge.createNewFile();
//            }
//
//            BufferedReader previousKnowledgeReader = new BufferedReader(new FileReader(previousKnowledge));
//
//            File updatedKnowledge = new File(STORAGE_PATH + "prc_objects_temp.txt");
//            PrintWriter updatedKnowledgeWriter = new PrintWriter(new FileWriter(updatedKnowledge));
//
//            HashMap<Integer, ObjectRelations> objectsToUpdate = new HashMap<>();
//            objectsToUpdate.putAll(midTermMemoryObjectsByID);
//
//            //OBJECTS SORTED BY AFFECTIVE VALUE
//            HashMap<Integer, ObjectRelations> objectsSortedByAffect = new HashMap<>();
//
//            //
//            String objectLineString = null;
//
//            while ((objectLineString = previousKnowledgeReader.readLine()) != null) {
//
//                ObjectRelations object = new ObjectRelations(objectLineString);
//
//                if (objectsToUpdate.containsKey(object.getObjectId())) {
//
//                    //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER ACTUALIZADO ANTES DE GUARDAR
//                    object = objectsToUpdate.get(object.getObjectId());
//
//                    objectsToUpdate.remove(object.getObjectId());
//                }
//
//                updatedKnowledgeWriter.println(object.toString());
//                updatedKnowledgeWriter.flush();
//
//                //OBJECTS SORTED BY AFFECTIVE VALUE
//                objectsSortedByAffect.put(object.getObjectId(), object);
//            }
//
//            SortedSet<Integer> objectIDs = new TreeSet<>(objectsToUpdate.keySet());
//
//            for (Integer objectID : objectIDs) {
//
//                ObjectRelations object = objectsToUpdate.get(objectID);
//
//                //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER GUARDADO
//                updatedKnowledgeWriter.println(object.toString());
//                updatedKnowledgeWriter.flush();
//
//                //OBJECTS SORTED BY AFFECTIVE VALUE
//                objectsSortedByAffect.put(object.getObjectId(), object);
//            }
//
//            updatedKnowledgeWriter.close();
//            previousKnowledgeReader.close();
//
//            if (previousKnowledge.delete()) {
//
//                if (!updatedKnowledge.renameTo(previousKnowledge)) {
//                    SimpleLogger.log(this, "Error on updating file");
//                }
//
//            } else {
//                SimpleLogger.log(this, "Error on updating file");
//            }
//
//            //WRITE THE OBJECTS SORTED BY POSITIVE AFFECT
//            File affectIndexedObjects = new File(STORAGE_PATH + "prc_objects_positive_affect.txt");
//            PrintWriter affectIndexedObjectsWriter = new PrintWriter(new FileWriter(affectIndexedObjects));
//
//            HashMap<Integer, ObjectRelations> sortedAHM = sortByPositiveAffect(objectsSortedByAffect, true);
//
//            for (Map.Entry<Integer, ObjectRelations> aSO : sortedAHM.entrySet()) {
//                affectIndexedObjectsWriter.println(aSO.getValue().toString());
//                affectIndexedObjectsWriter.flush();
//            }
//
//            affectIndexedObjectsWriter.close();
//
//            //WRITE THE OBJECTS SORTED BY POSITIVE AFFECT
//            File nAffectIndexedObjects = new File(STORAGE_PATH + "prc_objects_negative_affect.txt");
//            PrintWriter nAffectIndexedObjectsWriter = new PrintWriter(new FileWriter(nAffectIndexedObjects));
//
//            HashMap<Integer, ObjectRelations> sortedNAHM = sortByNegativeAffect(objectsSortedByAffect, true);
//
//            for (Map.Entry<Integer, ObjectRelations> aSO : sortedNAHM.entrySet()) {
//                nAffectIndexedObjectsWriter.println(aSO.getValue().toString());
//                nAffectIndexedObjectsWriter.flush();
//            }
//
//            nAffectIndexedObjectsWriter.close();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    
    
    private Idea createObjectRelationIdeaFromString(String str) {
        String data[] = str.split(",");
        Idea objectRelationIdea = new Idea(OBJECT_RELATION_IDEA, null,"Property", 1);
        Idea object1IdIdea = new Idea(OBJECT_1_ID_IDEA, Integer.parseInt(data[0]),"Property", 1);
        Idea object2IdIdea = new Idea(OBJECT_2_ID_IDEA, Integer.parseInt(data[1]),"Property", 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, Integer.parseInt(data[2]),"Property", 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, Double.parseDouble(data[3]),"Property", 1);
        Idea timeIdea = new Idea(TIME_IDEA, Integer.parseInt(data[4]),"Property", 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA, Long.parseLong(data[5]),"Property", 1);
        Idea recentIdea = new Idea(RECENT_IDEA, false,"Property", 1);
        Idea updatedIdea = new Idea(UPDATED_IDEA, false,"Property", 1);
        

        objectRelationIdea.add(object1IdIdea);
        objectRelationIdea.add(object2IdIdea);
        objectRelationIdea.add(timeIdea);
        objectRelationIdea.add(activationIdea);
        objectRelationIdea.add(timestampIdea);
        objectRelationIdea.add(recentIdea);
        objectRelationIdea.add(updatedIdea);
        objectRelationIdea.add(repetitionsIdea);
        return objectRelationIdea;
    }
    
    private String ideaToString(Idea idea) {
        Integer object1Id = (Integer) idea.get(OBJECT_1_ID_IDEA).getValue();
        Integer object2Id = (Integer) idea.get(OBJECT_2_ID_IDEA).getValue();
        Integer repetitions = (Integer) idea.get(REPETITIONS_IDEA).getValue();
        Double activation = (Double) idea.get(ACTIVATION_IDEA).getValue();
        Integer time = (Integer) idea.get(TIME_IDEA).getValue();
        Long timeStamp = (Long) idea.get(TIMESTAMP_IDEA).getValue();
        return object1Id + "," + object2Id + "," + String.format("%010d", repetitions) + "," + String.format("%.5f", activation) + "," + String.format("%020d", time) + "," + timeStamp;
    }
    
    public void countMtm() {
        int total = 0;
        for (Integer vertex : midTermMemoryObjectRelations.keySet()) {

            ConcurrentHashMap<Integer, Idea> relations = midTermMemoryObjectRelations.get(vertex);


            for (Idea sr : relations.values()) {
                total++;
            }

        }
        
        System.out.println("[PRC] total OBJ relations: "+total);
    }
}
