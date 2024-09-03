/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;

/**
 *
 * @author karenlima
 */
public class PPCCodelet extends Codelet {
    
    private MemoryObject ppcSpikeMO;
    private Idea ppcSpikeIdea;

    private MemoryObject phcSpikeMO;
    private Idea phcSpikeIdea;    
    
    private String data = null;
    
    public PPCCodelet() {
        setIsMemoryObserver(true);
    }


    @Override
    public void accessMemoryObjects() {
        ppcSpikeMO = (MemoryObject) getInput(Configuration.PPC_SPIKE_MO);
        ppcSpikeIdea = (Idea) ppcSpikeMO.getI();
        data = (String) ppcSpikeIdea.getValue();
        
        phcSpikeMO = (MemoryObject) getOutput(Configuration.PHC_SPIKE_MO);
        phcSpikeIdea = (Idea) phcSpikeMO.getI();
    }
    
    @Override
    public void proc() {
//        send(AreaNames.PHC, data.getBytes());
        phcSpikeIdea.setValue(data.getBytes());
        phcSpikeMO.setI(phcSpikeIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
