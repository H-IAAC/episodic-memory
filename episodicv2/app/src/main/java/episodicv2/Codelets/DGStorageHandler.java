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

/**
 *
 * @author karenlima
 */
public class DGStorageHandler extends Codelet {
    
    MemoryObject rootMO;
    Idea rootIdea;

    MemoryObject dgMidTermMemoryScenesMO;
    Idea dgMidTermMemoryScenesIdea;
    
    MemoryObject rootOutputMO;
    Idea rootOutputIdea;
    
    Integer savedTimes = 0;

    public DGStorageHandler() {
        try {
            setThreshold(0.5);
            
        } catch(CodeletThresholdBoundsException  ex){
            System.out.println("Threshold Bounds exception");
        }
        
    }
    
    @Override
    public void accessMemoryObjects() {
        rootMO = (MemoryObject) getInput(ROOT_MO);
        dgMidTermMemoryScenesMO = (MemoryObject) getInput(DG_MID_TERM_MEMORY_SCENES_MO);
        rootOutputMO = (MemoryObject) getOutput(ROOT_MO);
        try {
            rootIdea = (Idea) rootMO.getI();
            dgMidTermMemoryScenesIdea = (Idea) dgMidTermMemoryScenesMO.getI();
            rootOutputIdea = (Idea) rootMO.getI();
        } catch (NullPointerException ex) {
            System.out.println("Root MO is null");
        }
    }
    
    @Override
    public void proc() {
        persistScenes();
        savedTimes+=1;
        System.out.println("Scenes from DG persisted on rootMO" + savedTimes);
        
        
        //        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());
//
//        startConsolidation()
    
//      public void startConsolidation() { 
//        //each X seconds it persists the scenes
//
//        long delay = Configuration.CONSOLIDATION_INTERVAL;
//
//        consolidationTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //SimpleLogger.log(this, "Storing...");
//                persistScenes();
//            }
//        }, delay, delay);
//    }
    }
    
    @Override
    public void calculateActivation(){
        Double activationValue = 0.0;
        try {
            Integer dgSize = (Integer) dgMidTermMemoryScenesIdea.get(DG_SIZE_IDEA).getValue();
            if (dgSize > 0) {
                activationValue = 1.0;
                setTimeStep(CONSOLIDATION_INTERVAL);
            } 
        } catch (NullPointerException ex) {
            System.out.println("dgMidTermMemoryScenesIdea is null");
        }
            
        
        
            
        
        try {
            setActivation(activationValue);
            
        } catch(CodeletActivationBoundsException  ex){
            System.out.println("Activation Bounds exception");
        }
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
        
        Map<Integer, Idea> midTermMemoryScenesByID = (Map<Integer, Idea>) dgMidTermMemoryScenesIdea.get(MID_TERM_MEMORY_SCENES_BY_ID).getValue();
        Idea dgDataIdea = rootIdea.get(DG_DATA_IDEA);
        HashMap<Integer, Idea> scenesToUpdate = new HashMap<>();
        scenesToUpdate.putAll(midTermMemoryScenesByID);
        ArrayList<String> ideasToBeStored = new ArrayList<String>();
        if ( dgDataIdea.get(DG_MEMORY_SCENES_IDEA) != null){
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
        List<Idea> ideaslist = new ArrayList<>();
        ideaslist.add(dgDataIdea);
        rootOutputIdea.get(DG_DATA_IDEA).setL(ideaslist);
        rootOutputMO.setI(rootOutputIdea);
        
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
