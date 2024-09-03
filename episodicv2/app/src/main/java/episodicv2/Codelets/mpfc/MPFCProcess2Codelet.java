/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.mpfc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.Scene;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.spike.TopDownParameters;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class MPFCProcess2Codelet extends Codelet {
    
    private MemoryObject mpfcProcess2SpikeMO;
    private Idea mpfcProcess2SpikeIdea;
    
    private MemoryObject encSpikeMO;
    private Idea encSpikeIdea;
    
    private byte[] data = null;
    
    private Scene maxPositiveAffectiveScene;
    private Scene lessPositiveAffectiveScene;
    private Scene maxNegativeAffectiveScene;
    private Scene lessNegativeAffectiveScene;
    
    
    public MPFCProcess2Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        mpfcProcess2SpikeMO = (MemoryObject) getInput(Configuration.MPFC_PROCESS_2_SPIKE_MO);
        mpfcProcess2SpikeIdea = (Idea) mpfcProcess2SpikeMO.getI();
        data = (byte[]) mpfcProcess2SpikeIdea.getValue();
        
        encSpikeMO = (MemoryObject) getOutput(Configuration.ENC_SPIKE_MO);
        encSpikeIdea = (Idea) encSpikeMO.getI();
        
    }
    
    @Override
    public void proc() {
        
        try {

            SpikeObject rawSpike = SpikeObject.fromBytes(data);

            switch (rawSpike.getId()) {
                case SpikeType.REQUEST_SCENE_BY_AFFECT:

                    SpikeObject<Integer> requestSpike = rawSpike;

                    evaluateRequest(requestSpike, data);

                    break;

                case SpikeType.RETRIEVED_AFFECTIVE_SCENE_TOP_DOWN:
                    break;

                case SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN:

                    SpikeObject<ArrayList<Scene>> mostASpike = rawSpike;

                    maxPositiveAffectiveScene = mostASpike.getObject().get(0);

                    break;

                case SpikeType.RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN:

                    SpikeObject<ArrayList<Scene>> lessASpike = rawSpike;

                    lessPositiveAffectiveScene = lessASpike.getObject().get(0);

                    break;

                case SpikeType.RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN:

                    SpikeObject<ArrayList<Scene>> mostNASpike = rawSpike;

                    maxNegativeAffectiveScene = mostNASpike.getObject().get(0);

                    break;

                case SpikeType.RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN:

                    SpikeObject<ArrayList<Scene>> lessNASpike = rawSpike;

                    lessNegativeAffectiveScene = lessNASpike.getObject().get(0);

                    break;

                case SpikeType.RETRIEVED_AFFECTIVE_SCENE_RELATION_TOP_DOWN:

                    SpikeObject<ArrayList<SceneRelation>> sceneRelationsSpike = rawSpike;
                    /*
                    for(SceneRelation sr: sceneRelationsSpike.getObject()){
                        System.out.println(sr.toString());
                    }*/

                    break;
            }

        } catch (Exception e) {
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    private void evaluateRequest(SpikeObject<Integer> requestSpike, byte[] data) {

        switch (requestSpike.getObject()) {
            case TopDownParameters.MAX_POSITIVE_AFFECT:

                if (maxPositiveAffectiveScene != null) {
                    System.out.println("[Exists] " + maxPositiveAffectiveScene.toString());
                } else {
                    encSpikeIdea.setValue(data);
                    encSpikeMO.setI(encSpikeIdea);
//                    send(AreaNames.ENC, data);
                }

                break;
            case TopDownParameters.MIN_POSITIVE_AFFECT:

                if (lessPositiveAffectiveScene != null) {
                    System.out.println("[Exists] " + lessPositiveAffectiveScene.toString());
                } else {
                    encSpikeIdea.setValue(data);
                    encSpikeMO.setI(encSpikeIdea);
//                    send(AreaNames.ENC, data);
                }
                break;
            case TopDownParameters.MAX_NEGATIVE_AFFECT:
                if (maxNegativeAffectiveScene != null) {
                    System.out.println("[Exists] " + maxNegativeAffectiveScene.toString());
                } else {
                    encSpikeIdea.setValue(data);
                    encSpikeMO.setI(encSpikeIdea);
//                    send(AreaNames.ENC, data);
                }
                break;
            case TopDownParameters.MIN_NEGATIVE_AFFECT:
                if (lessNegativeAffectiveScene != null) {
                    System.out.println("[Exists] " + lessNegativeAffectiveScene.toString());
                } else {
                    encSpikeIdea.setValue(data);
                    encSpikeMO.setI(encSpikeIdea);
//                    send(AreaNames.ENC, data);
                }
                break;
        }

    }
    
}
