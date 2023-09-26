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
import episodicv2.emotions.ActivationFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import t2dstring.T2DString;

/**
 *
 * @author karenlima
 */
public class DGProcessCodelet extends Codelet {
    
    MemoryObject rootMO;
    Idea rootIdea;
    
    MemoryObject patternReplacedMO;
    Idea patternReplacedIdea;

    MemoryObject newEncodedSceneSpikeMO;
    Idea newEncodedSceneSpikeIdea;
    
    MemoryObject dgMidTermMemoryScenesMO;
    Idea dgMidTermMemoryScenesIdea;
    
    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;
    
    //CARGA LAS ESCENAS EXISTENTES
    private static int DG_SIZE = 0;
    private static ArrayList<Idea> midTermMemoryScenes;
    private static Map<Integer, Idea> midTermMemoryScenesByID;
    
    public DGProcessCodelet(Boolean load) {
        
        setIsMemoryObserver(true);
        midTermMemoryScenes = new ArrayList<>();
        midTermMemoryScenesByID = new HashMap<>();

        if (load) {
            load();
            loadDGSize();
        }
        
    }
    
    @Override
    public void accessMemoryObjects() {
        System.out.println("[DG] Executing accessMemoryObjects DGProcessCodelet");
        //  TODO: revisar o que deve ser entrada ou saída para que modifique a root mas não notifique para ativar o codelet sempre que a root alterar
        rootMO = (MemoryObject) getInput(ROOT_MO);
        rootIdea = (Idea) rootMO.getI();
        
        patternReplacedMO = (MemoryObject) getInput(PATTERN_REPLACED_MO);
        patternReplacedIdea = (Idea) patternReplacedMO.getI();
        
        newEncodedSceneSpikeMO = (MemoryObject) getOutput(NEW_ENCODED_SCENE_SPIKE_MO);
        newEncodedSceneSpikeIdea = (Idea) newEncodedSceneSpikeMO.getI();
        
        dgMidTermMemoryScenesMO = (MemoryObject) getOutput(DG_MID_TERM_MEMORY_SCENES_MO);
        dgMidTermMemoryScenesIdea = (Idea) dgMidTermMemoryScenesMO.getI();
        
    }
    
