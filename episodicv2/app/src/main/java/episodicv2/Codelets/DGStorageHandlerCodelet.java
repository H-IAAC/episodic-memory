/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.configuration.Configuration;
import episodicv2.emotions.ActivationFunctions;
import java.util.ArrayList;
import java.util.Map;
import t2dstring.T2DString;

/**
 *
 * @author karenlima
 */
public class DGStorageHandlerCodelet extends Codelet {
    
    MemoryObject rootMO;
    Idea rootIdea;
    
    MemoryObject patternReplacedMO;
    Idea patternReplacedIdea;

    MemoryObject newEncodedSceneSpikeMO;
    Idea newEncodedSceneSpikeIdea;
    
    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;
    
    //CARGA LAS ESCENAS EXISTENTES
    private static int DG_SIZE = 0;
    private static ArrayList<Idea> midTermMemoryScenes;
    private static Map<Integer, Idea> midTermMemoryScenesByID;
    
    @Override
    public void accessMemoryObjects() {
        //  TODO: revisar o que deve ser entrada ou saída para que modifique a root mas não notifique para ativar o codelet sempre que a root alterar
        rootMO = (MemoryObject) getInput("rootMO");
        rootIdea = (Idea) rootMO.getI();
        
        patternReplacedMO = (MemoryObject) getInput("patternReplacedMO");
        patternReplacedIdea = (Idea) patternReplacedMO.getI();
        
        newEncodedSceneSpikeMO = (MemoryObject) getOutput("newEncodedSceneSpikeMO");
        newEncodedSceneSpikeIdea = (Idea) newEncodedSceneSpikeMO.getI();
    }
    
    @Override
    public void proc() {
            String patternReplaced = (String) patternReplacedIdea.get("patternReplaced").getValue();
            Integer time = (Integer) patternReplacedIdea.get("time").getValue();
            Double positiveAffect = (double) patternReplacedIdea.get("positiveAffect").getValue();
            Double negativeAffect = (double) patternReplacedIdea.get("negativeAffect").getValue();
            Double affectIntensity = (double) patternReplacedIdea.get("affectIntensity").getValue();
            
          /**
             * INTENTA CREAR UNA NUEVA ESCENA SINO EXISTE UN PATRON SIMILAR SI
             * EL PATRON EXISTE LO RETORNA PARA ASOCIACION
             */

            Idea scene = createNewScene(patternReplaced, positiveAffect, negativeAffect, affectIntensity, time);
            
            newEncodedSceneSpikeIdea.add(scene);
            newEncodedSceneSpikeMO.setI(newEncodedSceneSpikeIdea);
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

            assignedId = DG_SIZE;
            Idea idIdea = new Idea("id",assignedId, "Property", 1);
            Idea patternIdea = new Idea("patternReplaced",pattern, "Property", 1);
            Idea timeIdea = new Idea("time",time, "Property", 1);
            Idea repetitionsIdea = new Idea("repetitions",null, "Property", 1);
            Idea positiveAffectIdea = new Idea("positiveAffect",0.0, "Property", 1);
            Idea negativeAffectIdea = new Idea("negativeAffect",0.0, "Property", 1);
            Idea activationIdea = new Idea("activation",0.5, "Property", 1);
            Idea timestampIdea = new Idea("timestamp",System.currentTimeMillis(), "Property", 1);
            Idea relationsIdea = new Idea("relations",null, "Property", 1);
            Idea activeSimilarityIdea = new Idea("activeSimilarity",null, "Property", 1);
            Idea recentIdea = new Idea("recent",null, "Property", 1);
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

        for (int i = 0; i < midTermMemoryScenes.size(); i++) {

            Idea scene = midTermMemoryScenes.get(i);
            String patternToCompare = (String) scene.get("patternReplaced").getValue();

            float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

            if (similarity >= SIMILARITY_THRESHOLD) {
                similar = scene;
                scene = updateSceneActivationWithAffect(affect, scene);
                scene.get("timestamp").setValue(System.currentTimeMillis());

                break;
            }

        }

        //SI NO HAY UN PATRON SIMILAR EN MID-TERM LO BUSCA EN LONG-TERM
        if (similar == null) {
            Idea sceneLinesStoredIdea = rootIdea.get("dgMemoyScenesIdea");
                ArrayList<String> sceneLines = (ArrayList<String>) sceneLinesStoredIdea.get("sceneLines").getValue();
                for (String line: sceneLines) {
                    Idea sceneIdea = storedSceneToIdea(line);

                    String patternToCompare = (String) sceneIdea.get("patternReplaced").getValue();

                    float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

                    if (similarity >= SIMILARITY_THRESHOLD) {

                        similar = sceneIdea;

                        sceneIdea = updateSceneActivationWithAffect(affect, sceneIdea);
                        sceneIdea.get("timestamp").setValue(System.currentTimeMillis());

                        Integer idScene = (Integer) sceneIdea.get("id").getValue();
                        midTermMemoryScenes.add(sceneIdea);
                        midTermMemoryScenesByID.put(idScene, sceneIdea);

                        break;
                    }
                }
        }

        return similar;
    }
    
