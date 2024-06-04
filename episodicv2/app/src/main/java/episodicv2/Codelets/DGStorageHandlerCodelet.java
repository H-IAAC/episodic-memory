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
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author karenlima
 * Has the purpose of saving the dgMidTermMemoryScenesIdea in the RootMO
 * DG_DATA_IDEA -> DG_MEMORY_SCENES_IDEA e DG_SIZE_IDEA
 */

public class DGStorageHandlerCodelet extends Codelet {
    
    MemoryObject rootInputMO;
    Idea rootInputIdea;

    MemoryObject dgMidTermMemoryScenesMO;
    Idea dgMidTermMemoryScenesIdea;
    
    MemoryObject rootOutputMO;
    Idea rootOutputIdea;
    
    Integer savedTimes = 0;
    
    Integer currentFrameSaved = 0;
    
    private long startTime;

    public DGStorageHandlerCodelet() {
        try {
            setThreshold(0.5);
            
        } catch(CodeletThresholdBoundsException  ex){
            System.out.println("Threshold Bounds exception");
        }
        
        startTime = System.currentTimeMillis();
    }
    
    @Override
    public void accessMemoryObjects() {
        rootInputMO = (MemoryObject) getInput(ROOT_MO);
        dgMidTermMemoryScenesMO = (MemoryObject) getInput(DG_MID_TERM_MEMORY_SCENES_MO);
        rootOutputMO = (MemoryObject) getOutput(ROOT_MO);
        try {
            rootInputIdea = (Idea) rootInputMO.getI();
            rootOutputIdea = (Idea) rootOutputMO.getI();
        } catch (NullPointerException ex) {
        }
        
        try {
            dgMidTermMemoryScenesIdea = (Idea) dgMidTermMemoryScenesMO.getI();
        } catch (NullPointerException ex) {
        }
    }
    
    @Override
    public void proc() {
        persistScenes();
        savedTimes+=1;     
    }
    
    @Override
    public void calculateActivation(){
        
        Double activationValue = 0.0;
        
        if(hasTimerExpired()) {
            activationValue = 1.0;
            startTime = System.currentTimeMillis();
            System.out.println("Timer expired! Executing DG Storage");
        } 
        try {
            setActivation(activationValue);

        } catch(CodeletActivationBoundsException  ex){
            System.out.println("Activation Bounds exception");
        }
    }
//        if (dgMidTermMemoryScenesIdea != null) {
//            if (dgMidTermMemoryScenesIdea.get(DG_TOTAL_FRAME) != null) {
//                Integer dgScenesFrame = (Integer) dgMidTermMemoryScenesIdea.get(DG_TOTAL_FRAME).getValue();
//                if (currentFrameSaved < dgScenesFrame) {
//                    currentFrameSaved = dgScenesFrame;
//                    activationValue = 1.0;
//                } 
//                try {
//                    setActivation(activationValue);
//                } catch(CodeletActivationBoundsException  ex){
//                    System.out.println("Activation Bounds exception");
//                }
//            }
//        }

