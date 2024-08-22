/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.ca1;

import java.util.ArrayList;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.entities.Scene;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.storage.ca1.CA1StorageHandler;


/**
 *
 * @author karenlima
 */
public class CA1Process3Codelet extends Codelet {
    private MemoryObject ca1Process3SpikeMO;
    private Idea ca1Process3SpikeIdea;
    
    private MemoryObject sbSpikeMO;
    private Idea sbSpikeIdea;
    
    private byte[] data = null;
    
    private CA1StorageHandler graph = CA1StorageHandler.getInstance(Configuration.LOAD_MEMORY);

    public CA1Process3Codelet(){
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        ca1Process3SpikeMO = (MemoryObject) getOutput(Configuration.CA1_PROCESS_2_SPIKE_MO);
        ca1Process3SpikeIdea = (Idea) ca1Process3SpikeMO.getI();
        data = (byte[]) ca1Process3SpikeIdea.get(Configuration.SPIKE_CA1_PROCESS_2_DATA_IDEA).getValue();

        sbSpikeMO = (MemoryObject) getOutput(Configuration.SB_SPIKE_MO);
        sbSpikeIdea = (Idea) sbSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        

        try {

            SpikeObject rawSpike = SpikeObject.fromBytes(data);
            Scene scene = null;
            int spikeType = SpikeType.SCENE_RELATIONS;

            if (rawSpike.getId() == SpikeType.NEW_ENCODED_SCENE) {

                scene = (Scene) rawSpike.getObject();
                spikeType = SpikeType.SCENE_RELATIONS;

                ArrayList<SceneRelation> sceneRelations = graph.retrieveSceneRelationsLTM(scene.getId(), System.currentTimeMillis());

                //Send to SB to reach working memory
                SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = new SpikeObject(spikeType, sceneRelations, rawSpike.getTime());
                sbSpikeIdea.setValue(sceneRelationsSpike.toBytes());
                sbSpikeMO.setI(sbSpikeIdea);
//                send(AreaNames.SB, sceneRelationsSpike.toBytes());

            } else if (rawSpike.getId() == SpikeType.SIMILAR_SCENES) {

                //SEND ALL THE RELATIONS OF THE SIMILAR SCENES, NOT JUST OF THE MOST SIMILAR
                ArrayList<Scene> similarScenes = (ArrayList<Scene>) rawSpike.getObject();
                //scene = similarScenes.get(0); //The most similar

                spikeType = SpikeType.SCENE_RELATIONS;

                ArrayList<SceneRelation> sceneRelations = new ArrayList<>();

                for (Scene sc : similarScenes) {
                    sceneRelations = graph.retrieveSceneRelationsLTM(sc.getId(), System.currentTimeMillis());

                    SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = new SpikeObject(spikeType, sceneRelations, rawSpike.getTime());
                    sbSpikeIdea.setValue(sceneRelationsSpike.toBytes());
                    sbSpikeMO.setI(sbSpikeIdea);
//                    send(AreaNames.SB, sceneRelationsSpike.toBytes());
                }

            } else if (rawSpike.getId() == SpikeType.RETRIEVED_SCENE_TOP_DOWN_LTM) {

                //SEND ALL THE RELATIONS OF THE SIMILAR SCENES, NOT JUST OF THE MOST SIMILAR
                ArrayList<Scene> similarScenes = (ArrayList<Scene>) rawSpike.getObject();
                //scene = similarScenes.get(0); //The most similar

                ArrayList<SceneRelation> sceneRelations = new ArrayList<>();

                spikeType = SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM;

                if (!similarScenes.isEmpty()) {

                    for (Scene sc : similarScenes) {

                        sceneRelations = graph.retrieveSceneRelationsLTM(sc.getId(), System.currentTimeMillis());

                        SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = new SpikeObject(spikeType, sceneRelations, rawSpike.getTime());
                        sceneRelationsSpike.setProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);
                        sbSpikeIdea.setValue(sceneRelationsSpike.toBytes());
                        sbSpikeMO.setI(sbSpikeIdea);
//                        send(AreaNames.SB, sceneRelationsSpike.toBytes());

                    }

                } else {

                    SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = new SpikeObject(spikeType, sceneRelations, rawSpike.getTime());
                    sceneRelationsSpike.setProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);
                    sbSpikeIdea.setValue(sceneRelationsSpike.toBytes());
                    sbSpikeMO.setI(sbSpikeIdea);
//                    send(AreaNames.SB, sceneRelationsSpike.toBytes());

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
