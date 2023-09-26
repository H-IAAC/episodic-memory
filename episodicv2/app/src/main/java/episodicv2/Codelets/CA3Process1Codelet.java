/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.configuration.Configuration.*;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class CA3Process1Codelet extends Codelet {
    
    MemoryObject newEncodedSceneSpikeMO;
    Idea newEncodedSceneSpikeIdea;
    
    MemoryObject recentNewEncodedSceneSpikeMO;
    Idea recentNewEncodedSceneSpikeIdea;
    
    MemoryObject newEncodedSceneToStoreMO;
    Idea newEncodedSceneToStoreIdea;
    
    private double affectIntensity = 0.0;
    private EmotionalDecay emotionalDecay = null;
    
    @Override
    public void accessMemoryObjects() {
        System.out.println("[CA3P1] Executing accessMemoryObjects CA3Process1Codelet");
        newEncodedSceneSpikeMO = (MemoryObject) getInput(NEW_ENCODED_SCENE_SPIKE_MO);
        newEncodedSceneSpikeIdea = (Idea) newEncodedSceneSpikeMO.getI();
        
        recentNewEncodedSceneSpikeMO = (MemoryObject) getOutput(RECENT_NEW_ENCODED_SCENE_SPIKE_MO);
        recentNewEncodedSceneSpikeIdea = (Idea) recentNewEncodedSceneSpikeMO.getI();
        
        newEncodedSceneToStoreMO = (MemoryObject) getOutput(NEW_ENCODED_SCENE_TO_STORE_MO);
        newEncodedSceneToStoreIdea = (Idea) newEncodedSceneToStoreMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("[CA3P1] Executing proc CA3Process1Codelet");
        initComponents();
        Idea sceneIdea = (Idea) newEncodedSceneSpikeIdea.get(SCENE_IDEA);
        System.out.println("Scene Idea CA3 childs");
        System.out.println(sceneIdea.getL());
        sceneIdea.get(RECENT_IDEA).setValue(true);
        recentNewEncodedSceneSpikeIdea.add(sceneIdea);
        recentNewEncodedSceneSpikeIdea.add(new Idea(CURRENT_FRAME_IDEA, newEncodedSceneSpikeIdea.get(CURRENT_FRAME_IDEA).getValue(), "Property", 1));

        recentNewEncodedSceneSpikeMO.setI(recentNewEncodedSceneSpikeIdea);
        
        affectIntensity = emotionalDecay.getActivation();
        
        newEncodedSceneToStoreIdea.setL(new ArrayList<>());
        newEncodedSceneToStoreIdea.add(sceneIdea);
        newEncodedSceneToStoreIdea.add(new Idea(AFFECT_INTENSITY_IDEA, affectIntensity, "Property", 1));
        newEncodedSceneToStoreIdea.add(new Idea(POSITIVE_AFFECT_IDEA, sceneIdea.get(POSITIVE_AFFECT_IDEA).getValue(), "Property", 1));
        newEncodedSceneToStoreIdea.add(new Idea(NEGATIVE_AFFECT_IDEA, sceneIdea.get(NEGATIVE_AFFECT_IDEA).getValue(), "Property", 1));
        newEncodedSceneToStoreMO.setI(newEncodedSceneToStoreIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    private void initComponents() {
        if (emotionalDecay == null) {
            emotionalDecay = new EmotionalDecay(this.getClass().getName());
            emotionalDecay.start();
        }
    }
    
}
