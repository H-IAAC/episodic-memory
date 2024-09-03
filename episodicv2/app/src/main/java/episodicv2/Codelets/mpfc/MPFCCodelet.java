/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.mpfc;

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
public class MPFCCodelet extends Codelet {
    
    private byte[] data = null;
    private MemoryObject mpfcSpikeMO;
    private Idea mpfcSpikeIdea;
    private MemoryObject mpfcProcess1SpikeMO;
    private Idea mpfcProcess1SpikeIdea;
    private MemoryObject mpfcProcess2SpikeMO;
    private Idea mpfcProcess2SpikeIdea;
    private MemoryObject dlpfcSpikeMO;
    private Idea dlpfcSpikeIdea;
    
    public MPFCCodelet(){
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        mpfcSpikeMO = (MemoryObject) getInput(Configuration.MPFC_SPIKE_MO);
        mpfcSpikeIdea = (Idea) mpfcSpikeMO.getI();
        data = (byte[]) mpfcSpikeIdea.getValue();
        
        mpfcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.MPFC_PROCESS_1_SPIKE_MO);
        mpfcProcess1SpikeIdea = (Idea) mpfcProcess1SpikeMO.getI();
        
        mpfcProcess2SpikeMO = (MemoryObject) getOutput(Configuration.MPFC_PROCESS_2_SPIKE_MO);
        mpfcProcess2SpikeIdea = (Idea) mpfcProcess2SpikeMO.getI();
        
        dlpfcSpikeMO = (MemoryObject) getOutput(Configuration.DLPFC_SPIKE_MO);
        dlpfcSpikeIdea = (Idea) dlpfcSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        
        SpikeObject spike = SpikeObject.fromBytes(data);

        switch (spike.getId()) {
            case SpikeType.CURRENT_SCENE:
                dlpfcSpikeIdea.setValue(data);
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
                
                mpfcProcess1SpikeIdea.setValue(data);
                mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//                send(AreaNames.DLPFC, data);
//                send(AreaNames.MPFCProcess1, data);
                break;
            case SpikeType.RETRIEVED_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_SCENE_TOP_DOWN_LTM:
            case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN:
            case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM:
                dlpfcSpikeIdea.setValue(data);
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
                
                mpfcProcess1SpikeIdea.setValue(data);
                mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//                send(AreaNames.DLPFC, data);
//                send(AreaNames.MPFCProcess1, data);
                break;
            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_RELATION_TOP_DOWN:
                dlpfcSpikeIdea.setValue(data);
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
                
                mpfcProcess2SpikeIdea.setValue(data);
                mpfcProcess2SpikeMO.setI(mpfcProcess2SpikeIdea);
                
//                send(AreaNames.DLPFC, data);
//                send(AreaNames.MPFCProcess2, data);
                break;
            default:
                mpfcProcess1SpikeIdea.setValue(data);
                mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//                send(AreaNames.MPFCProcess1, data);
                break;
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