    @Override
    public void proc() {
        System.out.println("[DG] Executing proc DGProcessCodelet");
        

        String patternReplaced = (String) patternReplacedIdea.get(PATTERN_IDEA).getValue();
        Integer time = (Integer) patternReplacedIdea.get(TIME_IDEA).getValue();
        Double positiveAffect = (Double) patternReplacedIdea.get(POSITIVE_AFFECT_IDEA).getValue();
        Double negativeAffect = (Double) patternReplacedIdea.get(NEGATIVE_AFFECT_IDEA).getValue();
        Double affectIntensity = (Double) patternReplacedIdea.get(AFFECT_INTENSITY_IDEA).getValue();

        /**
         * INTENTA CREAR UNA NUEVA ESCENA SINO EXISTE UN PATRON SIMILAR SI
         * EL PATRdON EXISTE LO RETORNA PARA ASOCIACION
         */

        Idea scene = createNewScene(patternReplaced, positiveAffect, negativeAffect, affectIntensity, time);
        newEncodedSceneSpikeIdea.setL(new ArrayList());
        newEncodedSceneSpikeIdea.add(scene);
        
        newEncodedSceneSpikeIdea.add(new Idea(CURRENT_FRAME_IDEA, patternReplacedIdea.get(CURRENT_FRAME_IDEA).getValue(), "Property", 1));
        newEncodedSceneSpikeMO.setI(newEncodedSceneSpikeIdea);
        
        Idea midTermMemoryScenesIdea = new Idea(MID_TERM_MEMORY_SCENES, midTermMemoryScenes, "Property", 1);
        Idea midTermMemoryScenesIdeaById = new Idea(MID_TERM_MEMORY_SCENES_BY_ID, midTermMemoryScenesByID, "Property", 1);
        Idea dgSizeIdea = new Idea(DG_SIZE_IDEA, DG_SIZE, "Property", 1);
        
        dgMidTermMemoryScenesIdea.setL(new ArrayList());
        dgMidTermMemoryScenesIdea.add(midTermMemoryScenesIdea);
        dgMidTermMemoryScenesIdea.add(midTermMemoryScenesIdeaById);
        dgMidTermMemoryScenesIdea.add(dgSizeIdea);
        Integer dgSize = (Integer) dgMidTermMemoryScenesIdea.get(DG_SIZE_IDEA).getValue();
        System.out.println("[DG] DG size saved at dgMidTermMemoryScenesIdea(DGProcessCodelet): "+ dgSize);
        dgMidTermMemoryScenesMO.setI(dgMidTermMemoryScenesIdea);
        
        Idea testeIdea = (Idea) dgMidTermMemoryScenesMO.getI();
        System.out.println("[DG] dgMidTermMemoryScenesMO idea content");
        System.out.println(testeIdea.getL());
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
     /**
     * STORES A NEW 2D STRING PATTERN
     *
     * @param pattern
     * @param positiveAffect
     * @param negativeAffect
     * @param affectIntensity
     * @param time
     * @return
     */
    public Idea createNewScene(String pattern, double positiveAffect, double negativeAffect, double affectIntensity, int time) {

        int assignedId = 0;
        Idea scene = getSimilar(pattern, affectIntensity);

        //SI NO HAY UNA ESCENA SIMILAR CREA UNA NUEVA
        if (scene == null) {

            DG_SIZE = DG_SIZE + 1;
            scene = new Idea(SCENE_IDEA, null, "Property", 1);
            assignedId = DG_SIZE;
            Idea idIdea = new Idea(ID_IDEA,assignedId, "Property", 1);
            Idea patternIdea = new Idea(PATTERN_IDEA,pattern, "Property", 1);
            Idea timeIdea = new Idea(TIME_IDEA,time, "Property", 1);
            Idea repetitionsIdea = new Idea(REPETITIONS_IDEA,1, "Property", 1);
            Idea positiveAffectIdea = new Idea(POSITIVE_AFFECT_IDEA,0.0, "Property", 1);
            Idea negativeAffectIdea = new Idea(NEGATIVE_AFFECT_IDEA,0.0, "Property", 1);
            Idea activationIdea = new Idea(ACTIVATION_IDEA,0.5, "Property", 1);
            Idea timestampIdea = new Idea(TIMESTAMP_IDEA,System.currentTimeMillis(), "Property", 1);
            Idea relationsIdea = new Idea(RELATIONS_IDEA,null, "Property", 1);
            Idea activeSimilarityIdea = new Idea(ACTIVE_SIMILARITY_IDEA,null, "Property", 1);
            Idea recentIdea = new Idea(RECENT_IDEA,true, "Property", 1);
            scene.add(idIdea);
            scene.add(patternIdea);
            scene.add(timeIdea);
            scene.add(repetitionsIdea);
            scene.add(positiveAffectIdea);
            scene.add(negativeAffectIdea);
            scene.add(activationIdea);
            scene.add(timestampIdea);
            scene.add(relationsIdea);
            scene.add(activeSimilarityIdea);
            scene.add(recentIdea);
            
            midTermMemoryScenes.add(scene);
            midTermMemoryScenesByID.put(assignedId, scene);

        }else{
            scene = updateScenePositiveAffect(positiveAffect, scene);
            scene = updateSceneNegativeAffect(negativeAffect, scene);
        }

        //SimpleLogger.log(this, "Created scene: " + scene.toString());
        return scene;
    }
    
     /**
     * CHECKS IF EXISTS A 2D STRING SIMILAR INTO THE STORED ONES
     *
     * @param pattern
     * @param affect
     * @return
     */
    public Idea getSimilar(String pattern, Double affect) {

        Idea similar = null;
        if (midTermMemoryScenes != null) {
            for (int i = 0; i < midTermMemoryScenes.size(); i++) {
                Idea scene = midTermMemoryScenes.get(i);
                String patternToCompare = (String) scene.get(PATTERN_IDEA).getValue();

                float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

                if (similarity >= SIMILARITY_THRESHOLD) {
                    similar = scene;
                    scene = updateSceneActivationWithAffect(affect, scene);
                    scene.get(TIMESTAMP_IDEA).setValue(System.currentTimeMillis());

                    break;
                }

            }
        }
        

        //SI NO HAY UN PATRON SIMILAR EN MID-TERM LO BUSCA EN LONG-TERM
        if (similar == null) {
            Idea dgDataIdea = rootIdea.get(DG_DATA_IDEA);
            if (dgDataIdea.get(DG_MEMORY_SCENES_IDEA).getValue() != null) {
                System.out.println(DG_MEMORY_SCENES_IDEA);
                System.out.println(dgDataIdea.get(DG_MEMORY_SCENES_IDEA).getValue());
                ArrayList<String> sceneLines = (ArrayList<String>) dgDataIdea.get(DG_MEMORY_SCENES_IDEA).getValue();
                for (String line: sceneLines) {
                    Idea sceneIdea = storedSceneToIdea(line);

                    String patternToCompare = (String) sceneIdea.get(PATTERN_IDEA).getValue();

                    float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

                    if (similarity >= SIMILARITY_THRESHOLD) {

                        similar = sceneIdea;

                        sceneIdea = updateSceneActivationWithAffect(affect, sceneIdea);
                        sceneIdea.get(TIMESTAMP_IDEA).setValue(System.currentTimeMillis());

                        Integer idScene = (Integer) sceneIdea.get(ID_IDEA).getValue();
                        midTermMemoryScenes.add(sceneIdea);
                        midTermMemoryScenesByID.put(idScene, sceneIdea);

                        break;
                    }
                }
            }
        }

        return similar;
    }
    
    public Idea updateSceneActivationWithAffect(double affectIntensity, Idea scene) {

        Integer repetitions = (Integer) scene.get(REPETITIONS_IDEA).getValue();
        Double activation = (Double) scene.get(ACTIVATION_IDEA).getValue();
        Integer id = (Integer) scene.get(ID_IDEA).getValue();
        repetitions++;

        double scale = Configuration.SIGMOID_SCALE;
        double alpha = Configuration.ALPHA_MEMORY_INC_RELEVANCE;
        double beta = Configuration.BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, affectIntensity, alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);
        activation = (activation + currentActivation) / 2.0;
        
        System.out.println("Scene: "+id+" activation: "+ activation +" repetition: " + repetitions);
        
        scene.get(REPETITIONS_IDEA).setValue(repetitions);
        scene.get(ACTIVATION_IDEA).setValue(activation);
        
        return scene;
        
    }
    
