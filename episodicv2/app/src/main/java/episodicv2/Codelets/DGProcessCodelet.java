/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.configuration.Configuration;
import static episodicv2.configuration.Configuration.*;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;
import java.util.HashMap;
import spike.Synchronizer;
import t2dstring.OccupancyGrid;
import t2dstring.T2DString;

/**
 *
 * @author karenlima
 */
public class DGProcessCodelet extends Codelet {
    
    MemoryObject recognizedObjectsSpikeMO;
    Idea recognizedObjectsSpikeIdea;
    
    MemoryObject unintegratedScenePatternMO;
    Idea unintegratedScenePatternIdea;
    
    MemoryObject patternReplacedMO;
    Idea patternReplacedIdea;
    
    private EmotionalDecay emotionalDecay = null;
    private double affectIntensity = 0.0;
    
    private Synchronizer synchronizer = new Synchronizer(2);
    private static final int OBJECTS_LIST_KEY = 10;
    private static final int SCENE_PATTERN_KEY = 20;
    private ArrayList<Idea> dgObjects;
    private String dgPattern;
    
    public DGProcessCodelet() {
        
        setIsMemoryObserver(true);
        
    }
    @Override
    public void accessMemoryObjects() {
        recognizedObjectsSpikeMO = (MemoryObject) getInput(RECOGNIZED_OBJECTS_SPIKE_MO);
        recognizedObjectsSpikeIdea = (Idea) recognizedObjectsSpikeMO.getI();
        unintegratedScenePatternMO = (MemoryObject) getInput(UNINTEGRATED_SCENE_PATTERN_MO);
        unintegratedScenePatternIdea = (Idea) unintegratedScenePatternMO.getI();
        
        patternReplacedMO = (MemoryObject) getOutput(PATTERN_REPLACED_MO);
        patternReplacedIdea = (Idea) patternReplacedMO.getI();
        
    }
    
    @Override
    public void proc() {
        //TODO: identify what MO changed
        Integer currentFrameObjects = (Integer) recognizedObjectsSpikeIdea.get(CURRENT_FRAME_IDEA).getValue();
        Integer currentFramePattern = (Integer) unintegratedScenePatternIdea.get(CURRENT_FRAME_IDEA).getValue();
        String pattern = (String) unintegratedScenePatternIdea.get(PATTERN_IDEA).getValue();
        ArrayList<Idea> objects = (ArrayList<Idea>) recognizedObjectsSpikeIdea.get(OBJECTS_IDEA).getValue();
            
        synchronizer.addElement(OBJECTS_LIST_KEY, objects, currentFrameObjects);
        synchronizer.addElement(SCENE_PATTERN_KEY, pattern, currentFramePattern);
        
        if (synchronizer.isFull()) {
            initComponents();

            this.dgObjects = (ArrayList<Idea>) synchronizer.getElement(OBJECTS_LIST_KEY);
            this.dgPattern = (String) synchronizer.getElement(SCENE_PATTERN_KEY);

            //Decode the pattern
            OccupancyGrid occupancyGrid = T2DString.decodeMatrixAndReplace(createHashMap(this.dgObjects),
                    this.dgPattern,
                    Configuration.GRID_COLUMNS_X,
                    Configuration.GRID_ROWS_Y);

            //Creates the 2D String pattern
            String patternReplaced = T2DString.create2DStringPattern(occupancyGrid,
                    Configuration.IMAGE_WIDTH,
                    Configuration.IMAGE_HEIGHT,
                    Configuration.GRID_COLUMNS_X,
                    Configuration.GRID_ROWS_Y);
            
            affectIntensity = emotionalDecay.getActivation();
            
            Idea patternReplacedAtributeIdea = new Idea(PATTERN_IDEA, patternReplaced, "Property", 1);
            Idea sincTimeIdea = new Idea(TIME_IDEA, synchronizer.getTime(),"Property", 1);
            Idea positiveActivationIdea = new Idea(POSITIVE_AFFECT_IDEA, emotionalDecay.getPositiveActivation(), "Property", 1);
            Idea negativeActivationIdea = new Idea(NEGATIVE_AFFECT_IDEA, emotionalDecay.getNegativeActivation(), "Property", 1);
            Idea affectIntensityIdea = new Idea(AFFECT_INTENSITY_IDEA, affectIntensity, "Property", 1);
            patternReplacedIdea.add(patternReplacedAtributeIdea);
            patternReplacedIdea.add(sincTimeIdea);
            patternReplacedIdea.add(positiveActivationIdea);
            patternReplacedIdea.add(negativeActivationIdea);
            patternReplacedIdea.add(affectIntensityIdea);


            patternReplacedMO.setI(patternReplacedIdea);

        }
    }
    
    @Override
    public void calculateActivation(){
        
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
    
    /**
     * OPERATIONS
     */
    private HashMap<Integer, Integer> createHashMap(ArrayList<Idea> objects) {

        HashMap<Integer, Integer> objectsMap = new HashMap<>();

        for (Idea object : objects) {
            Integer preId = (Integer) object.get("pid").getValue();
            Integer classId = (Integer) object.get("id").getValue();
            objectsMap.put(preId, classId);
        }

        return objectsMap;
    }
    
}
