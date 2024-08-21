/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.vlpfc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.Codelets.storage.vlpfc.RelationsWMQueue;

import episodicv2.core.spike.SpikeObject;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.ObjectRelations;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import episodicv2.core.spike.SpikeType;
import static episodicv2.core.spike.SpikeType.REQUEST_OBJECT_RELATION;
import static episodicv2.core.spike.SpikeType.TASK_SET_ALLOWED_CLASSES;

/**
 *
 * @author karenlima
 */
public class VLPFCProcess2Codelet extends Codelet {
    
    private MemoryObject vlpfcProcess2SpikeMO;
    private Idea vlpfcProcess2SpikeIdea;
    private MemoryObject dlpfcSpikeMO;
    private Idea dlpfcSpikeIdea;
    private MemoryObject prcSpikeMO;
    private Idea prcSpikeIdea;
    private byte[] data = null;
    private RelationsViewer viewer;
    private static RelationsWMQueue items = new RelationsWMQueue(7);
    

    private ArrayList<Integer> allowedClasses = new ArrayList<>();
    
    public VLPFCProcess2Codelet() {
        setIsMemoryObserver(true);
        viewer = RelationsViewer.getInstance();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                viewer.setVisible(true);
            }
        });
    }

    @Override
    public void accessMemoryObjects() {
        vlpfcProcess2SpikeMO = (MemoryObject) getInput(Configuration.VLPFC_PROCESS_2_SPIKE_MO);
        vlpfcProcess2SpikeIdea = (Idea) vlpfcProcess2SpikeMO.getI();
        data = (byte[]) vlpfcProcess2SpikeIdea.get(Configuration.SPIKE_VLPFC_PROCESS_2_DATA_IDEA).getValue();
        
        dlpfcSpikeMO = (MemoryObject) getOutput(Configuration.DLPFC_SPIKE_MO);
        dlpfcSpikeIdea = (Idea) dlpfcSpikeMO.getI();
        
        prcSpikeMO = (MemoryObject) getOutput(Configuration.PRC_SPIKE_MO);
        prcSpikeIdea = (Idea) prcSpikeMO.getI();
    }
    
    @Override
    public void proc() {

        SpikeObject rawSpike = SpikeObject.fromBytes(data);

        if (rawSpike.getId() == TASK_SET_ALLOWED_CLASSES) {

            SpikeObject<ArrayList<Integer>> spike = rawSpike;
            allowedClasses = spike.getObject();

        } else if (rawSpike.getId() == REQUEST_OBJECT_RELATION) {

            SpikeObject<Integer> spike = rawSpike;

            ObjectRelations relations = items.getRelatedItems(spike.getObject());

            if (relations != null) {
                SpikeObject<ObjectRelations> spikeRelations = new SpikeObject(SpikeType.RETRIEVED_OBJECT_RELATIONS_TOP_DOWN, relations, spike.getTime());
                dlpfcSpikeIdea.setValue(spikeRelations.toBytes());
                dlpfcSpikeMO.setI(dlpfcSpikeIdea);
            } else {
                prcSpikeIdea.setValue(data);
                prcSpikeMO.setI(prcSpikeIdea);
            }

        } else {
            SpikeObject<ObjectRelations> spikeRelations = rawSpike;

            ObjectRelations relations = spikeRelations.getObject();

            if (allowedClasses.contains(relations.getObjectId()) || allowedClasses.isEmpty()) {

                items.addItem(relations, spikeRelations.getTime());

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        viewer.addRelations(items.getObjectRelations());
                    }
                });

               // SimpleLogger.log(this, "Relations of object class " + relations.getObjectId() + " dd " + items.getItems().size());
            }

        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
