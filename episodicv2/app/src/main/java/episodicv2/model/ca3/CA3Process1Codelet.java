
package episodicv2.model.ca3;


import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.entities.Scene;
import episodicv2.core.storage.CA3StorageHandler;
import episodicv2.emotions.EmotionalDecay;
/**
 *
 * @author karenlima
 */
public class CA3Process1Codelet extends Codelet {
    
    private MemoryObject ca3Process1SpikeMO;
    private Idea ca3Process1SpikeIdea;
    
    private MemoryObject ca1SpikeMO;
    private Idea ca1SpikeIdea;
    
    private byte[] data = null;
    
    private static CA3StorageHandler ca3StorageHandler = CA3StorageHandler.getInstance(Configuration.LOAD_MEMORY);

    private EmotionalDecay emotionalDecay = null;
    private double affectIntensity = 0.0;
    private double affectiveValues[] = new double[]{0, 0};
    
    public CA3Process1Codelet() {
        setIsMemoryObserver(true);
    }
    
    @Override
    public void accessMemoryObjects() {
        
        ca3Process1SpikeMO = (MemoryObject) getInput(Configuration.CA3_PROCESS_1_SPIKE_MO);
        ca3Process1SpikeIdea = (Idea) ca3Process1SpikeMO.getI();
        data = (byte[]) ca3Process1SpikeIdea.get(Configuration.SPIKE_CA3_PROCESS_1_DATA_IDEA).getValue();

        ca1SpikeMO = (MemoryObject) getOutput(Configuration.CA1_SPIKE_MO);
        ca1SpikeIdea = (Idea) ca1SpikeMO.getI();
    }
    
    private void initComponents() {
        if (emotionalDecay == null) {
            emotionalDecay = new EmotionalDecay(this.getClass().getName());
            emotionalDecay.start();
        }
    }
    
    @Override
    public void proc() {
        

        try {

            initComponents();

            SpikeObject<Scene> spike = SpikeObject.fromBytes(data);

            if (spike.getId() == SpikeType.AFFECTIVE_VALUE) {

                SpikeObject<double[]> affectSpike = SpikeObject.fromBytes(data);

                affectiveValues = affectSpike.getObject();

                affectIntensity = ActivationFunctions.affect(affectiveValues[0], affectiveValues[1]);

                emotionalDecay.setNewActivation(affectiveValues[0], affectiveValues[1]);
                
            } else {

                Scene scene = spike.getObject();
                scene.setRecent(true);

                affectIntensity = emotionalDecay.getActivation();
                
                ca3StorageHandler.storeScene(scene, scene.getPositiveAffect(), scene.getNegativeAffect(), affectIntensity);
                
                ca1SpikeIdea.setValue(data);
                ca1SpikeMO.setI(ca1SpikeIdea);
//                send(AreaNames.CA1, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void calculateActivation(){
    }
    
    
}
