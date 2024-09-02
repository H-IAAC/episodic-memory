/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.enc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;

/**
 *
 * @author karenlima
 */
public class ENCCodelet extends Codelet {
    
    private MemoryObject encSpikeMO;
    private Idea encSpikeIdea;
    
    private MemoryObject ca3SpikeMO;
    private Idea ca3SpikeIdea;

    private MemoryObject dgSpikeMO;
    private Idea dgSpikeIdea;   
    
    private MemoryObject mpfcSpikeMO;
    private Idea mpfcSpikeIdea;   
    
    private byte[] data = null;
    
    public ENCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        encSpikeMO = (MemoryObject) getInput(Configuration.ENC_SPIKE_MO);
        encSpikeIdea = (Idea) encSpikeMO.getI();
        data = (byte[]) encSpikeIdea.get(Configuration.SPIKE_ENC_DATA_IDEA).getValue();
        
        dgSpikeMO = (MemoryObject) getOutput(Configuration.DG_SPIKE_MO);
        dgSpikeIdea = (Idea) dgSpikeMO.getI();
        
        ca3SpikeMO = (MemoryObject) getOutput(Configuration.CA3_SPIKE_MO);
        ca3SpikeIdea = (Idea) ca3SpikeMO.getI();
        
        mpfcSpikeMO = (MemoryObject) getOutput(Configuration.MPFC_SPIKE_MO);
        mpfcSpikeIdea = (Idea) mpfcSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        
        if(data == null) {
            return;
        }
        SpikeObject spike = SpikeObject.fromBytes(data);

        switch (spike.getId()) {
            case SpikeType.RECOGNIZED_OBJECTS:
            case SpikeType.UNINTEGRATED_SCENE_PATTERN:
                dgSpikeIdea.setValue(data);
                dgSpikeMO.setI(dgSpikeIdea);
                ca3SpikeIdea.setValue(data);
                ca3SpikeMO.setI(ca3SpikeIdea);
//                send(AreaNames.DG, data);
//                send(AreaNames.CA3, data);
                break;
            case SpikeType.NEW_ENCODED_SCENE:
            case SpikeType.SCENE_RELATIONS:
            case SpikeType.SIMILAR_SCENES:
            case SpikeType.CURRENT_SCENE:
            case SpikeType.RETRIEVED_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_SCENE_TOP_DOWN_LTM:
            case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN:
            case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM:
            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_RELATION_TOP_DOWN:
                mpfcSpikeIdea.setValue(data);
                mpfcSpikeMO.setI(mpfcSpikeIdea);
//                send(AreaNames.MPFC, data);
                break;
            case SpikeType.REQUEST_SIMILAR_SCENES:
            case SpikeType.REQUEST_SCENE_BY_ID:
            case SpikeType.REQUEST_SCENE_BY_AFFECT:
                ca3SpikeIdea.setValue(data);
                ca3SpikeMO.setI(ca3SpikeIdea);
//                send(AreaNames.CA3, data);
                break;
            case SpikeType.AFFECTIVE_VALUE:
                dgSpikeIdea.setValue(data);
                dgSpikeMO.setI(dgSpikeIdea);
//                send(AreaNames.DG, data);
                break;
            default:
                break;
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
