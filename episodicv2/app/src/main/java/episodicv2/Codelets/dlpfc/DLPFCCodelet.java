/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.dlpfc;

import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.entities.Scene;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.t2string.T2DString;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author karenlima
 */
public class DLPFCCodelet extends DLPFCBaseController {
    private String className = this.getClass().getName();
    
    private MemoryObject mpfcProcess1SpikeMO;
    private Idea mpfcProcess1SpikeIdea;
    
    private TaskFrame taskFrame;

    private long queryStartTime = 0l;
    private long queryEndTime = 0l;
    private FileWriter resultsFile;
    private PrintWriter resultsWriter;

    //FOR BACKTRACKING
    private ArrayList<String> landMarks;
    private String chairsPattern = "<<(57)<(1)<(61:57)<(1)<(57),<<(61:57)(57)<(57)(1)(1),(3)(4)(6)(1)(2)(5),(3)(4)(5)(5)(6)(7),(4)(4)(3)(3)(4)(3)";
    private String carPattern = "<<<(1)<<(3)<(1)(10),<<(1)(3)<(1)<(10),(1)(2)(3)(4),(4)(6)(7)(7),(3)(3)(4)(5)";
    private String umbrellaPattern = "<<<<(1)(26)<<(1),<<<(1)(1)<(26),(1)(3)(2),(5)(5)(7),(4)(5)(4)";

    private String dogPattern = "<<(73)<<<(1)<(70)<<(17),<(70)<(1)(17)<(73),(3)(2)(4)(1),(3)(6)(7)(9),(4)(3)(2)(3)";
    private String bedRoomPattern = "<<(59)<(63)<(64)<<<(60),<(60)<(59)<(63)(64),(4)(1)(2)(3),(3)(4)(5)(8),(3)(4)(4)(2)";
    private String tvRoomPattern = "<(81)<(59)<<<(63)<<(59)<(81),<(81)(81)<<(59)(63)(59),(1)(5)(2)(3)(4),(2)(3)(6)(8)(9),(2)(4)(4)(4)(2)";

    private String officePattern = "<<(64:63)<<<<(42),<<(42)<(64:63),(3)(1)(2),(3)(3)(7),(4)(4)(3)"; //REVISAR
    private String winesPattern = "<(41)<(57:41)<(57:41)<(61)(41)<(57:40)<(57:41)<(41),<(41)(61)(41)<(57:41)(57:41)(41)(57:40)(57:41),(1)(6)(12)(2)(3)(4)(5)(7)(8)(9)(10)(11),(2)(3)(3)(4)(4)(5)(5)(6)(6)(7)(7)(8),(2)(3)(3)(3)(3)(2)(3)(3)(3)(3)(3)(2)";
    private String colibriPattern = "(59)<<<<<(78:22)<<<<(59),<<(59)(78:22)(59),(1)(2)(3)(4),(1)(6)(6)(10),(3)(3)(3)(3)"; //<<(14)<(15)<<(15)<<(15)<(15),<<(14)<<<(15)(15)(15)(15),(5)(1)(2)(4)(3),(3)(4)(6)(8)(9),(3)(6)(6)(6)(6)";

    private String startPattern = "";
    private String goalPattern = "";
    private int goalId = 0;

    private HashMap<Integer, ArrayList<SceneRelation>> sceneRelations;
    private HashMap<Integer, Scene> scenesByID;

    private int currentState = 0;

    private static final int VALIDATE_START = 1;
    private static final int VALIDATE_GOAL = 2;
    private static final int TRAVELING = 3;
    private static final int IDENTIFY_START = 4;

    private int waitForMaxRelations = 0;
    private int receivedRelations = 0;

    private LinkedList<Scene> visitedNodes;
    private LinkedList<Integer> visitedNodesID;
    private HashMap<Integer, Integer> visitedRelations;
    private Scene startScene;
    private Scene goalScene;
    private Scene currentScene;

    private final double NEGATIVE_AFFECT_THRESHOLD = 0.75;

