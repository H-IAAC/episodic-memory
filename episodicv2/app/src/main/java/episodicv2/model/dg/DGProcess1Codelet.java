/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.dg;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.Scene;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.spike.Synchronizer;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.storage.dg.DGStorageHandler;
import episodicv2.core.t2string.OccupancyGrid;
import episodicv2.core.t2string.T2DString;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author karenlima
 */
public class DGProcess1Codelet extends Codelet {
    
    private MemoryObject dgProcess1SpikeMO;
    private Idea dgProcess1SpikeIdea;   
    
    private MemoryObject ca3SpikeMO;
    private Idea ca3SpikeIdea;
    
    private byte[] data = null;
    
    private static final int OBJECTS_LIST_KEY = 10;
    private static final int SCENE_PATTERN_KEY = 20;
    private Synchronizer synchronizer = new Synchronizer(2);
    private DGStorageHandler dgStorageHandler = DGStorageHandler.getInstance(Configuration.LOAD_MEMORY);
    private EmotionalDecay emotionalDecay = null;

    private ArrayList<CObject> dgObjects;
    private String dgPattern;
    private double affectIntensity = 0.0;
    private double affectiveValues[] = new double[]{0, 0};
    
    public DGProcess1Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        dgProcess1SpikeMO = (MemoryObject) getInput(Configuration.DG_PROCESS_1_SPIKE_MO);
        dgProcess1SpikeIdea = (Idea) dgProcess1SpikeMO.getI();
        data = (byte[]) dgProcess1SpikeIdea.get(Configuration.SPIKE_DG_PROCESS_1_DATA_IDEA).getValue();
        
        ca3SpikeMO = (MemoryObject) getInput(Configuration.CA3_SPIKE_MO);
        ca3SpikeIdea = (Idea) ca3SpikeMO.getI();
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
        

        initComponents();

        SpikeObject spike = SpikeObject.fromBytes(data);

        if (spike.getId() == SpikeType.RECOGNIZED_OBJECTS) {

            ArrayList<CObject> objects = (ArrayList<CObject>) spike.getObject();

            synchronizer.addElement(OBJECTS_LIST_KEY, objects, spike.getTime());

        } else if (spike.getId() == SpikeType.UNINTEGRATED_SCENE_PATTERN) {

            String pattern = (String) spike.getObject();

            synchronizer.addElement(SCENE_PATTERN_KEY, pattern, spike.getTime());

        } else if (spike.getId() == SpikeType.AFFECTIVE_VALUE) {

            SpikeObject<double[]> affectSpike = spike;

            affectiveValues = affectSpike.getObject();

            affectIntensity = ActivationFunctions.affect(affectiveValues[0], affectiveValues[1]);

            emotionalDecay.setNewActivation(affectiveValues[0], affectiveValues[1]);

        }

        if (synchronizer.isFull()) {

            this.dgObjects = (ArrayList<CObject>) synchronizer.getElement(OBJECTS_LIST_KEY);
            this.dgPattern = (String) synchronizer.getElement(SCENE_PATTERN_KEY);

            //Decode the pattern
            OccupancyGrid occupancyGrid = T2DString.decodeMatrixAndReplace(createHashMap(this.dgObjects),
                    this.dgPattern,
                    Configuration.GRID_COLUMNS_X,
                    Configuration.GRID_ROWS_Y);

            //Creates the 2D String pattern
            String pattern = T2DString.create2DStringPattern(occupancyGrid,
                    Configuration.IMAGE_WIDTH,
                    Configuration.IMAGE_HEIGHT,
                    Configuration.GRID_COLUMNS_X,
                    Configuration.GRID_ROWS_Y);

            /**
             * INTENTA CREAR UNA NUEVA ESCENA SINO EXISTE UN PATRON SIMILAR SI
             * EL PATRON EXISTE LO RETORNA PARA ASOCIACION
             */
            affectIntensity = emotionalDecay.getActivation();

            Scene scene = dgStorageHandler.createNewScene(pattern, emotionalDecay.getPositiveActivation(), emotionalDecay.getNegativeActivation(), affectIntensity, synchronizer.getTime());

            SpikeObject<Scene> sceneSpike = new SpikeObject(SpikeType.NEW_ENCODED_SCENE, scene, scene.getTime());

//            send(AreaNames.CA3, sceneSpike.toBytes());
            ca3SpikeIdea.setValue(sceneSpike.toBytes());
            ca3SpikeMO.setI(ca3SpikeIdea);

        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    /**
     * OPERATIONS
     */
    private HashMap<Integer, Integer> createHashMap(ArrayList<CObject> objects) {

        HashMap<Integer, Integer> objectsMap = new HashMap<>();

        for (CObject object : objects) {
            objectsMap.put(object.getPreId(), object.getClassId());
        }

        return objectsMap;
    }
    
}
