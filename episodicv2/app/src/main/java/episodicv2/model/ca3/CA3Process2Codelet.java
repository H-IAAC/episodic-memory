/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.ca3;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import java.util.ArrayList;
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
import episodicv2.core.t2string.T2DString;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;
import java.util.HashMap;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.Scene;
import episodicv2.core.t2string.T2DString;
import episodicv2.utils.SimpleLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import episodicv2.core.t2string.OccupancyGrid;
import episodicv2.core.ConsolidationNodeHandler;
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
public class CA3Process2Codelet extends Codelet {
    
    private MemoryObject ca3Process2SpikeMO;
    private Idea ca3Process2SpikeIdea;
    
    private MemoryObject ca1SpikeMO;
    private Idea ca1SpikeIdea;
    
    private byte[] data = null;
    
    
    private static CA3StorageHandler ca3StorageHandler = CA3StorageHandler.getInstance(Configuration.LOAD_MEMORY);
    private EmotionalDecay emotionalDecay = null;

    private static final int OBJECTS_LIST_KEY = 10;
    private static final int SCENE_PATTERN_KEY = 20;
    private static final int SAVE_EACH_TIME = 5;

    private Synchronizer synchronizer = new Synchronizer(2);
    private ArrayList<CObject> dgObjects;
    private String dgPattern;
    private double affectIntensity = 0.0;
    private double affectiveValues[] = new double[]{0, 0};
    
    public CA3Process2Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        ca3Process2SpikeMO = (MemoryObject) getInput(Configuration.CA3_PROCESS_2_SPIKE_MO);
        ca3Process2SpikeIdea = (Idea) ca3Process2SpikeMO.getI();
        data = (byte[]) ca3Process2SpikeIdea.getValue();

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
            
            SpikeObject spike = SpikeObject.fromBytes(data);

            if (spike.getId() == SpikeType.RECOGNIZED_OBJECTS) {

                ArrayList<CObject> objects = (ArrayList<CObject>) spike.getObject();

                dgObjects = objects;

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

                affectIntensity = emotionalDecay.getActivation();

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

                //CREATES THE PATTERN TO SEND TO WORKING MEMORY
                
                Scene scene = new Scene(0, emotionalDecay.getPositiveActivation(), emotionalDecay.getNegativeActivation(), pattern, synchronizer.getTime());
                SpikeObject<Scene> sceneSpike = new SpikeObject(SpikeType.CURRENT_SCENE, scene, scene.getTime());

//                send(AreaNames.CA1, sceneSpike.toBytes());
                ca1SpikeIdea.setValue(sceneSpike.toBytes());
                ca1SpikeMO.setI(ca1SpikeIdea);

                //RETRIEVES THE MOST SIMILAR SCENE FOR THE LEARNING QUEUE 
                ArrayList<Scene> similarScenes = ca3StorageHandler.retrieveSimilarScenesBottomUp(pattern, emotionalDecay.getPositiveActivation(), emotionalDecay.getNegativeActivation(), affectIntensity, synchronizer.getTime(), System.currentTimeMillis());

                if (!similarScenes.isEmpty()) {

                    SpikeObject<ArrayList<Scene>> similarScenesSpike = new SpikeObject(SpikeType.SIMILAR_SCENES, similarScenes, synchronizer.getTime());
                    ca1SpikeIdea.setValue(similarScenesSpike.toBytes());
                    ca1SpikeMO.setI(ca1SpikeIdea);
//                    send(AreaNames.CA1, similarScenesSpike.toBytes());

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
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
