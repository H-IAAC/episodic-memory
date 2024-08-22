/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.ca3;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.configuration.Configuration;

/**
 *
 * @author karenlima
 */
public class CA3Codelet extends Codelet {
    
    private MemoryObject ca3SpikeMO;
    private Idea ca3SpikeIdea;
    
    private MemoryObject ca3Process1SpikeMO;
    private Idea ca3Process1SpikeIdea;
    
    private MemoryObject ca3Process2SpikeMO;
    private Idea ca3Process2SpikeIdea;
    
    private MemoryObject ca3Process3SpikeMO;
    private Idea ca3Process3SpikeIdea;
    
    private MemoryObject ca1SpikeMO;
    private Idea ca1SpikeIdea;
    
    private byte[] data = null;
    
    public CA3Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        ca3SpikeMO = (MemoryObject) getInput(Configuration.CA3_SPIKE_MO);
        ca3SpikeIdea = (Idea) ca3SpikeMO.getI();
        data = (byte[]) ca3SpikeIdea.get(Configuration.SPIKE_CA3_DATA_IDEA).getValue();
        
        ca3Process1SpikeMO = (MemoryObject) getOutput(Configuration.CA3_PROCESS_1_SPIKE_MO);
        ca3Process1SpikeIdea = (Idea) ca3Process1SpikeMO.getI();
        
        ca3Process2SpikeMO = (MemoryObject) getOutput(Configuration.CA3_PROCESS_2_SPIKE_MO);
        ca3Process2SpikeIdea = (Idea) ca3Process2SpikeMO.getI();
        
        ca3Process3SpikeMO = (MemoryObject) getOutput(Configuration.CA3_PROCESS_3_SPIKE_MO);
        ca3Process3SpikeIdea = (Idea) ca3Process3SpikeMO.getI();
        
        ca1SpikeMO = (MemoryObject) getOutput(Configuration.CA1_SPIKE_MO);
        ca1SpikeIdea = (Idea) ca1SpikeMO.getI();
    }
    
    @Override
    public void proc() {
        

        SpikeObject spike = SpikeObject.fromBytes(data);

        switch (spike.getId()) {

            case SpikeType.RECOGNIZED_OBJECTS:
            case SpikeType.UNINTEGRATED_SCENE_PATTERN:
                ca3Process2SpikeIdea.setValue(data);
                ca3Process2SpikeMO.setI(ca3Process2SpikeIdea);
//                send(AreaNames.CA3Process2, data);
                break;
            case SpikeType.NEW_ENCODED_SCENE:
                //send(AreaNames.CA1, data);
                ca3Process1SpikeIdea.setValue(data);
                ca3Process1SpikeMO.setI(ca3Process1SpikeIdea);
//                send(AreaNames.CA3Process1, data);
                break;
            case SpikeType.REQUEST_SIMILAR_SCENES:
            case SpikeType.REQUEST_SCENE_BY_ID:
            case SpikeType.REQUEST_SCENE_BY_AFFECT:

                //MTM
                //BUSCAR EN MID-TERM MEMORY Y LONG-TERM MEMORY DE FORMA SECUENCIAL
//                send(AreaNames.CA3Process3, data);
                ca3Process3SpikeIdea.setValue(data);
                ca3Process3SpikeMO.setI(ca3Process3SpikeIdea);

                //BUSCAR EN MEMORIA PERSISTENTE DE FORMA PARALELA EN OTRO NODO
                //send(AreaNames.CA3Process4, data);
                break;
            case SpikeType.AFFECTIVE_VALUE:
                ca3Process1SpikeIdea.setValue(data);
                ca3Process1SpikeMO.setI(ca3Process1SpikeIdea);
                ca3Process2SpikeIdea.setValue(data);
                ca3Process2SpikeMO.setI(ca3Process2SpikeIdea);
                ca1SpikeIdea.setValue(data);
                ca1SpikeMO.setI(ca1SpikeIdea);
//                send(AreaNames.CA1, data);
//                send(AreaNames.CA3Process1, data);
//                send(AreaNames.CA3Process2, data);
                break;
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