     public DLPFCCodelet() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                taskFrame = new TaskFrame(className);
                taskFrame.setController(DLPFCCodelet.this);
                taskFrame.setVisible(true);
            }
        });
         
        setIsMemoryObserver(true);
        
        sceneRelations = new HashMap<>();
        scenesByID = new HashMap<>();
        visitedNodes = new LinkedList<>();
        visitedNodesID = new LinkedList<>();
        visitedRelations = new HashMap<>();

        landMarks = new ArrayList<>();

        landMarks.add(chairsPattern);
        landMarks.add(carPattern);
        landMarks.add(umbrellaPattern);

        landMarks.add(dogPattern);
        landMarks.add(bedRoomPattern);
        landMarks.add(tvRoomPattern);

        landMarks.add(officePattern);
        landMarks.add(winesPattern);
        landMarks.add(colibriPattern);

        getTaskSet().setAllowedProcessingFlow(SpikeObject.BOTTOM_UP_PROCESSING);
     }

    @Override
    public void accessMemoryObjects() {
        mpfcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.MPFC_PROCESS_1_SPIKE_MO);
        mpfcProcess1SpikeIdea = (Idea) mpfcProcess1SpikeMO.getI();
    }
    
    @Override
    public void proc() {
        
    }
    
    @Override
    public void calculateActivation(){
        
    }

    @Override
    public void setGoalPattern(String pattern) {
        goalPattern = pattern;
        System.out.println("The goal assigned is: " + goalPattern);    
    }

    @Override
    public void backtracking(String startPattern, String goalPattern) {
        currentState = VALIDATE_START;

        this.startPattern = startPattern;
        this.goalPattern = goalPattern;

        searchSceneByPattern(startPattern);    }

    @Override
    public void receivedCurrentScene(Scene scene) {

        if (getTaskSet().getAllowedProcessingFlow() == TaskSet.ALLOW_BOTTOM_UP) {

            getTaskSet().setAllowedProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);

            this.startPattern = scene.getPattern();

            searchMostAffectiveObject();

        }

        //META MANUAL
        /*if (getTaskSet().getAllowedProcessingFlow() == TaskSet.ALLOW_BOTTOM_UP) {

            
            getTaskSet().setAllowedProcessingFlow(SpikeObject.TOP_DOWN_PROCESSING);

            this.startPattern = scene.getPattern();

            //META ASIGNADA
            this.goalPattern = colibriPattern;

            SimpleLogger.log(this, "Agent view: " + this.startPattern);

            SimpleLogger.log(this, "Agent goal: " + this.goalPattern);

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SimpleLogger.log(this, "STARTING PLANNING");

            SpikeObject<TaskSet> spike = new SpikeObject(SpikeType.TASK_SET, getTaskSet(), 0);

            send(AreaNames.MPFCProcess1, spike.toBytes());

            backtracking(startPattern, goalPattern);

            /*SI SE QUIERE TENER UNA META AUTOMATICA
            if (goalPattern.isEmpty()) {
                searchMostAffectiveScene();
            }
        }*/
    }

    @Override
    public void receivedObject(CObject object) {

        queryEndTime = System.nanoTime();
        //SimpleLogger.log(this, "receivedObject, "+ queryEndTime);

        System.out.println((queryEndTime - queryStartTime));

        taskFrame.setImage(object.getFeaturesAsImage());

    }

    @Override
    public void receivedObjectRelations(ObjectRelations objectRelations) {

        queryEndTime = System.nanoTime();

        System.out.println((queryEndTime - queryStartTime));

        //SimpleLogger.log(this, "receivedObjectRelations");
    }

    @Override
    public void receiveScenes(ArrayList<Scene> scenes) {

        doRetrievedScene(scenes);
    }

    @Override
    public void receivedSceneRelations(ArrayList<SceneRelation> relations) {

        doRetrievedRelations(relations);
    }

    @Override
    public void receivedAffectiveObject(ObjectRelations object) {
        System.out.println("receivedAffectiveObject");
    }

    @Override
    public void receivedMostAffectiveObject(ObjectRelations object) {
        System.out.println("receivedMostAffectiveObject");

        GoalFrame goalFrame = new GoalFrame();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                goalFrame.setGoal(object);
                goalFrame.setVisible(true);
            }
        });

        this.goalId = object.getObjectId();

        System.out.println("Agent view: " + this.startPattern);

        System.out.println("Agent goal: " + this.goalId);

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("STARTING PLANNING");

        SpikeObject<TaskSet> spike = new SpikeObject(SpikeType.TASK_SET, getTaskSet(), 0);
        mpfcProcess1SpikeIdea.setValue(spike.toBytes());
        mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//        send(AreaNames.MPFCProcess1, spike.toBytes());

        backtracking(startPattern, goalPattern);
    }

    @Override
    public void receivedLessAffectiveObject(ObjectRelations object) {
        System.out.println("receivedLessAffectiveObject");
    }

    @Override
    public void receivedAffectiveScene(Scene scene) {
        System.out.println("receivedAffectiveScene");
    }

    @Override
    public void receivedMostPositiveAffectiveScene(Scene scene) {
        System.out.println("receivedMostAffectiveScene");

        /* SI SE QUIERE TENER UNA META AUTOMATICA
        goalPattern = scene.getPattern();

        SimpleLogger.log(this, "Agent view: " + this.startPattern);

        SimpleLogger.log(this, "Agent goal: " + this.goalPattern);

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleLogger.log(this, "STARTING PLANNING");

        SpikeObject<TaskSet> spike = new SpikeObject(SpikeType.TASK_SET, getTaskSet(), 0);

        send(AreaNames.MPFCProcess1, spike.toBytes());

        backtracking(startPattern, goalPattern);*/
    }

    @Override
    public void receivedLessPositiveAffectiveScene(Scene scene) {
        System.out.println("receivedLessAffectiveScene");
    }

    @Override
    public void receivedMostNegativeAffectiveScene(Scene scene) {
        System.out.println("receivedMostNegativeAffectiveScene");
    }

    @Override
    public void receivedLessNegativeAffectiveScene(Scene scene) {
        System.out.println("receivedLessNegativeAffectiveScene");
    }

    @Override
    public void receivedAffectiveSceneRelations(ArrayList<SceneRelation> relations) {
        System.out.println("receivedAffectiveSceneRelations");
    }
    
    //HANDLES THE RECEPTION OF THE SCENES
    private void doRetrievedScene(ArrayList<Scene> scenes) {
        queryEndTime = System.nanoTime();
        System.out.println((queryEndTime - queryStartTime));
        //SimpleLogger.log(this, "doRetrievedScene " + scenes);

        switch (currentState) {
            case VALIDATE_START:

                System.out.println("START SCENE: " + scenes.get(0).getId());

                waitForMaxRelations = scenes.size();
                startScene = scenes.get(0);
                scenesByID.put(startScene.getId(), startScene);

                currentScene = startScene;

                break;
            case VALIDATE_GOAL:

                System.out.println("GOAL SCENE: " + scenes.get(0).getId());

                waitForMaxRelations = scenes.size();
                goalScene = scenes.get(0);
                goalPattern = goalScene.getPattern();
                scenesByID.put(goalScene.getId(), goalScene);

                break;
            case TRAVELING:
                currentScene = scenes.get(0);
                visitedNodes.add(currentScene);
                visitedNodesID.add(currentScene.getId());
                addOrInitializeVR(currentScene.getId());
                break;
                
            default:
                break;
        }
    }
    
    //INITIALIZE THE CONTROLLER OF VISITED RELATIONS
    private void addOrInitializeVR(int sceneId) {

        Integer vr = visitedRelations.get(sceneId);

        if (vr == null) {
            visitedRelations.put(sceneId, -1);
        }
    }
    
    
    //HANDLES THE RECEPTION OF THE RELATIONS FOR EACH SCENE
    private void doRetrievedRelations(ArrayList<SceneRelation> relations) {

        switch (currentState) {
            case VALIDATE_START:
                if (!relations.isEmpty()) {
                    System.out.println("START SCENE RELATIONS: " + relations);

                    receivedRelations++;

                    sceneRelations.put(relations.get(0).getScene2Id(), relations);

                    if (receivedRelations >= waitForMaxRelations) {

                        receivedRelations = 0;

                        //currentState = VALIDATE_GOAL;
                        //CUANDO SE BUSCA SOLO UN OBJETO NO SE VALIDA
                        //searchSceneByPattern(goalPattern);
                        //CUANDO SE BUSCA UN OBJETO
                        currentState = TRAVELING;

                        visitedNodes.add(currentScene);
                        visitedNodesID.add(currentScene.getId());
                        addOrInitializeVR(currentScene.getId());

                        doEvaluation();

                        //
                    }

                } else {
                    System.out.println("UNKNOWN START SCENE");
                    return;
                }
                break;

            case VALIDATE_GOAL:

                if (!relations.isEmpty()) {

                    System.out.println("GOAL SCENE RELATIONS: " + relations);

                    receivedRelations++;

                    sceneRelations.put(relations.get(0).getScene2Id(), relations);

                    if (receivedRelations >= waitForMaxRelations) {
                        receivedRelations = 0;

                        currentState = TRAVELING;

                        visitedNodes.add(currentScene);
                        visitedNodesID.add(currentScene.getId());
                        addOrInitializeVR(currentScene.getId());

                        doEvaluation();
                    }

                    //currentState = TRAVELING;
                    //doEvaluation();
                } else {
                    System.out.println("UNKNOWN GOAL SCENE");
                    return;
                }

                break;

            case TRAVELING:
                System.out.println("SCENE TO EVALUATE " + currentScene.getId() + " " + relations);

                if (!relations.isEmpty()) {

                    sceneRelations.put(relations.get(0).getScene2Id(), relations);
                    doEvaluation();

                } else {
                    System.out.println("LA ESCENA NO TIENE RELACIONES");
                }

                break;
                
            default:
                break;

        }

    }
    
    /**
     * THE PLANNING
     */
    //STARTS THE BACKTRACKING PROCESS
    private void doEvaluation() {

        ArrayList<Integer>[][] content = T2DString.decodeMatrix(currentScene.getPattern(), Configuration.GRID_COLUMNS_X, Configuration.GRID_ROWS_Y);

        boolean has = hasGoal(content, this.goalId);
        
        //ESTOY PARADO EN LA META
        //float similarity = T2DString.lcs2DString(goalPattern, currentScene.getPattern(), T2DString.SIMILARITY_TYPE_1);

        //SimpleLogger.log("Comparing " + currentScene.toString() + " and " + goalPattern + ": " + similarity);

        if (has) {
            System.out.println("Scene has the object, Im on the goal");

            makePlan();

        }  else {

            System.out.println("Scene does not have the object, Im not on the goal");

            //START TRAVELING FROM THE LAST INDEX OF THE VISITED RELATION OF THIS SCENE
            int lastRelationIndex = visitedRelations.get(currentScene.getId());
            int newRelationIndex = lastRelationIndex;
            int sceneIdToSearch = -1;
            ArrayList<SceneRelation> srs = sceneRelations.get(currentScene.getId());

            for (int i = lastRelationIndex + 1; i < srs.size(); i++) {

                SceneRelation sr = srs.get(i);

                sceneIdToSearch = sr.getScene1Id();

                if (!visitedNodesID.contains(sceneIdToSearch)) {
                    newRelationIndex = i;
                    break;
                } else {
                    System.out.println("Cant travel to " + sceneIdToSearch);
                }

                System.out.println("Option: " + sr.toString());
            }

            if (newRelationIndex == lastRelationIndex) {

                System.out.println("CANT TRAVEL TO ANY RELATION, BACKWARD ");

                goBack();

            } else {
                System.out.println("Exploring: " + sceneIdToSearch);

                visitedRelations.put(currentScene.getId(), newRelationIndex);
                searchSceneByID(sceneIdToSearch);
            }

        }

    }
    
    public boolean hasGoal(ArrayList<Integer>[][] imageMatrix, int goalId) {

        for (int i = 0; i < imageMatrix.length; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {

                if (imageMatrix[i][j] == null) {
                    continue;
                }

                for (int k = 0; k < imageMatrix[i][j].size(); k++) {

                    if (imageMatrix[i][j].get(k) == goalId) {
                        return true;
                    }

                }

            }
        }

        return false;
    }

    //GO BACK AND CLEAN THE VISITED RELATIONS 
    private void goBack() {
        visitedNodes.removeLast();
        visitedNodesID.removeLast();
        visitedRelations.put(currentScene.getId(), -1);

        currentScene = visitedNodes.peekLast();

        doEvaluation();

    }
    
    //CREATE PLAN MOVEMENT
    private void makePlan() {

        System.out.println("The scenes plan is " + visitedNodesID);
        //SimpleLogger.log("The execution plan is " + visitedNodes);

        LinkedList<Integer> movementPlan = new LinkedList<>();
        LinkedList<Integer> reducedMovementPlan = new LinkedList<>();

        for (Scene scene : visitedNodes) {

            float similarity = 0.0f;
            int cIndex = -1;

            for (int i = 0; i < landMarks.size(); i++) {

                String lmk = landMarks.get(i);

                float cs = T2DString.lcs2DString(lmk, scene.getPattern(), T2DString.SIMILARITY_TYPE_1);

                if (cs > similarity) {
                    similarity = cs;
                    cIndex = i;
                }

            }

            if (reducedMovementPlan.peekLast() != null) {
                if (reducedMovementPlan.peekLast() != cIndex) {
                    reducedMovementPlan.add(cIndex);
                }
            } else {
                reducedMovementPlan.add(cIndex);
            }

            movementPlan.add(cIndex);

        }

        System.out.println("The execution plan is " + movementPlan);
        System.out.println("The reduced execution plan is " + reducedMovementPlan);

        sendPlan(reducedMovementPlan);
    }
    
    //SEND PLAN TO UNITY
    private void sendPlan(LinkedList<Integer> steps) {
        try {

            Socket commandSocket = new Socket("172.26.208.1", 11000);

            PrintWriter pw = new PrintWriter(commandSocket.getOutputStream());

            JSONObject commandJSON = new JSONObject();
            JSONArray plan = new JSONArray();

            // Add elements from ArrayList to JSONArray individually
            for (Integer step : steps) {
                plan.put(step); // Using put() to add elements to JSONArray
            }

            commandJSON.put("plan", plan);

            pw.println(commandJSON.toString());

            pw.close();

            commandSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