    private Boolean hasTimerExpired() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime >= CONSOLIDATION_INTERVAL;
    }
    /**
     * STORES ALL THE TEMPORAL PATTERNS
     */
    private void persistScenes() {
        //store at rootMO
        //verify if there is data
        //scenes to update is the midTermScenesByID
        //from each previous data verify if has to update, if yes, replace with new data, else, just save again
        //if still have scenes to update, save them
        //save DGSize
        if (dgMidTermMemoryScenesIdea.get(MID_TERM_MEMORY_SCENES_BY_ID) == null) {
            return ;
        }
        Map<Integer, Idea> midTermMemoryScenesByID = (Map<Integer, Idea>) dgMidTermMemoryScenesIdea.get(MID_TERM_MEMORY_SCENES_BY_ID).getValue();
        
        HashMap<Integer, Idea> scenesToUpdate = new HashMap<>();
        scenesToUpdate.putAll(midTermMemoryScenesByID);
        ArrayList<String> ideasToBeStored = new ArrayList<String>();
        Idea dgDataIdea = rootInputIdea.get(DG_DATA_IDEA);
        if (dgDataIdea.get(DG_MEMORY_SCENES_IDEA) != null){
            ArrayList<String> previousData = (ArrayList<String>) dgDataIdea.get(DG_MEMORY_SCENES_IDEA).getValue();
            if (previousData != null) {
                for (String line: previousData) {
                    Idea previousScene = storedSceneToIdea(line);
                    Integer previousSceneId = (Integer) previousScene.get(ID_IDEA).getValue();
                    if (scenesToUpdate.containsKey(previousSceneId)) {
                        //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER ACTUALIZADO ANTES DE GUARDAR
                        previousScene = scenesToUpdate.get(previousSceneId);
                        scenesToUpdate.remove(previousSceneId);
                    }
                    ideasToBeStored.add(ideatoStoredScene(previousScene));
                } 
            } 
        }
        
        SortedSet<Integer> sceneIDs = new TreeSet<>(scenesToUpdate.keySet());

        for (Integer sceneID : sceneIDs) {

            Idea scene = scenesToUpdate.get(sceneID);
            ideasToBeStored.add(ideatoStoredScene(scene));
            //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER GUARDADO
        }
        dgDataIdea.setL(new ArrayList<>());
        Idea dgMemoryScenesIdea = new Idea(DG_MEMORY_SCENES_IDEA, ideasToBeStored, "Property", 1);
        Idea dgMemoryScenesDGSizeIdea = new Idea(DG_SIZE_IDEA, ideasToBeStored.size(), "Property", 1);
        dgDataIdea.add(dgMemoryScenesIdea);
        dgDataIdea.add(dgMemoryScenesDGSizeIdea);

        List<Idea> newL = createNewL(rootOutputIdea, dgDataIdea);
        rootOutputIdea.setL(newL);
        rootOutputMO.setI(rootOutputIdea);
    }
    
    private List<Idea> createNewL(Idea currentRootIdea, Idea ideaToAdd) {
        List<Idea> newL = new CopyOnWriteArrayList<>();
        List<Idea> currentL = currentRootIdea.getL();
        String ideaToAddName = ideaToAdd.getName();
        
        for( Idea i : currentL) {
            if (i.getName() != ideaToAddName) {
                newL.add(i);
            }
        }
        newL.add(ideaToAdd);
        return newL;
    }
    
    private String ideatoStoredScene(Idea idea){
        Integer id = (Integer) idea.get(ID_IDEA).getValue();
        String pattern = (String) idea.get(PATTERN_IDEA).getValue();
        Integer repetitions = (Integer) idea.get(REPETITIONS_IDEA).getValue();
        Double positiveAffect = (Double) idea.get(POSITIVE_AFFECT_IDEA).getValue();
        Double negativeAffect = (Double) idea.get(NEGATIVE_AFFECT_IDEA).getValue();
        Double activation = (Double) idea.get(ACTIVATION_IDEA).getValue();
        Integer time = (Integer) idea.get(TIME_IDEA).getValue();
        Long timeStamp = (Long) idea.get(TIMESTAMP_IDEA).getValue();
        
        return id + "," + pattern + "," + String.format("%010d", repetitions) + "," + String.format("%.5f", positiveAffect) + "," + String.format("%.5f", negativeAffect) + "," + String.format("%.5f", activation) + "," + String.format("%020d", time) + "," + timeStamp;
    }
    
     private Idea storedSceneToIdea(String storedString) {
        Idea storedSceneIdea = new Idea(STORED_SCENE_IDEA, null, "Property", 1);
        String data[] = storedString.split(",");
        
        Idea idIdea = new Idea(ID_IDEA, Integer.parseInt(data[0]), "Property", 1);
        Idea patternIdea = new Idea(PATTERN_IDEA,data[1] + "," + data[2] + "," + data[3] + "," + data[4] + "," + data[5], "Property", 1);
        Idea timeIdea = new Idea(TIME_IDEA,Integer.parseInt(data[10]), "Property", 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA,Integer.parseInt(data[6]), "Property", 1);
        Idea positiveAffectIdea = new Idea(POSITIVE_AFFECT_IDEA,Double.parseDouble(data[7]), "Property", 1);
        Idea negativeAffectIdea = new Idea(NEGATIVE_AFFECT_IDEA,Double.parseDouble(data[8]), "Property", 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, Double.parseDouble(data[9]), "Property", 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA,Long.parseLong(data[11]), "Property", 1);
        Idea relationsIdea = new Idea(RELATIONS_IDEA,null, "Property", 1);
        Idea activeSimilarityIdea = new Idea(ACTIVE_SIMILARITY_IDEA,null, "Property", 1);
        Idea recentIdea = new Idea(RECENT_IDEA,false, "Property", 1);
        
        storedSceneIdea.add(idIdea);
        storedSceneIdea.add(patternIdea);
        storedSceneIdea.add(timeIdea);
        storedSceneIdea.add(repetitionsIdea);
        storedSceneIdea.add(positiveAffectIdea);
        storedSceneIdea.add(negativeAffectIdea);
        storedSceneIdea.add(activationIdea);
        storedSceneIdea.add(timestampIdea);
        storedSceneIdea.add(relationsIdea);
        storedSceneIdea.add(activeSimilarityIdea);
        storedSceneIdea.add(recentIdea);
            
        return storedSceneIdea;
    }
    
    
}
