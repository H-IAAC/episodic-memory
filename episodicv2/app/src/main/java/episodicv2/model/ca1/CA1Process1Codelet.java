/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.ca1;

import episodicv2.emotions.AssociationQueue;
import episodicv2.utils.SimpleLogger;
import java.util.ArrayList;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.entities.Scene;
import episodicv2.emotions.EmotionalDecay;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.storage.ca1.CA1StorageHandler;

/**
 *
 * @author karenlima
 */
public class CA1Process1Codelet extends Codelet {

    private MemoryObject ca1Process1SpikeMO;
    private Idea ca1Process1SpikeIdea;
    
    private byte[] data = null;
    
    private AssociationQueue queue = AssociationQueue.getInstance();
    private CA1StorageHandler graph = CA1StorageHandler.getInstance(Configuration.LOAD_MEMORY);

    private EmotionalDecay emotionalDecay = null;
    private double affectIntensity = 0.0;
    private double affectiveValues[] = new double[]{0, 0};
    
    public CA1Process1Codelet (){
        setIsMemoryObserver(true);
    }
    
    @Override
    public void accessMemoryObjects() {
        ca1Process1SpikeMO = (MemoryObject) getInput(Configuration.CA1_PROCESS_1_SPIKE_MO);
        ca1Process1SpikeIdea = (Idea) ca1Process1SpikeMO.getI();
        data = (byte[]) ca1Process1SpikeIdea.getValue();
        
    }
    
    /**
     * ESTA SOLUCION ES PORQUE EL MIDDLEWARE LLAMA DOS VECES AL CONSTRUCTOR DE
     * LA MISMA CLASE (CREANDO DOS INTANCIAS), PERO SOLO LLAMA UNA VEZ AL METODO
     * INIT (DE LA PRIMERA INSTANCIA) PERO EN EJECUCION USA LA SEGUNDA
     * (IGNORANDO LO QUE SE HAYA INICIALIZADO EN LA SEGUNDA) SI SE INICIALIZA
     * ALGO EN EL CONSTRUCTOR (COMO UN TIMER O UNA VENTANA) CREA DOS INSTANCIAS
     */
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

            SpikeObject rawSpike = SpikeObject.fromBytes(data);
            Scene scene = null;

            switch (rawSpike.getId()) {
                case SpikeType.NEW_ENCODED_SCENE:
                    scene = (Scene) rawSpike.getObject();

                    //SimpleLogger.log(this,"Encoded "+scene.toString());
                    break;
                case SpikeType.SIMILAR_SCENES:
                    ArrayList<Scene> similarScenes = (ArrayList<Scene>) rawSpike.getObject();
                    scene = similarScenes.get(0);
                    break;
                case SpikeType.AFFECTIVE_VALUE:
                    SpikeObject<double[]> affectSpike = rawSpike;
                    
                    affectiveValues = affectSpike.getObject();

                    affectIntensity = ActivationFunctions.affect(affectiveValues[0], affectiveValues[1]);

                    emotionalDecay.setNewActivation(affectiveValues[0], affectiveValues[1]);
                    
                    break;
                default:
                    break;
            }

            if (scene != null && scene.getId() != 0) {

                int tuple[] = queue.addSceneId(scene.getId());

                if (tuple != null) {

                    affectIntensity = emotionalDecay.getActivation();

                    SceneRelation sr = new SceneRelation(tuple[0], tuple[1], rawSpike.getTime());

                    graph.addVertex(sr, affectIntensity);

                    //graph.printGraph();
                } else {
                    SimpleLogger.log(this, "Empty queue");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
