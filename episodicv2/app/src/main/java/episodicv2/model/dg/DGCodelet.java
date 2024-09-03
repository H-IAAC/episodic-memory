/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.dg;

import episodicv2.FutureCodelets.*;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;

/**
 *
 * @author karenlima
 */
public class DGCodelet extends Codelet {
    
    private MemoryObject dgSpikeMO;
    private Idea dgSpikeIdea;   
    
    private MemoryObject dgProcess1SpikeMO;
    private Idea dgProcess1SpikeIdea;   
    
    private byte[] data = null;
    
    public DGCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        dgSpikeMO = (MemoryObject) getInput(Configuration.DG_SPIKE_MO);
        dgSpikeIdea = (Idea) dgSpikeMO.getI();
        data = (byte[]) dgSpikeIdea.getValue();
        
        dgProcess1SpikeMO = (MemoryObject) getOutput(Configuration.DG_PROCESS_1_SPIKE_MO);
        dgProcess1SpikeIdea = (Idea) dgProcess1SpikeMO.getI();
    }
    
    @Override
    public void proc() {
//        send(AreaNames.DGProcess1, data);
        dgProcess1SpikeIdea.setValue(data);
        dgProcess1SpikeMO.setI(dgProcess1SpikeIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