    public Idea updateScenePositiveAffect(double affect, Idea scene) {
        Double positiveAffect = (double) scene.get(POSITIVE_AFFECT_IDEA).getValue();
        positiveAffect = (positiveAffect + affect) / 2;
        scene.get(POSITIVE_AFFECT_IDEA).setValue(positiveAffect);
        return scene;
    }

    public Idea updateSceneNegativeAffect(double affect, Idea scene) {
        Double negativeAffect = (double) scene.get(NEGATIVE_AFFECT_IDEA).getValue();
        negativeAffect = (negativeAffect + affect) / 2;
        scene.get(NEGATIVE_AFFECT_IDEA).setValue(negativeAffect);
        return scene;
    }
    
        // 1,<(59)<<(63)<(64)<<<(60),<(60)<(59)<(63)(64),(4)(1)(2)(3),(2)(4)(5)(8),(3)(4)(4)(2),0000000176,0.93745,0.14345,1.00000,00000000000000000002,1607465245977

    
    private Idea storedSceneToIdea(String storedString) {
        Idea storedSceneIdea = new Idea(STORED_SCENE_IDEA, null, "Property", 1);
        
        String data[] = storedString.split(",");
        
        Idea idIdea = new Idea(ID_IDEA, Integer.parseInt(data[0]), "Property", 1);
        Idea patternIdea = new Idea(PATTERN_IDEA,data[1] + "," + data[2] + "," + data[3] + "," + data[4] + "," + data[5], "Property", 1);
        Idea timeIdea = new Idea(TIME_IDEA,Integer.parseInt(data[10]), "Property", 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA,Integer.parseInt(data[6]), "Property", 1);
        Idea positiveAffectIdea = new Idea(POSITIVE_AFFECT_IDEA,Double.parseDouble(data[7]), "Property", 1);
        Idea negativeAffectIdea = new Idea(NEGATIVE_AFFECT_IDEA,Double.parseDouble(data[8]), "Property", 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, Double.parseDouble(data[9]), "Property", 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA,Long.parseLong(data[11]), "Property", 1);
        Idea relationsIdea = new Idea(RELATIONS_IDEA,null, "Property", 1);
        Idea activeSimilarityIdea = new Idea(ACTIVE_SIMILARITY_IDEA,null, "Property", 1);
        Idea recentIdea = new Idea(RECENT_IDEA,false, "Property", 1);
        
        storedSceneIdea.add(idIdea);
        storedSceneIdea.add(patternIdea);
        storedSceneIdea.add(timeIdea);
        storedSceneIdea.add(repetitionsIdea);
        storedSceneIdea.add(positiveAffectIdea);
        storedSceneIdea.add(negativeAffectIdea);
        storedSceneIdea.add(activationIdea);
        storedSceneIdea.add(timestampIdea);
        storedSceneIdea.add(relationsIdea);
        storedSceneIdea.add(activeSimilarityIdea);
        storedSceneIdea.add(recentIdea);
            
        
        return storedSceneIdea;
    }
    
    /**
     * *
     * LOAD THE PATTERNS ON ROOT MEMORY
     */
    private void load() {
        //load from rootMO
        //TODO: what if is repeated??
        Idea dgDataIdea = rootIdea.get(DG_DATA_IDEA);
        ArrayList<String> sceneLines = (ArrayList<String>) dgDataIdea.get(DG_MEMORY_SCENES_IDEA).getValue();
        for (String line: sceneLines) {
            midTermMemoryScenes.add(storedSceneToIdea(line));
        }
    }
    
     /**
     * *
     * CARGA EL ARCHIVO DG_SIZE PARA SABER EL INDICE DE LA ULTIMA ESCENA
     * GUARDADA
     */
    private void loadDGSize() {
        //load size of dg
        Idea dgDataIdea = rootIdea.get(DG_DATA_IDEA);
        Integer dgSize = (Integer) dgDataIdea.get(DG_SIZE_IDEA).getValue();
        DG_SIZE = dgSize;
        
    }
    
  
    
}
