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
        
        if(Boolean.TRUE.equals(hasTimerExpired())) {
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
                Idea newRelationFileIdea = new Idea(FILENAME + relationID, null, CATEGORY_PROPERTY, 1);
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
            prcDataIdea = new Idea(DG_DATA_IDEA, null, CATEGORY_PROPERTY, 1); 
        }
        rootOutputIdea.add(prcDataIdea);
        rootOutputMO.setI(rootOutputIdea);

        Idea dgDataIdeaOutput = rootOutputIdea.get(DG_DATA_IDEA);
        System.out.println("Root Idea on PRCStorage output ideas: " + dgDataIdeaOutput.getL());
        System.out.println("Root Idea on PRCStorage output DGSize Idea: " + dgDataIdeaOutput.get(DG_SIZE_IDEA).getValue());
        countMtm();
    }
    
    private Idea createObjectRelationIdeaFromString(String str) {
        String data[] = str.split(",");
        Idea objectRelationIdea = new Idea(OBJECT_RELATION_IDEA, null,CATEGORY_PROPERTY, 1);
        Idea object1IdIdea = new Idea(OBJECT_1_ID_IDEA, Integer.valueOf(data[0]),CATEGORY_PROPERTY, 1);
        Idea object2IdIdea = new Idea(OBJECT_2_ID_IDEA, Integer.valueOf(data[1]),CATEGORY_PROPERTY, 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, Integer.valueOf(data[2]),CATEGORY_PROPERTY, 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, Double.valueOf(data[3]),CATEGORY_PROPERTY, 1);
        Idea timeIdea = new Idea(TIME_IDEA, Integer.valueOf(data[4]),CATEGORY_PROPERTY, 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA, Long.valueOf(data[5]),CATEGORY_PROPERTY, 1);
        Idea recentIdea = new Idea(RECENT_IDEA, false,CATEGORY_PROPERTY, 1);
        Idea updatedIdea = new Idea(UPDATED_IDEA, false,CATEGORY_PROPERTY, 1);

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