    public Idea updateSceneActivationWithAffect(double affectIntensity, Idea scene) {

        Double repetitions = (double) scene.get("repetitions").getValue();
        Double activation = (double) scene.get("activation").getValue();
        Integer id = (Integer) scene.get("objectId").getValue();
        repetitions++;

        double scale = Configuration.SIGMOID_SCALE;
        double alpha = Configuration.ALPHA_MEMORY_INC_RELEVANCE;
        double beta = Configuration.BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, affectIntensity, alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);
        activation = (activation + currentActivation) / 2.0;
        
        System.out.println("Scene: "+id+" activation: "+this.activation+" repetition: "+repetitions);
        
        scene.get("repetitions").setValue(repetitions);
        scene.get("activation").setValue(activation);
        
        return scene;
        
    }
    
    public Idea updateScenePositiveAffect(double affect, Idea scene) {
        Double positiveAffect = (double) scene.get("positiveAffect").getValue();
        positiveAffect = (positiveAffect + affect) / 2;
        scene.get("positiveAffect").setValue(positiveAffect);
        return scene;
    }

    public Idea updateSceneNegativeAffect(double affect, Idea scene) {
        Double negativeAffect = (double) scene.get("negativeAffect").getValue();
        negativeAffect = (negativeAffect + affect) / 2;
        scene.get("negativeAffect").setValue(negativeAffect);
        return scene;
    }
    
        // 1,<(59)<<(63)<(64)<<<(60),<(60)<(59)<(63)(64),(4)(1)(2)(3),(2)(4)(5)(8),(3)(4)(4)(2),0000000176,0.93745,0.14345,1.00000,00000000000000000002,1607465245977

    private Idea storedSceneToIdea(String storedString) {
        Idea storedSceneIdea = new Idea("storedScene", null, "Property", 1);
        
        String data[] = storedString.split(",");
        
        Idea idIdea = new Idea("id", Integer.parseInt(data[0]), "Property", 1);
        Idea patternIdea = new Idea("pattern",data[1] + "," + data[2] + "," + data[3] + "," + data[4] + "," + data[5], "Property", 1);
        Idea timeIdea = new Idea("time",Integer.parseInt(data[10]), "Property", 1);
        Idea repetitionsIdea = new Idea("repetitions",Integer.parseInt(data[6]), "Property", 1);
        Idea positiveAffectIdea = new Idea("positiveAffect",Double.parseDouble(data[7]), "Property", 1);
        Idea negativeAffectIdea = new Idea("negativeAffect",Double.parseDouble(data[8]), "Property", 1);
        Idea activationIdea = new Idea("activation", Double.parseDouble(data[9]), "Property", 1);
        Idea timestampIdea = new Idea("timestamp",Long.parseLong(data[11]), "Property", 1);
        Idea relationsIdea = new Idea("relations",null, "Property", 1);
        Idea activeSimilarityIdea = new Idea("activeSimilarity",null, "Property", 1);
        Idea recentIdea = new Idea("recent",false, "Property", 1);
        
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
    
}
