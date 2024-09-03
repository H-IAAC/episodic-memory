/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.ca3;

import episodicv2.utils.SimpleLogger;
import java.util.ArrayList;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.entities.Scene;
import episodicv2.core.spike.TopDownParameters;
import episodicv2.core.storage.CA3StorageHandler;


/**
 *
 * @author karenlima
 */
public class CA3Process3Codelet extends Codelet {
    
    private MemoryObject ca3Process3SpikeMO;
    private Idea ca3Process3SpikeIdea;
    
    private MemoryObject ca1SpikeMO;
    private Idea ca1SpikeIdea;
    
    private byte[] data = null;
    
    private static CA3StorageHandler ca3StorageHandler = CA3StorageHandler.getInstance(Configuration.LOAD_MEMORY);

    public CA3Process3Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        ca3Process3SpikeMO = (MemoryObject) getInput(Configuration.CA3_PROCESS_3_SPIKE_MO);
        ca3Process3SpikeIdea = (Idea) ca3Process3SpikeMO.getI();
        data = (byte[]) ca3Process3SpikeIdea.getValue();

        ca1SpikeMO = (MemoryObject) getOutput(Configuration.CA1_SPIKE_MO);
        ca1SpikeIdea = (Idea) ca1SpikeMO.getI();
    }
    
    @Override
    public void proc() {
        
        //SimpleLogger.log(this, "Searching in MTM and LTM of CA3");

        try {

            SpikeObject rawSpike = SpikeObject.fromBytes(data);

            if (rawSpike.getId() == SpikeType.REQUEST_SIMILAR_SCENES) {

                //SCENE SEARCH BY SIMILARITY
                SpikeObject<Scene> spike = SpikeObject.fromBytes(data);
                Scene scene = spike.getObject();

                ArrayList<Scene> similarScenes = ca3StorageHandler.retrieveSimilarScenesTopDown(scene.getPattern(), 0.0, 0.0, 0.0, 0, System.currentTimeMillis(), false);

                for (Scene s : similarScenes) {
                    SimpleLogger.log(this, s.toString());
                }

                SpikeObject<ArrayList<Scene>> similarScenesSpike = new SpikeObject(SpikeType.RETRIEVED_SCENE_TOP_DOWN, similarScenes, 0);
                similarScenesSpike.setProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);
                
                ca1SpikeIdea.setValue(similarScenesSpike.toBytes());
                ca1SpikeMO.setI(ca1SpikeIdea);
//                send(AreaNames.CA1, similarScenesSpike.toBytes());

            } else if (rawSpike.getId() == SpikeType.REQUEST_SCENE_BY_ID) {

                ArrayList<Scene> sceneWrapper = new ArrayList<>();

                SpikeObject<Integer> spike = rawSpike;

                Scene scene = ca3StorageHandler.retrieveSceneByID(spike.getObject(), System.currentTimeMillis());

                if (scene != null) {
                    sceneWrapper.add(scene);

                } else {
                    SimpleLogger.log(this, "The scene could not be found");
                }

                SpikeObject<ArrayList<Scene>> similarScenesSpike = new SpikeObject(SpikeType.RETRIEVED_SCENE_TOP_DOWN, sceneWrapper, 0);
                similarScenesSpike.setProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);
                
                ca1SpikeIdea.setValue(similarScenesSpike.toBytes());
                ca1SpikeMO.setI(ca1SpikeIdea);
//                send(AreaNames.CA1, similarScenesSpike.toBytes());

            } else if (rawSpike.getId() == SpikeType.REQUEST_SCENE_BY_AFFECT) {

                ArrayList<Scene> sceneWrapper = new ArrayList<>();
                int request = (int) rawSpike.getObject();

                Scene affectiveScene = null;
                int returnedType = SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN;

                switch (request) {

                    case TopDownParameters.MAX_POSITIVE_AFFECT:

                        affectiveScene = ca3StorageHandler.retrieveMostPositiveAffectiveScene();
                        returnedType = SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN;
                        break;

                    case TopDownParameters.MIN_POSITIVE_AFFECT:

                        affectiveScene = ca3StorageHandler.retrieveLessPositiveAffectiveScene();
                        returnedType = SpikeType.RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN;

                        break;
                        
                    case TopDownParameters.MAX_NEGATIVE_AFFECT:
                        
                        affectiveScene = ca3StorageHandler.retrieveMostNegativeAffectiveScene();
                        returnedType = SpikeType.RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN;
                        
                        break;
                        
                    case TopDownParameters.MIN_NEGATIVE_AFFECT:

                        affectiveScene = ca3StorageHandler.retrieveLessNegativeAffectiveScene();
                        returnedType = SpikeType.RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN;
                        
                        break;
                }

                sceneWrapper.add(affectiveScene);

                SpikeObject<ArrayList<Scene>> spikeRelations = new SpikeObject(returnedType, sceneWrapper, 0);
                spikeRelations.setProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);
                ca1SpikeIdea.setValue(spikeRelations.toBytes());
                ca1SpikeMO.setI(ca1SpikeIdea);
//                send(AreaNames.CA1, spikeRelations.toBytes());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
