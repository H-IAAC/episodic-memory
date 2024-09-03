/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.mpfc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.Codelets.dlpfc.TaskSet;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.Scene;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.storage.mpfc.ScenesWMQueue;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 *
 * @author karenlima
 */
public class MPFCProcess1Codelet extends Codelet {
    
    private MemoryObject mpfcProcess1SpikeMO;
    private Idea mpfcProcess1SpikeIdea;
    
    private MemoryObject dlpfcSpikeMO;
    private Idea dlpfcSpikeIdea;
    
    private MemoryObject encSpikeMO;
    private Idea encSpikeIdea;
    
    
    private ScenesViewer viewer = null;

    private ScenesWMQueue items = ScenesWMQueue.getInstance(7);

    private Scene perceivedScene = null;
    
    //HACER UN OBJETO TASK SET POR AREA DE WORKING MEMORY NO POR VARIABLES
    private String allowedScenePattern = "";

    private TaskSet taskSet = new TaskSet();

    private ArrayList<SceneRelation> sceneRelations = null;
    
    private byte[] data = null;
    
    public MPFCProcess1Codelet() {
        setIsMemoryObserver(true);
        
        viewer = ScenesViewer.getInstance();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                viewer.setVisible(true);
            }
        });
    }

    @Override
    public void accessMemoryObjects() {
        mpfcProcess1SpikeMO = (MemoryObject) getInput(Configuration.MPFC_PROCESS_1_SPIKE_MO);
        mpfcProcess1SpikeIdea = (Idea) mpfcProcess1SpikeMO.getI();
        data = (byte[]) mpfcProcess1SpikeIdea.getValue();
        
        dlpfcSpikeMO = (MemoryObject) getOutput(Configuration.DLPFC_SPIKE_MO);
        dlpfcSpikeIdea = (Idea) dlpfcSpikeMO.getI();
        
        encSpikeMO = (MemoryObject) getOutput(Configuration.ENC_SPIKE_MO);
        encSpikeIdea = (Idea) encSpikeMO.getI();
        
    }
    
    @Override
    public void proc() {
        
        try {

            SpikeObject rawSpike = SpikeObject.fromBytes(data);

            Scene scene = null;
            ArrayList<Scene> similarScenes = null;

            switch (rawSpike.getId()) {

                case SpikeType.NEW_ENCODED_SCENE:

                    
                    scene = (Scene) rawSpike.getObject();

                    items.addItem(scene, rawSpike.getTime());

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            viewer.setItems(items.getItems());
                        }
                    });

                    break;

                case SpikeType.SCENE_RELATIONS:
                case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN:
                case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM:   
                    
                   
                    if (taskSet.getAllowedProcessingFlow() == 0 || rawSpike.getProcessingFlow() == taskSet.getAllowedProcessingFlow()) {

                        sceneRelations = (ArrayList<SceneRelation>) rawSpike.getObject();
                        
                        if (sceneRelations != null) {
                                                        
                            if (!sceneRelations.isEmpty()) {

                               
                                items.addRelations(sceneRelations.get(0).getScene2Id(), sceneRelations);

                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        viewer.setItems(items.getItems());
                                        
                                       
                                        //viewer.setRelations(sceneRelations.get(0).getScene2Id(), sceneRelations);
                                        
                                        viewer.setRelations(items.getRelations());
                                    }
                                });

                            }
                        }
                    }

                    break;

                case SpikeType.SIMILAR_SCENES:
                case SpikeType.RETRIEVED_SCENE_TOP_DOWN:
                case SpikeType.RETRIEVED_SCENE_TOP_DOWN_LTM:
                    
                    //SimpleLogger.log(this, "Received TOP-DOWN SS");
                    
                    if (taskSet.getAllowedProcessingFlow() == 0 || rawSpike.getProcessingFlow() == taskSet.getAllowedProcessingFlow()) {
                        similarScenes = (ArrayList<Scene>) rawSpike.getObject();

                        for (Scene ss : similarScenes) {
                            items.addItem(ss, rawSpike.getTime());

                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                viewer.setItems(items.getItems());
                            }
                        });
                    }

                    break;

                case SpikeType.CURRENT_SCENE: // La que esta viendo actualmente

                   
                    if (taskSet.getAllowedProcessingFlow() == 0 || rawSpike.getProcessingFlow() == taskSet.getAllowedProcessingFlow()) {
                        scene = (Scene) rawSpike.getObject();

                        perceivedScene = scene;

                        items.addItem(scene, rawSpike.getTime());

                        

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                viewer.setItems(items.getItems());
                                viewer.setPerceivedScene(perceivedScene);
                            }
                        });
                    }
                    break;

                case SpikeType.REQUEST_SIMILAR_SCENES:

                    similarScenes = new ArrayList<>();

                    scene = (Scene) rawSpike.getObject();

                    Scene retrievedScene = items.getMostSimilarItem(scene.getPattern());

                    if (retrievedScene != null) {

                        similarScenes.add(retrievedScene);

                        SpikeObject<ArrayList<Scene>> spikeResponse = new SpikeObject(SpikeType.RETRIEVED_SCENE_TOP_DOWN, similarScenes, 0);

//                        send(AreaNames.DLPFC, spikeResponse.toBytes());
                        dlpfcSpikeIdea.setValue(spikeResponse.toBytes());
                        dlpfcSpikeMO.setI(dlpfcSpikeIdea);
                        

                        SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = new SpikeObject(SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN, retrievedScene.getRelations(), 0);
                        
                        dlpfcSpikeIdea.setValue(sceneRelationsSpike.toBytes());
                        dlpfcSpikeMO.setI(dlpfcSpikeIdea);
//                        send(AreaNames.DLPFC, sceneRelationsSpike.toBytes());

                        //SimpleLogger.log(this, "Retrieved pattern: " + retrievedScene.getId() + "," + retrievedScene.getPattern());

                    } else {
                        
                        //SimpleLogger.log(this, "Pattern does not exist, searching in CA3: " + scene.getPattern());
                        encSpikeIdea.setValue(data);
                        encSpikeMO.setI(encSpikeIdea);
//                        send(AreaNames.ENC, data);
                    }

                    break;

                case SpikeType.REQUEST_SCENE_BY_ID:

                    similarScenes = new ArrayList<>();

                    SpikeObject<Integer> idSpike = rawSpike;

                    Scene sceneByID = items.getScene(idSpike.getObject());

                    similarScenes.add(sceneByID);

                    if (sceneByID != null) {

                        SpikeObject<ArrayList<Scene>> spikeResponse = new SpikeObject(SpikeType.RETRIEVED_SCENE_TOP_DOWN, similarScenes, 0);
                        
//                        send(AreaNames.DLPFC, spikeResponse.toBytes());
                        dlpfcSpikeIdea.setValue(spikeResponse.toBytes());
                        dlpfcSpikeMO.setI(dlpfcSpikeIdea);

                        SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = new SpikeObject(SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN, sceneByID.getRelations(), 0);

//                        send(AreaNames.DLPFC, sceneRelationsSpike.toBytes());
                        dlpfcSpikeIdea.setValue(sceneRelationsSpike.toBytes());
                        dlpfcSpikeMO.setI(dlpfcSpikeIdea);

                    } else {
                        //SimpleLogger.log(this, "Scene with ID: " + idSpike.getObject() + " does not exist, search in CA3");
//                        send(AreaNames.ENC, data);
                        encSpikeIdea.setValue(data);
                        encSpikeMO.setI(encSpikeIdea);
                    }

                    break;

                case SpikeType.TASK_SET_ALLOWED_SCENE:

                    allowedScenePattern = (String) rawSpike.getObject();

                    break;
                    
                case SpikeType.TASK_SET:
                    
                    taskSet = ((SpikeObject<TaskSet>)rawSpike).getObject();
                    System.out.println("The Task Set has been changed");
                    
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
