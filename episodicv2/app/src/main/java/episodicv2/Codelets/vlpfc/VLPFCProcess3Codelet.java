/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.vlpfc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.spike.TopDownParameters;

/**
 *
 * @author karenlima
 */
public class VLPFCProcess3Codelet extends Codelet {
    
    private MemoryObject vlpfcProcess3SpikeMO;
    private Idea vlpfcProcess3SpikeIdea;
    private MemoryObject prcSpikeMO;
    private Idea prcSpikeIdea;
    
    //TIENEN QUE DECAER, AUN NO DECAEN
    private ObjectRelations maxAffectiveObject;
    private ObjectRelations lessAffectiveObject;
    
    private byte[] data = null;
    
    public VLPFCProcess3Codelet() {
        
    }

    @Override
    public void accessMemoryObjects() {
        vlpfcProcess3SpikeMO = (MemoryObject) getInput(Configuration.VLPFC_PROCESS_3_SPIKE_MO);
        vlpfcProcess3SpikeIdea = (Idea) vlpfcProcess3SpikeMO.getI();
        
        data = (byte[]) vlpfcProcess3SpikeIdea.getValue();
        
        prcSpikeMO = (MemoryObject) getOutput(Configuration.PRC_SPIKE_MO);
        prcSpikeIdea = (Idea) prcSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        try {

            SpikeObject rawSpike = SpikeObject.fromBytes(data);

            switch (rawSpike.getId()) {
                case SpikeType.REQUEST_OBJECT_BY_AFFECT:

                    SpikeObject<Integer> requestSpike = rawSpike;

                    if (requestSpike.getObject() == TopDownParameters.MAX_AFFECT) {

                        if (maxAffectiveObject != null) {
                            System.out.println("[Exists] " + maxAffectiveObject.toString());
                        } else {
                            prcSpikeIdea.setValue(data);
                            prcSpikeMO.setI(prcSpikeIdea);
                        }

                    } else if (requestSpike.getObject() == TopDownParameters.MIN_AFFECT) {
                        if (lessAffectiveObject != null) {
                            System.out.println("[Exists] " + lessAffectiveObject.toString());
                        } else {
                            prcSpikeIdea.setValue(data);
                            prcSpikeMO.setI(prcSpikeIdea);
                        }
                    }

                    break;

                case SpikeType.RETRIEVED_AFFECTIVE_OBJECT_TOP_DOWN:
                    break;

                case SpikeType.RETRIEVED_MOST_AFFECTIVE_OBJECT_TOP_DOWN:

                    SpikeObject<ObjectRelations> mostASpike = rawSpike;

                    maxAffectiveObject = mostASpike.getObject();

                    System.out.println(maxAffectiveObject.toString());

                    break;

                case SpikeType.RETRIEVED_LESS_AFFECTIVE_OBJECT_TOP_DOWN:

                    SpikeObject<ObjectRelations> lessASpike = rawSpike;

                    lessAffectiveObject = lessASpike.getObject();

                    System.out.println(lessAffectiveObject.toString());

                    break;
            }

        } catch (Exception e) {
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
