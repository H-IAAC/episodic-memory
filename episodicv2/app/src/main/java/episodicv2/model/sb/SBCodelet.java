/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.sb;

import episodicv2.FutureCodelets.*;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;

/**
 *
 * @author karenlima
 */
public class SBCodelet extends Codelet {
    
    private MemoryObject sbSpikeMO;
    private Idea sbSpikeIdea;
    
    private MemoryObject encSpikeMO;
    private Idea encSpikeIdea;
    
    private byte[] data = null;

    @Override
    public void accessMemoryObjects() {
        
        sbSpikeMO = (MemoryObject) getInput(Configuration.SB_SPIKE_MO);
        sbSpikeIdea = (Idea) sbSpikeMO.getI();
        data = (byte[]) sbSpikeIdea.get(Configuration.SPIKE_SB_DATA_IDEA).getValue();

        encSpikeMO = (MemoryObject) getInput(Configuration.ENC_SPIKE_MO);
        encSpikeIdea = (Idea) encSpikeMO.getI();
        
    }
    
    @Override
    public void proc() {
//        send(AreaNames.ENC, data);
        encSpikeIdea.setValue(data);
        encSpikeMO.setI(encSpikeIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
