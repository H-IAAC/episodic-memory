/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.configuration.Configuration.*;
import episodicv2.emotions.ActivationFunctions;
import episodicv2.emotions.AssociationQueue;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class CA1Process1Codelet extends Codelet {
    MemoryObject recentNewEncodedSceneSpikeMO;
    Idea recentNewEncodedSceneSpikeIdea;
    
    MemoryObject sceneRelationVertexToStoreMO;
    Idea sceneRelationVertexToStoreIdea;
    
    private AssociationQueue queue = AssociationQueue.getInstance();
    private EmotionalDecay emotionalDecay = null;
    private double affectIntensity = 0.0;
    
    @Override
    public void accessMemoryObjects() {
        System.out.println("[CA1P1] Executing accessMemoryObjects CA1Process1Codelet");
        recentNewEncodedSceneSpikeMO = (MemoryObject) getInput(RECENT_NEW_ENCODED_SCENE_SPIKE_MO);
        recentNewEncodedSceneSpikeIdea = (Idea) recentNewEncodedSceneSpikeMO.getI();
        
        sceneRelationVertexToStoreMO = (MemoryObject) getOutput(SCENE_RELATION_VERTEX_TO_STORE_MO);
        sceneRelationVertexToStoreIdea = (Idea) sceneRelationVertexToStoreMO.getI();
    }
    
    @Override
    public void proc() {
        initComponents();
        
        Idea scene = null;
//        switch (spikeId) {
//            case SpikeType.NEW_ENCODED_SCENE:
        scene = recentNewEncodedSceneSpikeIdea.get(SCENE_IDEA);      
        if (scene != null) {
            if (scene.getId() != 0) {

                int tuple[] = queue.addSceneId((Integer) scene.get(ID_IDEA).getValue());

                if (tuple != null) {

                    affectIntensity = emotionalDecay.getActivation();
                    Integer time = (Integer) recentNewEncodedSceneSpikeIdea.get(CURRENT_FRAME_IDEA).getValue();      
                    Idea sr = createSceneRelationIdea(tuple[0], tuple[1], time);
                    sceneRelationVertexToStoreIdea.setL(new ArrayList<>());
                    sceneRelationVertexToStoreIdea.add(new Idea(AFFECT_INTENSITY_IDEA, affectIntensity, "Property", 1));
                    sceneRelationVertexToStoreIdea.add(sr);
                    sceneRelationVertexToStoreMO.setI(sceneRelationVertexToStoreIdea);
//                    graph.addVertex(sr, affectIntensity);

                    //graph.printGraph();
                } else {
                    System.out.println("Empty queue");
                }
            }
        }
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
    
    private Idea createSceneRelationIdea(int scene1Id, int scene2Id, int time) {
        
        Idea sceneRelationIdea = new Idea(SCENE_RELATION_IDEA, null, "Property", 1);
        Idea scene1IdIdea = new Idea(SCENE_ID_1_IDEA,scene1Id, "Property", 1);
        Idea scene2IdIdea = new Idea(SCENE_ID_2_IDEA,scene2Id, "Property", 1);
        Integer repetitions = 0;
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA,repetitions, "Property", 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA,ActivationFunctions.sigmoid(repetitions * SIGMOID_SCALE), "Property", 1);
        Idea timeIdea = new Idea(TIME_IDEA,time, "Property", 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA,System.currentTimeMillis(), "Property", 1);
        Idea recent = new Idea(RECENT_IDEA,true, "Property", 1);
        Idea updated = new Idea(UPDATED_IDEA,false, "Property", 1);

        sceneRelationIdea.add(scene1IdIdea);
        sceneRelationIdea.add(scene2IdIdea);
        sceneRelationIdea.add(activationIdea);
        sceneRelationIdea.add(repetitionsIdea);
        sceneRelationIdea.add(timeIdea);
        sceneRelationIdea.add(timestampIdea);
        sceneRelationIdea.add(recent);
        sceneRelationIdea.add(updated);
        
        return sceneRelationIdea;
    }
    
}
