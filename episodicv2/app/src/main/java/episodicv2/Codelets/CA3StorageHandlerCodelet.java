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
public class CA3StorageHandlerCodelet extends Codelet {
    
    MemoryObject rootInputMO;
    Idea rootInputIdea;

    MemoryObject newEncodedSceneToStoreMO;
    Idea newEncodedSceneToStoreIdea;
    
    MemoryObject rootOutputMO;
    Idea rootOutputIdea;
    
    //CARGA LAS ESCENAS EXISTENTES
    private static Map<Integer, Idea> midTermMemoryScenesByID  = new HashMap<>();;

    private long startTime;
    
    public CA3StorageHandlerCodelet() {
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
        newEncodedSceneToStoreMO = (MemoryObject) getInput(NEW_ENCODED_SCENE_TO_STORE_MO);
        rootOutputMO = (MemoryObject) getOutput(ROOT_MO);
        try {
            rootInputIdea = (Idea) rootInputMO.getI();
            rootOutputIdea = (Idea) rootOutputMO.getI();
        } catch (NullPointerException ex) {
        }
        
        try {
            newEncodedSceneToStoreIdea = (Idea) newEncodedSceneToStoreMO.getI();
        } catch (NullPointerException ex) {
        }
    }
    
    @Override
    public void proc() {
        if (newEncodedSceneToStoreIdea != null) {
            if (newEncodedSceneToStoreIdea.get(SCENE_IDEA) != null) {
                double affectIntensity = (double) newEncodedSceneToStoreIdea.get(AFFECT_INTENSITY_IDEA).getValue();
                double positiveAffect = (double) newEncodedSceneToStoreIdea.get(POSITIVE_AFFECT_IDEA).getValue();
                double negativeAffect = (double) newEncodedSceneToStoreIdea.get(NEGATIVE_AFFECT_IDEA).getValue();
                Idea sceneIdea = (Idea) newEncodedSceneToStoreIdea.get(SCENE_IDEA);
                storeScene(sceneIdea, positiveAffect, negativeAffect, affectIntensity);
                persistScenes();
            }
        }
    }
    
    @Override
    public void calculateActivation(){
        Double activationValue = 0.0;
        
        if(hasTimerExpired()) {
            activationValue = 1.0;
            startTime = System.currentTimeMillis();
            System.out.println("Timer expired! Executing CA3 Storage");
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
        //TODO: updateAffects instead of removing
    public void storeScene(Idea scene, double positiveAffect, double negativeAffect, double affectIntensity) {
        //if has the same id, then just update the values
        int sceneId = (int) scene.get(ID_IDEA).getValue();
        if (midTermMemoryScenesByID != null) {
            if (midTermMemoryScenesByID.containsKey(sceneId)) {
                midTermMemoryScenesByID.remove(sceneId);
            } 
        }
        midTermMemoryScenesByID.put(sceneId, scene);
    }
    
    
    /**
     * method persistScenes:
    store at rootMO
    verify if there is data
    scenes to update is the midTermScenesByID
    from each previous data verify if has to update, if yes, replace with new data, else, just save again
    if still have scenes to update, save them
    save DGSize     
    **/
    private void persistScenes() {
        HashMap<Integer, Idea> scenesToUpdate = new HashMap<>();
        scenesToUpdate.putAll(midTermMemoryScenesByID);
        ArrayList<String> ideasToBeStored = new ArrayList<String>();
        Idea ca3MemoryScenesIdea = rootInputIdea.get(CA3_MEMORY_SCENES_IDEA);
        if (ca3MemoryScenesIdea != null){
            ArrayList<String> previousData = (ArrayList<String>) ca3MemoryScenesIdea.getValue();
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
        ca3MemoryScenesIdea.setL(new ArrayList<>());
        Idea ideasToBeStoredIdea = new Idea(CA3_MEMORY_SCENES_IDEA, ideasToBeStored, CATEGORY_PROPERTY, 1);
        ca3MemoryScenesIdea.add(ideasToBeStoredIdea);

        List<Idea> newL = createNewL(rootOutputIdea, ca3MemoryScenesIdea);
        rootOutputIdea.setL(newL);
        rootOutputMO.setI(rootOutputIdea);
    }
    
    private Idea storedSceneToIdea(String storedString) {
        Idea storedSceneIdea = new Idea(STORED_SCENE_IDEA, null, CATEGORY_PROPERTY, 1);
        String data[] = storedString.split(",");
        
        Idea idIdea = new Idea(ID_IDEA, Integer.valueOf(data[0]), CATEGORY_PROPERTY, 1);
        Idea patternIdea = new Idea(PATTERN_IDEA,data[1] + "," + data[2] + "," + data[3] + "," + data[4] + "," + data[5], CATEGORY_PROPERTY, 1);
        Idea timeIdea = new Idea(TIME_IDEA,Integer.valueOf(data[10]), CATEGORY_PROPERTY, 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA,Integer.valueOf(data[6]), CATEGORY_PROPERTY, 1);
        Idea positiveAffectIdea = new Idea(POSITIVE_AFFECT_IDEA,Double.valueOf(data[7]), CATEGORY_PROPERTY, 1);
        Idea negativeAffectIdea = new Idea(NEGATIVE_AFFECT_IDEA,Double.valueOf(data[8]), CATEGORY_PROPERTY, 1);
        var activationIdea = new Idea(ACTIVATION_IDEA, Double.valueOf(data[9]), CATEGORY_PROPERTY, 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA,Long.valueOf(data[11]), CATEGORY_PROPERTY, 1);
        Idea relationsIdea = new Idea(RELATIONS_IDEA,null, CATEGORY_PROPERTY, 1);
        Idea activeSimilarityIdea = new Idea(ACTIVE_SIMILARITY_IDEA,null, CATEGORY_PROPERTY, 1);
        Idea recentIdea = new Idea(RECENT_IDEA,false, CATEGORY_PROPERTY, 1);
        
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
    
        private List<Idea> createNewL(Idea currentRootIdea, Idea ideaToAdd) {
        List<Idea> newL = new CopyOnWriteArrayList<>();
        List<Idea> currentL = currentRootIdea.getL();
        String ideaToAddName = ideaToAdd.getName();
        
        for( Idea i : currentL) {
            if (!i.getName().equals(ideaToAddName)) {
                newL.add(i);
            }
        }
        newL.add(ideaToAdd);
        return newL;
    }
}
