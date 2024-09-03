/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.itc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.Codelets.storage.itc.ITCStorageHandler;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.utils.ImageUtils;
import java.awt.image.BufferedImage;

/**
 *
 * @author karenlima
 */
public class ITCProcess1Codelet extends Codelet {
    
    private MemoryObject itcProcess1SpikeMO;
    private Idea itcProcess1SpikeIdea;
    
    private MemoryObject vlpfcSpikeMO;
    private Idea vlpfcSpikeIdea;
    
    private byte[] data = null;
    private static ITCStorageHandler itcStorageHandler = ITCStorageHandler.getInstance();

    public ITCProcess1Codelet() {
        setIsMemoryObserver(true);
    }
    
    @Override
    public void accessMemoryObjects() {
        setIsMemoryObserver(true);
        
        itcProcess1SpikeMO = (MemoryObject) getInput(Configuration.ITC_PROCESS_1_SPIKE_MO);
        itcProcess1SpikeIdea = (Idea) itcProcess1SpikeMO.getI();
        
        data = (byte[]) itcProcess1SpikeIdea.getValue();
       
        vlpfcSpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_SPIKE_MO);
        vlpfcSpikeIdea = (Idea) vlpfcSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        SpikeObject<Integer> spike = SpikeObject.fromBytes(data);

        Integer classId = spike.getObject();

        BufferedImage searchedFeatures = itcStorageHandler.getFeatures(classId);
        
        String base64String = ImageUtils.toBase64(searchedFeatures);

        SpikeObject<CObject> itemSpike = new SpikeObject(SpikeType.RETRIEVED_OBJECT_TOP_DOWN, new CObject(classId, classId, 0, 0, "", base64String, 0), 0);
        
        //To Working Memory
//        send(AreaNames.VLPFC, itemSpike.toBytes());
        vlpfcSpikeIdea.setValue(itemSpike.toBytes());
        vlpfcSpikeMO.setI(vlpfcSpikeIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
