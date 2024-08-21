/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.dlpfc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.entities.Scene;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.spike.TopDownParameters;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public abstract class DLPFCBaseController extends Codelet {
    
    private TaskSet taskSet;
    private MemoryObject dlpfcBaseControllerSpikeMO;
    private Idea dlpfcBaseControllerSpikeIdea;
    private MemoryObject vlpfcProcess1SpikeMO;
    private Idea vlpfcProcess1SpikeIdea;
    private MemoryObject vlpfcProcess2SpikeMO;
    private Idea vlpfcProcess2SpikeIdea;
    private MemoryObject vlpfcProcess3SpikeMO;
    private Idea vlpfcProcess3SpikeIdea;
    private MemoryObject mpfcProcess1SpikeMO;
    private Idea mpfcProcess1SpikeIdea;
    private MemoryObject mpfcProcess2SpikeMO;
    private Idea mpfcProcess2SpikeIdea;
    
    private byte[] data = null;
    
    public DLPFCBaseController() {

        taskSet = new TaskSet();
        /*
        this.ID = AreaNames.DLPFC;
        this.namer = AreaNames.class;

        taskSet = new TaskSet();
        addProcess(DLPFCProcess1.class);*/
    }


    @Override
    public void accessMemoryObjects() {
        dlpfcBaseControllerSpikeMO = (MemoryObject) getInput(Configuration.DLPFC_BASE_CONTROLLER_SPIKE_MO);
        dlpfcBaseControllerSpikeIdea = (Idea) dlpfcBaseControllerSpikeMO.getI();
        
        data = (byte[]) dlpfcBaseControllerSpikeIdea.get(Configuration.SPIKE_DLPFC_BASE_CONTROLLER_DATA_IDEA).getValue();
        
        
        vlpfcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_PROCESS_1_SPIKE_MO);
        vlpfcProcess1SpikeIdea = (Idea) vlpfcProcess1SpikeMO.getI();
        
        vlpfcProcess2SpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_PROCESS_2_SPIKE_MO);
        vlpfcProcess2SpikeIdea = (Idea) vlpfcProcess2SpikeMO.getI();
        
        vlpfcProcess3SpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_PROCESS_3_SPIKE_MO);
        vlpfcProcess3SpikeIdea = (Idea) vlpfcProcess3SpikeMO.getI();
        
        mpfcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.MPFC_PROCESS_1_SPIKE_MO);
        mpfcProcess1SpikeIdea = (Idea) mpfcProcess1SpikeMO.getI();
        
        mpfcProcess2SpikeMO = (MemoryObject) getOutput(Configuration.MPFC_PROCESS_2_SPIKE_MO);
        mpfcProcess2SpikeIdea = (Idea) mpfcProcess2SpikeMO.getI();
    }
    
    @Override
    public void proc() {

        SpikeObject rawSpike = SpikeObject.fromBytes(data);

        switch (rawSpike.getId()) {

            case SpikeType.CURRENT_SCENE:

                receivedCurrentScene((Scene) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_OBJECT_TOP_DOWN:

                receivedObject((CObject) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_OBJECT_RELATIONS_TOP_DOWN:

                receivedObjectRelations((ObjectRelations) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_SCENE_TOP_DOWN:
            case SpikeType.RETRIEVED_SCENE_TOP_DOWN_LTM:

                receiveScenes((ArrayList<Scene>) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN:
            case SpikeType.RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM:

                receivedSceneRelations((ArrayList<SceneRelation>) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_AFFECTIVE_OBJECT_TOP_DOWN:

                receivedAffectiveObject((ObjectRelations) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_MOST_AFFECTIVE_OBJECT_TOP_DOWN:

                receivedMostAffectiveObject((ObjectRelations) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_LESS_AFFECTIVE_OBJECT_TOP_DOWN:

                receivedLessAffectiveObject((ObjectRelations) rawSpike.getObject());

                break;

            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_TOP_DOWN:

                receivedAffectiveScene(((ArrayList<Scene>) rawSpike.getObject()).get(0));

                break;

            case SpikeType.RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN:

                receivedMostPositiveAffectiveScene(((ArrayList<Scene>) rawSpike.getObject()).get(0));

                break;

            case SpikeType.RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN:

                receivedLessPositiveAffectiveScene(((ArrayList<Scene>) rawSpike.getObject()).get(0));

                break;

            case SpikeType.RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN:

                receivedMostNegativeAffectiveScene(((ArrayList<Scene>) rawSpike.getObject()).get(0));

                break;

            case SpikeType.RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN:

                receivedLessNegativeAffectiveScene(((ArrayList<Scene>) rawSpike.getObject()).get(0));

                break;

            case SpikeType.RETRIEVED_AFFECTIVE_SCENE_RELATION_TOP_DOWN:

                receivedAffectiveSceneRelations((ArrayList<SceneRelation>) rawSpike.getObject());

                break;

            default:

                break;
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    
    /**
     * *
     * QUERIES TO RECOVER DATA FROM MEMORY
     */
    public void searchItem(SpikeObject spike) {
        vlpfcProcess1SpikeIdea.setValue(spike.toBytes());
        vlpfcProcess1SpikeMO.setI(vlpfcProcess1SpikeIdea);
//        send(AreaNames.VLPFCProcess1, spike.toBytes());
    }

    public void searchItemRelations(SpikeObject spike) {
        vlpfcProcess2SpikeIdea.setValue(spike.toBytes());
        vlpfcProcess2SpikeMO.setI(vlpfcProcess2SpikeIdea);
//        send(AreaNames.VLPFCProcess2, spike.toBytes());
    }

    public void searchScenes(SpikeObject spike) {
        mpfcProcess1SpikeIdea.setValue(spike.toBytes());
        mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//        send(AreaNames.MPFCProcess1, spike.toBytes());
    }

    public void searchSceneByID(SpikeObject spike) {
        mpfcProcess1SpikeIdea.setValue(spike.toBytes());
        mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//        send(AreaNames.MPFCProcess1, spike.toBytes());
    }

    public void searchSceneByPattern(String pattern) {
        Scene scene = new Scene(0, pattern, 0);
        SpikeObject<Scene> spike = new SpikeObject(SpikeType.REQUEST_SIMILAR_SCENES, scene, 0);
        mpfcProcess1SpikeIdea.setValue(spike.toBytes());
        mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//        send(AreaNames.MPFCProcess1, spike.toBytes());
    }

    public void searchSceneByID(int id) {
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_SCENE_BY_ID, id, 0);
        searchSceneByID(spike);
    }

    public void searchSceneRelations(SpikeObject spike) {
    }

    public void searchMostAffectiveObject() {
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_OBJECT_BY_AFFECT, TopDownParameters.MAX_AFFECT, 0);
        searchItemByAffect(spike);
    }

    public void searchLessAffectiveObject() {
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_OBJECT_BY_AFFECT, TopDownParameters.MIN_AFFECT, 0);
        searchItemByAffect(spike);
    }

    public void searchItemByAffect(SpikeObject spike) {
        vlpfcProcess3SpikeIdea.setValue(spike.toBytes());
        vlpfcProcess3SpikeMO.setI(vlpfcProcess3SpikeIdea);
//        send(AreaNames.VLPFCProcess3, spike.toBytes());
    }

    public void searchMostAffectiveScene() {
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_SCENE_BY_AFFECT, TopDownParameters.MAX_AFFECT, 0);
        searchSceneByAffect(spike);
    }

    public void searchLessAffectiveScene() {
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_SCENE_BY_AFFECT, TopDownParameters.MIN_AFFECT, 0);
        searchSceneByAffect(spike);
    }

    public void searchSceneByAffect(SpikeObject spike) {
        mpfcProcess2SpikeIdea.setValue(spike.toBytes());
        mpfcProcess2SpikeMO.setI(mpfcProcess2SpikeIdea);
//        send(AreaNames.MPFCProcess2, spike.toBytes());
    }

    public void setAllowedClasses(SpikeObject spike) {

        //FOR OBJECT
        vlpfcProcess1SpikeIdea.setValue(spike.toBytes());
        vlpfcProcess1SpikeMO.setI(vlpfcProcess1SpikeIdea);
//        send(AreaNames.VLPFCProcess1, spike.toBytes());

        //FOR RELATIONS
        vlpfcProcess2SpikeIdea.setValue(spike.toBytes());
        vlpfcProcess2SpikeMO.setI(vlpfcProcess2SpikeIdea);
//        send(AreaNames.VLPFCProcess2, spike.toBytes());
    }

    public void setAllowedScene(SpikeObject spike) {
        mpfcProcess1SpikeIdea.setValue(spike.toBytes());
        mpfcProcess1SpikeMO.setI(mpfcProcess1SpikeIdea);
//        send(AreaNames.MPFCProcess1, spike.toBytes());
    }

    public TaskSet getTaskSet() {
        return taskSet;
    }

    public void setTaskSet(TaskSet taskSet) {
        this.taskSet = taskSet;
    }

    
     /**
     * FOR PLANNING
     *
     * @param pattern
     */
    public abstract void setGoalPattern(String pattern);

    public abstract void backtracking(String startPattern, String goalPattern);
    
       /**
     * *
     * METHODS USED TO HANDLE THE RECEIVED SPIKES
     */
    public abstract void receivedCurrentScene(Scene scene);

    public abstract void receivedObject(CObject object);

    public abstract void receivedObjectRelations(ObjectRelations objectRelations);

    public abstract void receiveScenes(ArrayList<Scene> scenes);

    public abstract void receivedSceneRelations(ArrayList<SceneRelation> relations);

    public abstract void receivedAffectiveObject(ObjectRelations object);

    public abstract void receivedMostAffectiveObject(ObjectRelations object);

    public abstract void receivedLessAffectiveObject(ObjectRelations object);

    public abstract void receivedAffectiveScene(Scene scene);

    public abstract void receivedMostPositiveAffectiveScene(Scene scene);

    public abstract void receivedLessPositiveAffectiveScene(Scene scene);

    public abstract void receivedMostNegativeAffectiveScene(Scene scene);

    public abstract void receivedLessNegativeAffectiveScene(Scene scene);

    public abstract void receivedAffectiveSceneRelations(ArrayList<SceneRelation> relations);

}
