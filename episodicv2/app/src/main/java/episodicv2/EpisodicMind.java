/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
//import support.MindView;

/**
 *
 * @author karenlima
 */
public class EpisodicMind extends Mind {
    
    public EpisodicMind(){
        super();
        
        //Declare Memory Objects
        MemoryObject imagePerceptedMO;
        MemoryObject requestObjectRelationMO;
        MemoryObject requestObjectMO;
        MemoryObject boxCenterPointsMO;
        MemoryObject recognizedObjectsMO;
        MemoryObject iTCFeaturesMO;
        MemoryObject objectRelationMO;
        MemoryObject retrievedObjectTopDownMO;
        MemoryObject retrieveObjectRelationsMO;
        MemoryObject unintegratedScenePatternMO;
        MemoryObject midTermMemoryObjectRelationsMO;
        MemoryObject pRCProcess3toVLPFC;
        MemoryObject eNCtoDGandCA3;
        MemoryObject eNCtoMPFC;
        MemoryObject requestSomethingMO;
        MemoryObject newEncodedSceneMO;
        MemoryObject cA3toCA3Process;
        MemoryObject sceneMO;
        MemoryObject retrieveSimilarScenesBottomUpMO;
        MemoryObject retrieveSimilarScenesTopDownMO;
        MemoryObject similarScenesTopDownMO;
        MemoryObject similarScenesBottomUpMO;
        MemoryObject sceneStoreMO;
        MemoryObject cA3Process2toCA1;
        MemoryObject retrievedSceneTopDownMO;
        MemoryObject currentSceneMO;
        MemoryObject cA1toCA1Process2andSB;
        MemoryObject retrievedSceneTopDownLTMMO;
        MemoryObject sceneRelationMO;
        MemoryObject sceneIDMO;
        MemoryObject sceneRelationsArrayMO;
        MemoryObject sceneRelationsMO;
        MemoryObject retrievedSceneRelationTopDownMO;
        MemoryObject retrievedSceneRelationTopDownLTMMO;
        MemoryObject midTermMemorySceneRelationsMO;
        MemoryObject dGtoDGProcess;
        MemoryObject sBtoENC;
        
        
        imagePerceptedMO=super.createMemoryObject("IMAGE_PERCEPTED");
        requestObjectRelationMO=super.createMemoryObject("REQUEST_OBJECT_RELATION");
        requestObjectMO=super.createMemoryObject("REQUEST_OBJECT");
        boxCenterPointsMO=super.createMemoryObject("BOX_CENTER_POINTS");
        recognizedObjectsMO=super.createMemoryObject("RECOGNIZED_OBJECTS");
        iTCFeaturesMO=super.createMemoryObject("ITC_FEATURES");
        objectRelationMO=super.createMemoryObject("OBJECT_RELATION");
        retrievedObjectTopDownMO=super.createMemoryObject("RETRIEVED_OBJECT_TOP_DOWN");
        retrieveObjectRelationsMO=super.createMemoryObject("RETRIEVE_OBJECT_RELATIONS");
        unintegratedScenePatternMO=super.createMemoryObject("UNINTEGRATED_SCENE_PATTERN");
        midTermMemoryObjectRelationsMO=super.createMemoryObject("MID_TERM_MEMORY_OBJECT_RELATIONS");
        pRCProcess3toVLPFC=super.createMemoryObject("PRCPROCESS3_TO_VLPFC");
        eNCtoDGandCA3=super.createMemoryObject("ENC_TO_DG_AND_CA3");
        eNCtoMPFC=super.createMemoryObject("ENC_TO_MPFC");
        requestSomethingMO=super.createMemoryObject("REQUEST_SOMETHING");
        newEncodedSceneMO=super.createMemoryObject("NEW_ENCODED_SCENE");
        cA3toCA3Process=super.createMemoryObject("CA3_TO_CA3PROCESS");
        sceneMO=super.createMemoryObject("SCENE");
        retrieveSimilarScenesBottomUpMO=super.createMemoryObject("RETRIEVE_SIMILAR_SCENES_BOTTOM_UP");
        retrieveSimilarScenesTopDownMO=super.createMemoryObject("RETRIEVE_SIMILAR_SCENES_TOP_DOWN");
        similarScenesTopDownMO=super.createMemoryObject("SIMILAR_SCENES_TOP_DOWN");
        similarScenesBottomUpMO=super.createMemoryObject("SIMILAR_SCENES_BOTTOM_UP");
        sceneStoreMO=super.createMemoryObject("SCENE_STORE");
        cA3Process2toCA1=super.createMemoryObject("CA3PROCESS2_TO_CA1");
        retrievedSceneTopDownMO=super.createMemoryObject("RETRIEVED_SCENE_TOP_DOWN");
        currentSceneMO=super.createMemoryObject("CURRENT_SCENE");
        cA1toCA1Process2andSB=super.createMemoryObject("CA1_TO_CA1PROCESS2_AND_SB");
        retrievedSceneTopDownLTMMO=super.createMemoryObject("RETRIEVED_SCENE_TOP_DOWN_LTM");
        sceneRelationMO=super.createMemoryObject("SCENE_RELATION");
        sceneIDMO=super.createMemoryObject("SCENE_ID");
        sceneRelationsArrayMO=super.createMemoryObject("SCENE_RELATIONS_ARRAY");
        sceneRelationsMO=super.createMemoryObject("SCENE_RELATIONS");
        retrievedSceneRelationTopDownMO=super.createMemoryObject("RETRIEVED_SCENE_RELATION_TOP_DOWN");
        retrievedSceneRelationTopDownLTMMO=super.createMemoryObject("RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM");
        midTermMemorySceneRelationsMO=super.createMemoryObject("MID_TERM_MEMORY_SCENE_RELATIONS");
        dGtoDGProcess=super.createMemoryObject("DG_TO_DGPROCESS");
        sBtoENC=super.createMemoryObject("SB_TO_ENC");      
 
          
        // Create and populate MindViewer
//        MindView mv = new MindView("MindView");
//        mv.addMO(imagePerceptedaaMO);
        
        //Create Sensor Codelets
        Codelet vision=new Vision();
        vision.addOutput(imagePerceptedMO);
        insertCodelet(vision);
        
        start();
               
    }
}
