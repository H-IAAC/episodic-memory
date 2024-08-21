/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.vlpfc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.Codelets.storage.vlpfc.ItemWMQueue;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.gui.ImageList;
import java.util.ArrayList;
import episodicv2.core.spike.SpikeType;
import episodicv2.Codelets.storage.vlpfc.STMChangesListener;

/**
 *
 * @author karenlima
 */
public class VLPFCProcess1Codelet extends Codelet implements STMChangesListener {
    
    private MemoryObject vlpfcProcess1SpikeMO;
    private Idea vlpfcProcess1SpikeIdea;
    private MemoryObject vlpfcProcess2SpikeMO;
    private Idea vlpfcProcess2SpikeIdea;
    private MemoryObject dlpfcSpikeMO;
    private Idea dlpfcSpikeIdea;
    private MemoryObject itcSpikeMO;
    private Idea itcSpikeIdea;
    
    private ImageList itemsViewer;
    private ItemWMQueue items;
    private ArrayList<Integer> allowedClasses = new ArrayList<>();
    private byte[] data = null;
    
    public VLPFCProcess1Codelet() {
        setIsMemoryObserver(true);
        
        itemsViewer = ImageList.getInstance();
        
        items = ItemWMQueue.getInstance(7);
        items.setListener(this);
        
        

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                itemsViewer.setVisible(true);
            }
        });
    }
    
    @Override
    public void accessMemoryObjects() {
        vlpfcProcess1SpikeMO = (MemoryObject) getInput(Configuration.VLPFC_PROCESS_2_SPIKE_MO);
        vlpfcProcess1SpikeIdea = (Idea) vlpfcProcess2SpikeMO.getI();
        
        data = (byte[]) vlpfcProcess1SpikeIdea.get(Configuration.SPIKE_VLPFC_PROCESS_1_DATA_IDEA).getValue();
        
        dlpfcSpikeMO = (MemoryObject) getOutput(Configuration.DLPFC_SPIKE_MO);
        dlpfcSpikeIdea = (Idea) dlpfcSpikeMO.getI();
        
        itcSpikeMO = (MemoryObject) getOutput(Configuration.ITC_SPIKE_MO);
        itcSpikeIdea = (Idea) itcSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        SpikeObject rawSpike = SpikeObject.fromBytes(data);

        if (rawSpike.getId() == SpikeType.TASK_SET_ALLOWED_CLASSES) {

            SpikeObject<ArrayList<Integer>> spike = rawSpike;
            allowedClasses = spike.getObject();

        } else if (rawSpike.getId() == SpikeType.REQUEST_OBJECT) {

            //Search class in working memory first
            SpikeObject<Integer> spike = rawSpike;

            CObject searchedItem = items.getItem(spike.getObject());

            if (searchedItem != null) {

                SpikeObject<CObject> featuresSpike = new SpikeObject(SpikeType.RETRIEVED_OBJECT_TOP_DOWN, searchedItem, 0);

//                send(AreaNames.DLPFC, featuresSpike.toBytes());
                dlpfcSpikeIdea.setValue(featuresSpike.toBytes());
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);

                /*
                SimpleLogger.log(this, "No existe item en working memory, busca en ITC");
                send(AreaNames.ITC, data);*/
            } else {
                //SimpleLogger.log(this, "No existe item en working memory, busca en ITC");
//                send(AreaNames.ITC, data);
                itcSpikeIdea.setValue(data);
                itcSpikeMO.setI(itcSpikeIdea);
            }

        } else {

            //STORAGE OF ITEMS IN WORKING MEMORY
            SpikeObject<CObject> spike = rawSpike;

            //FILTERING OF CLASS ITEMS ACCORDING TO THE TASK SET
            if (allowedClasses.contains(spike.getObject().getClassId()) || allowedClasses.isEmpty()) {

                items.addItem(spike.getObject(), spike.getTime());

                this.itemsViewer.setItems(items.getItems());

            } else {
                //THE OBJECT WAS REJECTED
            }

        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    @Override
    public void itemDeleted() {
        this.itemsViewer.setItems(items.getItems());
    }
}
