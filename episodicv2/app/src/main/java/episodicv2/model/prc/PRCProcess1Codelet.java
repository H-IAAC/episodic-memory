/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.prc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class PRCProcess1Codelet extends Codelet {
    
    private MemoryObject prcProcess1SpikeMO;
    private Idea prcProcess1SpikeIdea;
    
    private MemoryObject encSpikeMO;
    private Idea encSpikeIdea;
    
    private byte[] data = null;
    
    
    public PRCProcess1Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        prcProcess1SpikeMO = (MemoryObject) getInput(Configuration.PRC_PROCESS_1_SPIKE_MO);
        prcProcess1SpikeIdea = (Idea) prcProcess1SpikeMO.getI();
        data = (byte[]) prcProcess1SpikeIdea.getValue();
        
        encSpikeMO = (MemoryObject) getOutput(Configuration.ENC_SPIKE_MO);
        encSpikeIdea = (Idea) encSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        //Aqui se deberian remover las features y solo mandar los id
        //Pero por ahora no existen features
        
        SpikeObject<ArrayList<CObject>> spike = SpikeObject.fromBytes(data);
        
        spike.setId(SpikeType.RECOGNIZED_OBJECTS);
        
        encSpikeIdea.setValue(spike.toBytes());
        encSpikeMO.setI(encSpikeIdea);
//        send(AreaNames.ENC,spike.toBytes() );
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
