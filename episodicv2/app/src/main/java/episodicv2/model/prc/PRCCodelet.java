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
public class PRCCodelet extends Codelet {
    
    private byte[] data = null;
    private MemoryObject prcSpikeMO;
    private Idea prcSpikeIdea;
    private MemoryObject prcProcess1SpikeMO;
    private Idea prcProcess1SpikeIdea;
    private MemoryObject prcProcess2SpikeMO;
    private Idea prcProcess2SpikeIdea;
    private MemoryObject prcProcess3SpikeMO;
    private Idea prcProcess3SpikeIdea;
    
    public PRCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        prcSpikeMO = (MemoryObject) getInput(Configuration.PRC_SPIKE_MO);
        prcSpikeIdea = (Idea) prcSpikeMO.getI();
        data = (byte[]) prcSpikeIdea.getValue();
        
        prcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.PRC_PROCESS_1_SPIKE_MO);
        prcProcess1SpikeIdea = (Idea) prcProcess1SpikeMO.getI();
        
        prcProcess2SpikeMO = (MemoryObject) getOutput(Configuration.PRC_PROCESS_2_SPIKE_MO);
        prcProcess2SpikeIdea = (Idea) prcProcess2SpikeMO.getI();
        
        prcProcess3SpikeMO = (MemoryObject) getOutput(Configuration.PRC_PROCESS_3_SPIKE_MO);
        prcProcess3SpikeIdea = (Idea) prcProcess3SpikeMO.getI();
    }
    
    @Override
    public void proc() {
        

        SpikeObject rawSpike = SpikeObject.fromBytes(data);

        if (rawSpike.getId() == SpikeType.REQUEST_OBJECT_RELATION) {

            //Search in MTM
            prcProcess3SpikeIdea.setValue(data);
            prcProcess3SpikeMO.setI(prcProcess3SpikeIdea);
//            send(AreaNames.PRCProcess3, data);

            //Search in LTM
            //send(AreaNames.PRCProcess4, data);
        } else if (rawSpike.getId() == SpikeType.REQUEST_OBJECT_BY_AFFECT) {
            prcProcess3SpikeIdea.setValue(data);
            prcProcess3SpikeMO.setI(prcProcess3SpikeIdea);
//            send(AreaNames.PRCProcess3, data);

        } else if (rawSpike.getId() == SpikeType.AFFECTIVE_VALUE) {

            //SimpleLogger.log(this, "Affective value received in PRC: ");
            prcProcess2SpikeIdea.setValue(data);
            prcProcess2SpikeMO.setI(prcProcess2SpikeIdea);
            prcProcess3SpikeIdea.setValue(data);
            prcProcess3SpikeMO.setI(prcProcess3SpikeIdea);
//            send(AreaNames.PRCProcess2, data);
//            send(AreaNames.PRCProcess3, data);

        } else {

            SpikeObject<ArrayList<CObject>> spike = SpikeObject.fromBytes(data);
            ArrayList<CObject> objects = spike.getObject(); //Utils2DString.getRecognizedObjects(new String(data));

            if (objects != null) {

                byte[] spikeBytes = spike.toBytes();

                //To Episodic memory
                prcProcess1SpikeIdea.setValue(spikeBytes);
                prcProcess1SpikeMO.setI(prcProcess1SpikeIdea);
//                send(AreaNames.PRCProcess1, spikeBytes);

                //To association
                prcProcess2SpikeIdea.setValue(spikeBytes);
                prcProcess2SpikeMO.setI(prcProcess2SpikeIdea);
                
//                send(AreaNames.PRCProcess2, spikeBytes);

                //To retrieve
                prcProcess3SpikeIdea.setValue(spikeBytes);
                prcProcess3SpikeMO.setI(prcProcess3SpikeIdea);
//                send(AreaNames.PRCProcess3, spikeBytes);
            }
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
