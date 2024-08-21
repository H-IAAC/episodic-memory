/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.itc;

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
public class ITCCodelet extends Codelet {
    
    private MemoryObject itcSpikeMO;
    private Idea itcSpikeIdea;
    private MemoryObject itcProcess1SpikeMO;
    private Idea itcProcess1SpikeIdea;
    
    
    private byte[] data = null;
    
    
    public ITCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        itcSpikeMO = (MemoryObject) getInput(Configuration.ITC_SPIKE_MO);
        itcSpikeIdea = (Idea) itcSpikeMO.getI();
        
        data = (byte[]) itcSpikeIdea.get(Configuration.SPIKE_ITC_DATA_IDEA).getValue();
        
        itcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.ITC_PROCESS_1_SPIKE_MO);
        itcProcess1SpikeIdea = (Idea) itcProcess1SpikeMO.getI();
        
    }
    
    @Override
    public void proc() {
        SpikeObject spike = SpikeObject.fromBytes(data);

        switch (spike.getId()) {

            case SpikeType.REQUEST_OBJECT:
                //SEARCH IN MTM
                itcProcess1SpikeIdea.setValue(data);
                itcProcess1SpikeMO.setI(itcProcess1SpikeIdea);
//                send(AreaNames.ITCProcess1, data);

                //SEARCH IN LTM
                //send(AreaNames.ITCProcess2, data);
                break;

        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
