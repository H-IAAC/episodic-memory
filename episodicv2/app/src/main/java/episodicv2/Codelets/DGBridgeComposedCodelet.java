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
public class DGBridgeComposedCodelet extends Codelet {
    
    MemoryObject recognizedObjectsSpikeAndunintegratedScenePatternMO;
    Idea recognizedObjectsSpikeAndunintegratedScenePatternIdea;
    
    Idea recognizedObjectsSpikeIdea;
    
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
    
    public DGBridgeComposedCodelet() {
        
        setIsMemoryObserver(true);
        
    }
    @Override
    public void accessMemoryObjects() {
        recognizedObjectsSpikeAndunintegratedScenePatternMO = (MemoryObject) getInput(RECOGNIZED_OBJECTS_SPIKE_AND_UNINTEGRATED_SCENE_PATTERN_MO);
        recognizedObjectsSpikeAndunintegratedScenePatternIdea = (Idea) recognizedObjectsSpikeAndunintegratedScenePatternMO.getI();
        
        recognizedObjectsSpikeIdea = recognizedObjectsSpikeAndunintegratedScenePatternIdea.get(RECOGNIZED_OBJECTS_SPIKE_IDEA);
        unintegratedScenePatternIdea = recognizedObjectsSpikeAndunintegratedScenePatternIdea.get(UNINTEGRATED_SCENE_PATTERN_IDEA);
        
        patternReplacedMO = (MemoryObject) getOutput(PATTERN_REPLACED_MO);
        patternReplacedIdea = (Idea) patternReplacedMO.getI();
    }
    
    @Override
    public void proc() {
        //TODO: identify what MO changed
        //verify if has data in the MO AND has no data with that key in the syncronizer
        if (unintegratedScenePatternIdea.get(CURRENT_FRAME_IDEA) != null) {
            Integer currentFramePattern = (Integer) unintegratedScenePatternIdea.get(CURRENT_FRAME_IDEA).getValue();
            String pattern = (String) unintegratedScenePatternIdea.get(PATTERN_IDEA).getValue();
            
            Object element = synchronizer.getElement(SCENE_PATTERN_KEY);
            if( element == null || synchronizer.getTime() <= currentFramePattern ) {
                System.out.println("Adicionou no sync SCENE_PATTERN_KEY, unintegratedScenePatternIdea" + pattern + "time" + synchronizer.getTime() + "Current frame pattern" + currentFramePattern);
                synchronizer.addElement(SCENE_PATTERN_KEY, pattern, currentFramePattern);
            } 
        }
        if (recognizedObjectsSpikeIdea.get(CURRENT_FRAME_IDEA) != null) {
            Integer currentFrameObjects = (Integer) recognizedObjectsSpikeIdea.get(CURRENT_FRAME_IDEA).getValue();

            ArrayList<Idea> objects = (ArrayList<Idea>) recognizedObjectsSpikeIdea.get(OBJECTS_IDEA).getValue();
            System.out.println("recognizedObjectsSpikeIdea" + objects);
            Object element = synchronizer.getElement(OBJECTS_LIST_KEY);
            if( element == null || synchronizer.getTime() <= currentFrameObjects ) {
                synchronizer.addElement(OBJECTS_LIST_KEY, objects, currentFrameObjects);
                System.out.println("adicionou no sync OBJECTS_LIST_KEY, recognizedObjectsSpikeIdea" + objects + "current frame objects" + currentFrameObjects);
            } 
        }
        
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
            
            System.out.println("Pattern replaced in DGBridgeComposed:" + patternReplaced);
            
            Idea patternReplacedAtributeIdea = new Idea(PATTERN_IDEA, patternReplaced, "Property", 1);
            Idea sincTimeIdea = new Idea(TIME_IDEA, synchronizer.getTime(),"Property", 1);
            Idea positiveActivationIdea = new Idea(POSITIVE_AFFECT_IDEA, emotionalDecay.getPositiveActivation(), "Property", 1);
            Idea negativeActivationIdea = new Idea(NEGATIVE_AFFECT_IDEA, emotionalDecay.getNegativeActivation(), "Property", 1);
            Idea affectIntensityIdea = new Idea(AFFECT_INTENSITY_IDEA, affectIntensity, "Property", 1);
            patternReplacedIdea.setL(new ArrayList());
            patternReplacedIdea.add(patternReplacedAtributeIdea);
            patternReplacedIdea.add(sincTimeIdea);
            patternReplacedIdea.add(positiveActivationIdea);
            patternReplacedIdea.add(negativeActivationIdea);
            patternReplacedIdea.add(affectIntensityIdea);
            patternReplacedIdea.add(new Idea(CURRENT_FRAME_IDEA, synchronizer.getTime(), "Property", 1));
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
            Integer preId = (Integer) object.get(PID_IDEA).getValue();
            Integer classId = (Integer) object.get(ID_IDEA).getValue();
            objectsMap.put(preId, classId);
        }
        return objectsMap;
    }
    
}
