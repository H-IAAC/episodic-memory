/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.phc;

import episodicv2.FutureCodelets.*;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.CObjectList;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.utils.Utils2DString;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class PHCCodelet extends Codelet {
    
    private MemoryObject phcSpikeMO;
    private Idea phcSpikeIdea;
    
    private MemoryObject phcProcess1SpikeMO;
    private Idea phcProcess1SpikeIdea;
    
    private byte[] data = null;
    
    public PHCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        phcSpikeMO = (MemoryObject) getInput(Configuration.PHC_SPIKE_MO);
        phcSpikeIdea = (Idea) phcSpikeMO.getI();
        data = (byte[]) phcSpikeIdea.get(Configuration.SPIKE_PHC_DATA_IDEA).getValue();
        
        phcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.PHC_PROCESS_1_SPIKE_MO);
        phcProcess1SpikeIdea = (Idea) phcProcess1SpikeMO.getI();
    }
    
    @Override
    public void proc() {
        
        CObjectList encapsulatedObjects = Utils2DString.getObjectsList(new String(data));

        if (encapsulatedObjects != null) {

            SpikeObject<ArrayList<CObject>> spike = new SpikeObject(SpikeType.CENTER_POINTS, encapsulatedObjects.getObjects(), encapsulatedObjects.getTime());

            byte[] spikeBytes = spike.toBytes();
            
            phcProcess1SpikeIdea.setValue(spikeBytes);
            phcProcess1SpikeMO.setI(phcProcess1SpikeIdea);

//            send(AreaNames.PHCProcess1, spikeBytes);

        }
        
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
