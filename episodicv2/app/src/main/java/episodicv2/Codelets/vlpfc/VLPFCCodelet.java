/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.vlpfc;

import episodicv2.FutureCodelets.*;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeType;

/**
 *
 * @author karenlima
 */
public class VLPFCCodelet extends Codelet {

    
    private MemoryObject vlpfcSpikeMO;
    private Idea vlpfcSpikeIdea;
    
    private MemoryObject vlpfcProcess2SpikeMO;
    private Idea vlpfcProcess2SpikeIdea;
    
    private MemoryObject vlpfcProcess1SpikeMO;
    private Idea vlpfcProcess1SpikeIdea;
    
    private MemoryObject vlpfcProcess3SpikeMO;
    private Idea vlpfcProcess3SpikeIdea;
    
    private MemoryObject dlpfcSpikeMO;
    private Idea dlpfcSpikeIdea;
    
    private byte[] data = null;
    
    public VLPFCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        vlpfcSpikeMO = (MemoryObject) getInput(Configuration.VLPFC_SPIKE_MO);
        vlpfcSpikeIdea = (Idea) vlpfcSpikeMO.getI();
        data = (byte[]) vlpfcSpikeIdea.get(Configuration.SPIKE_VLPFC_DATA_IDEA).getValue();
        
        dlpfcSpikeMO = (MemoryObject) getOutput(Configuration.DLPFC_SPIKE_MO);
        dlpfcSpikeIdea = (Idea) dlpfcSpikeMO.getI();
        
        vlpfcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_PROCESS_1_SPIKE_MO);
        vlpfcProcess1SpikeIdea = (Idea) vlpfcProcess1SpikeMO.getI();
        
        vlpfcProcess2SpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_PROCESS_2_SPIKE_MO);
        vlpfcProcess2SpikeIdea = (Idea) vlpfcProcess2SpikeMO.getI();
        
        vlpfcProcess3SpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_PROCESS_3_SPIKE_MO);
        vlpfcProcess3SpikeIdea = (Idea) vlpfcProcess3SpikeMO.getI();
    }
    
    @Override
    public void proc() {
        SpikeObject spike = SpikeObject.fromBytes(data);

        switch (spike.getId()) {

            case SpikeType.OBJECT_RELATIONS:
                vlpfcProcess2SpikeIdea.setValue(data);
                vlpfcProcess2SpikeMO.setI(vlpfcProcess2SpikeIdea);
//                send(AreaNames.VLPFCProcess2, data);
                break;
            case SpikeType.RETRIEVED_OBJECT_RELATIONS_TOP_DOWN:
                vlpfcProcess2SpikeIdea.setValue(data);
                vlpfcProcess2SpikeMO.setI(vlpfcProcess2SpikeIdea);
//                send(AreaNames.VLPFCProcess2, data);
                dlpfcSpikeIdea.setValue(data);
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
//                send(AreaNames.DLPFC, data);
                break;
            case SpikeType.RETRIEVED_OBJECT_TOP_DOWN:
//                send(AreaNames.VLPFCProcess1, data);
//                send(AreaNames.DLPFC, data);
                vlpfcProcess1SpikeIdea.setValue(data);
                vlpfcProcess1SpikeMO.setI(vlpfcProcess1SpikeIdea);
                dlpfcSpikeIdea.setValue(data);
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
                break;
            case SpikeType.RETRIEVED_AFFECTIVE_OBJECT_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_AFFECTIVE_OBJECT_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_AFFECTIVE_OBJECT_TOP_DOWN:
//                send(AreaNames.VLPFCProcess3, data);
//                send(AreaNames.DLPFC, data);
                vlpfcProcess3SpikeIdea.setValue(data);
                vlpfcProcess3SpikeMO.setI(vlpfcProcess3SpikeIdea);
                dlpfcSpikeIdea.setValue(data);
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
                break;

        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    
}
