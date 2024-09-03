/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.ca1;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;

/**
 *
 * @author karenlima
 */
public class CA1Codelet extends Codelet {
    
    private MemoryObject ca1SpikeMO;
    private Idea ca1SpikeIdea;
    
    private MemoryObject ca1Process1SpikeMO;
    private Idea ca1Process1SpikeIdea;

    private MemoryObject ca1Process2SpikeMO;
    private Idea ca1Process2SpikeIdea;

    private MemoryObject ca1Process3SpikeMO;
    private Idea ca1Process3SpikeIdea;

    private MemoryObject sbSpikeMO;
    private Idea sbSpikeIdea;
    
    private byte[] data = null;

    public CA1Codelet() {
        setIsMemoryObserver(true);
    }
    
    @Override
    public void accessMemoryObjects() {
        ca1SpikeMO = (MemoryObject) getInput(Configuration.CA1_SPIKE_MO);
        ca1SpikeIdea = (Idea) ca1SpikeMO.getI();
        data = (byte[]) ca1SpikeIdea.getValue();
        
        
        ca1Process1SpikeMO = (MemoryObject) getOutput(Configuration.CA1_PROCESS_1_SPIKE_MO);
        ca1Process1SpikeIdea = (Idea) ca1Process1SpikeMO.getI();

        ca1Process2SpikeMO = (MemoryObject) getOutput(Configuration.CA1_PROCESS_2_SPIKE_MO);
        ca1Process2SpikeIdea = (Idea) ca1Process2SpikeMO.getI();

        ca1Process3SpikeMO = (MemoryObject) getOutput(Configuration.CA1_PROCESS_3_SPIKE_MO);
        ca1Process3SpikeIdea = (Idea) ca1Process3SpikeMO.getI();

        sbSpikeMO = (MemoryObject) getOutput(Configuration.SB_SPIKE_MO);
        sbSpikeIdea = (Idea) sbSpikeMO.getI();

    }
    
    @Override
    public void proc() {

        SpikeObject spike = SpikeObject.fromBytes(data);

        switch (spike.getId()) {

            case SpikeType.NEW_ENCODED_SCENE:
                // Atualiza e configura memória para SB
                sbSpikeIdea.setValue(data);
                sbSpikeMO.setI(sbSpikeIdea);

                // Process1
                ca1Process1SpikeIdea.setValue(data);
                ca1Process1SpikeMO.setI(ca1Process1SpikeIdea);

                // Process2
                ca1Process2SpikeIdea.setValue(data);
                ca1Process2SpikeMO.setI(ca1Process2SpikeIdea);
                break;

            case SpikeType.SIMILAR_SCENES:
                // Atualiza e configura memória para SB
                sbSpikeIdea.setValue(data);
                sbSpikeMO.setI(sbSpikeIdea);

                // Process2
                ca1Process2SpikeIdea.setValue(data);
                ca1Process2SpikeMO.setI(ca1Process2SpikeIdea);
                break;

            case SpikeType.CURRENT_SCENE:
                // Atualiza e configura memória para SB
                sbSpikeIdea.setValue(data);
                sbSpikeMO.setI(sbSpikeIdea);
                break;

            case SpikeType.RETRIEVED_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN:
                // Atualiza e configura memória para SB
                sbSpikeIdea.setValue(data);
                sbSpikeMO.setI(sbSpikeIdea);

                // MTM
                ca1Process2SpikeIdea.setValue(data);
                ca1Process2SpikeMO.setI(ca1Process2SpikeIdea);
                break;

            case SpikeType.RETRIEVED_SCENE_TOP_DOWN_LTM:
                // Atualiza e configura memória para SB
                sbSpikeIdea.setValue(data);
                sbSpikeMO.setI(sbSpikeIdea);

                // LTM
                ca1Process3SpikeIdea.setValue(data);
                ca1Process3SpikeMO.setI(ca1Process3SpikeIdea);
                break;

            case SpikeType.AFFECTIVE_VALUE:
                // Process1
                ca1Process1SpikeIdea.setValue(data);
                ca1Process1SpikeMO.setI(ca1Process1SpikeIdea);
                break;
        }



    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
