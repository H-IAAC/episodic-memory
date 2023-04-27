/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package episodicv2;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.io.rest.RESTServer;
import br.unicamp.cst.util.viewer.MindViewer;

public final class App {
    
    Mind m;
    
    private void createAndShowGUI(Mind m) {
        MindViewer mv = new MindViewer(m,"MindViewer",m.getCodeletGroupList("Motor"));
        mv.setVisible(true);
    } 
    
    public Mind prepareMind() {
        m = new Mind();
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
        MemoryObject mPFCtoMPFCProcess1andDLPFCMO;
        MemoryObject taskSetMO;
        MemoryObject dLPFCtoMPFCProcess1MO;
        MemoryObject mPFCProcess1toENCMO;
        MemoryObject mPFCProcess1toDLPFCMO;
        MemoryObject retrievedObjectRelationsTopDownMO;
        MemoryObject vLPFCtoVLPFCProcess1MO;
 
        m.createMemoryGroup("VENTRAL_MOs");
        m.createMemoryGroup("DORSAL_MOs");
        m.createMemoryGroup("VISION_MOs");
        m.createMemoryGroup("CA3_MOs");
        m.createMemoryGroup("CA1_MOs");
        m.createMemoryGroup("DG_MOs");
        m.createMemoryGroup("SB_MOs");
        m.createMemoryGroup("PFC_MOs");
        m.createMemoryGroup("ENC_MOs");
        m.createCodeletGroup("VENTRAL_Cs");
        m.createCodeletGroup("DORSAL_Cs");
        m.createCodeletGroup("VISION_Cs");
        m.createCodeletGroup("CA3_Cs");
        m.createCodeletGroup("CA1_Cs");
        m.createCodeletGroup("DG_Cs");
        m.createCodeletGroup("SB_Cs");
        m.createCodeletGroup("PFC_Cs");
        m.createCodeletGroup("ENC_Cs");

        imagePerceptedMO=m.createMemoryObject("IMAGE_PERCEPTED");
        m.registerMemory(imagePerceptedMO,"VISION_MOs");
        
        boxCenterPointsMO=m.createMemoryObject("BOX_CENTER_POINTS");
        m.registerMemory(boxCenterPointsMO,"DORSAL_MOs"); //PHC
        
        unintegratedScenePatternMO=m.createMemoryObject("UNINTEGRATED_SCENE_PATTERN");
        m.registerMemory(unintegratedScenePatternMO,"DORSAL_MOs"); //PHC
        
        recognizedObjectsMO=m.createMemoryObject("RECOGNIZED_OBJECTS");
        m.registerMemory(recognizedObjectsMO,"VENTRAL_MOs"); //ITC
        
        iTCFeaturesMO=m.createMemoryObject("ITC_FEATURES");
        m.registerMemory(iTCFeaturesMO,"VENTRAL_MOs"); //ITC
        
        requestObjectMO=m.createMemoryObject("REQUEST_OBJECT");
        m.registerMemory(requestObjectMO,"VENTRAL_MOs"); //ITC
        
        retrievedObjectTopDownMO=m.createMemoryObject("RETRIEVED_OBJECT_TOP_DOWN");
        m.registerMemory(retrievedObjectTopDownMO,"VENTRAL_MOs"); //ITC
        
        requestObjectRelationMO=m.createMemoryObject("REQUEST_OBJECT_RELATION");
        m.registerMemory(requestObjectRelationMO,"VENTRAL_MOs"); //PRC
        
        objectRelationMO=m.createMemoryObject("OBJECT_RELATION");
        m.registerMemory(objectRelationMO,"VENTRAL_MOs"); //PRC
        
        retrieveObjectRelationsMO=m.createMemoryObject("RETRIEVE_OBJECT_RELATIONS");
        m.registerMemory(retrieveObjectRelationsMO,"VENTRAL_MOs"); //PRC
        
        midTermMemoryObjectRelationsMO=m.createMemoryObject("MID_TERM_MEMORY_OBJECT_RELATIONS");
        m.registerMemory(midTermMemoryObjectRelationsMO,"VENTRAL_MOs"); //PRC
        
        pRCProcess3toVLPFC=m.createMemoryObject("PRCPROCESS3_TO_VLPFC");
        m.registerMemory(pRCProcess3toVLPFC,"VENTRAL_MOs"); //PRC
        
        eNCtoDGandCA3=m.createMemoryObject("ENC_TO_DG_AND_CA3");
        m.registerMemory(eNCtoDGandCA3,"ENC_MOs");
        
        eNCtoMPFC=m.createMemoryObject("ENC_TO_MPFC");
        m.registerMemory(eNCtoMPFC,"ENC_MOs");
        
        cA3toCA3Process=m.createMemoryObject("CA3_TO_CA3PROCESS");
        m.registerMemory(cA3toCA3Process,"CA3_MOs");
        
        requestSomethingMO=m.createMemoryObject("REQUEST_SOMETHING");
        m.registerMemory(requestSomethingMO,"CA3_MOs");
        
        sceneMO=m.createMemoryObject("SCENE");
        m.registerMemory(sceneMO,"CA3_MOs");
        
        retrieveSimilarScenesBottomUpMO=m.createMemoryObject("RETRIEVE_SIMILAR_SCENES_BOTTOM_UP");
        m.registerMemory(retrieveSimilarScenesBottomUpMO,"CA3_MOs");
        
        retrieveSimilarScenesTopDownMO=m.createMemoryObject("RETRIEVE_SIMILAR_SCENES_TOP_DOWN");
        m.registerMemory(retrieveSimilarScenesTopDownMO,"CA3_MOs");
        
        similarScenesTopDownMO=m.createMemoryObject("SIMILAR_SCENES_TOP_DOWN");
        m.registerMemory(similarScenesTopDownMO,"CA3_MOs");
        
        similarScenesBottomUpMO=m.createMemoryObject("SIMILAR_SCENES_BOTTOM_UP");
        m.registerMemory(similarScenesBottomUpMO,"CA3_MOs");
        
        sceneStoreMO=m.createMemoryObject("SCENE_STORE");
        m.registerMemory(sceneStoreMO,"CA3_MOs");
        
        cA3Process2toCA1=m.createMemoryObject("CA3PROCESS2_TO_CA1");
        m.registerMemory(cA3Process2toCA1,"CA3_MOs");
        
        retrievedSceneTopDownMO=m.createMemoryObject("RETRIEVED_SCENE_TOP_DOWN");
        m.registerMemory(retrievedSceneTopDownMO,"CA3_MOs");
        
        currentSceneMO=m.createMemoryObject("CURRENT_SCENE");
        m.registerMemory(currentSceneMO,"CA1_MOs");
        
        cA1toCA1Process2andSB=m.createMemoryObject("CA1_TO_CA1PROCESS2_AND_SB");
        m.registerMemory(cA1toCA1Process2andSB,"CA1_MOs");
        
        retrievedSceneTopDownLTMMO=m.createMemoryObject("RETRIEVED_SCENE_TOP_DOWN_LTM");
        m.registerMemory(retrievedSceneTopDownLTMMO,"CA1_MOs");
        
        sceneRelationMO=m.createMemoryObject("SCENE_RELATION");
        m.registerMemory(sceneRelationMO,"CA1_MOs");
        
        sceneIDMO=m.createMemoryObject("SCENE_ID");
        m.registerMemory(sceneIDMO,"CA1_MOs");
        
        sceneRelationsArrayMO=m.createMemoryObject("SCENE_RELATIONS_ARRAY");
        m.registerMemory(sceneRelationsArrayMO,"CA1_MOs");
        
        sceneRelationsMO=m.createMemoryObject("SCENE_RELATIONS");
        m.registerMemory(sceneRelationsMO,"CA1_MOs");
        
        retrievedSceneRelationTopDownMO=m.createMemoryObject("RETRIEVED_SCENE_RELATION_TOP_DOWN");
        m.registerMemory(retrievedSceneRelationTopDownMO,"CA1_MOs");
        
        retrievedSceneRelationTopDownLTMMO=m.createMemoryObject("RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM");
        m.registerMemory(retrievedSceneRelationTopDownLTMMO,"CA1_MOs");
        
        midTermMemorySceneRelationsMO=m.createMemoryObject("MID_TERM_MEMORY_SCENE_RELATIONS");
        m.registerMemory(midTermMemorySceneRelationsMO,"CA1_MOs");
        
        dGtoDGProcess=m.createMemoryObject("DG_TO_DGPROCESS");
        m.registerMemory(dGtoDGProcess,"DG_MOs");
        
        newEncodedSceneMO=m.createMemoryObject("NEW_ENCODED_SCENE");
        m.registerMemory(newEncodedSceneMO,"DG_MOs");
        
        sBtoENC=m.createMemoryObject("SB_TO_ENC"); 
        m.registerMemory(sBtoENC,"SB_MOs");
        
        mPFCtoMPFCProcess1andDLPFCMO=m.createMemoryObject("MPFC_TO_MPFC_PROCESS_1AND_DLPFC"); 
        m.registerMemory(mPFCtoMPFCProcess1andDLPFCMO,"PFC_MOs");
        
        taskSetMO=m.createMemoryObject("TASK_SET"); 
        m.registerMemory(taskSetMO,"PFC_MOs");
        
        dLPFCtoMPFCProcess1MO=m.createMemoryObject("DLPFC_TO_MPFC_PROCESS_1"); 
        m.registerMemory(dLPFCtoMPFCProcess1MO,"PFC_MOs");
        
        mPFCProcess1toENCMO=m.createMemoryObject("MPFC_PROCESS_1_TO_ENC"); 
        m.registerMemory(mPFCProcess1toENCMO,"PFC_MOs");
        
        mPFCProcess1toDLPFCMO=m.createMemoryObject("MPFC_PROCESS_1_TO_DLPFC"); 
        m.registerMemory(mPFCProcess1toDLPFCMO,"PFC_MOs");
        
        retrievedObjectRelationsTopDownMO=m.createMemoryObject("RETRIEVED_OBJECT_RELATIONS_TOP_DOWN"); 
        m.registerMemory(retrievedObjectRelationsTopDownMO,"PFC_MOs");
        
        vLPFCtoVLPFCProcess1MO=m.createMemoryObject("VLPFC_TO_VLPFC_PROCESS_1"); 
        m.registerMemory(vLPFCtoVLPFCProcess1MO,"PFC_MOs");
        
        //Create Sensor Codelets
        Codelet vision=new Vision();
        vision.setName("VISION");
        vision.addOutput(imagePerceptedMO);
        m.insertCodelet(vision, "VISION_Cs");
        
        Codelet taskFrame=new TaskFrame();
        taskFrame.setName("TASK_NAME");
        taskFrame.addOutput(requestObjectRelationMO);
        taskFrame.addOutput(requestObjectMO);
        m.insertCodelet(taskFrame, "VISION_Cs");
        
        Codelet pC=new PC();
        pC.setName("PC");
        pC.addInput(imagePerceptedMO);
        pC.addOutput(boxCenterPointsMO);
        m.insertCodelet(pC, "DORSAL_Cs");
        
        Codelet pCBridge=new PCBridge();
        pCBridge.setName("PC_BRIDGE");
        pCBridge.addInput(boxCenterPointsMO);
        pCBridge.addOutput(boxCenterPointsMO);
        m.insertCodelet(pCBridge, "DORSAL_Cs");
        
        Codelet iTCObject=new ITCObject();
        iTCObject.setName("ITC_OBJECT");
        iTCObject.addInput(imagePerceptedMO);
        iTCObject.addOutput(recognizedObjectsMO);
        m.insertCodelet(iTCObject, "VENTRAL_Cs");
        
        Codelet iTCFeatures=new ITCFeatures();
        iTCFeatures.setName("ITC_FEATURES");
        iTCFeatures.addInput(imagePerceptedMO);
        iTCFeatures.addOutput(iTCFeaturesMO);
        m.insertCodelet(iTCFeatures, "VENTRAL_Cs");
        
        Codelet iTCBridge=new ITCBridge();
        iTCBridge.setName("ITC_BRIDGE");
        iTCBridge.addInput(recognizedObjectsMO);
        iTCBridge.addOutput(requestObjectMO);
        iTCBridge.addOutput(recognizedObjectsMO);
        m.insertCodelet(iTCBridge, "VENTRAL_Cs");
        
        Codelet iTCProcess1=new ITCProcess1();
        iTCProcess1.setName("ITC_PROCESS_1");
        iTCProcess1.addInput(requestObjectMO);
        iTCProcess1.addOutput(retrievedObjectTopDownMO);
        m.insertCodelet(iTCProcess1, "VENTRAL_Cs");
        
        Codelet pHCBridge=new PHCBridge();
        pHCBridge.setName("PHC_BRIDGE");
        pHCBridge.addInput(boxCenterPointsMO);
        pHCBridge.addOutput(boxCenterPointsMO);
        m.insertCodelet(pHCBridge, "DORSAL_Cs");
        
        Codelet pHCProcess1=new PHCProcess1();
        pHCProcess1.setName("PHC_PROCESS_1");
        pHCProcess1.addInput(boxCenterPointsMO);
        pHCProcess1.addOutput(unintegratedScenePatternMO);
        m.insertCodelet(pHCProcess1, "DORSAL_Cs");
        
        Codelet pRCBridge=new PRCBridge();
        pRCBridge.setName("PRC_BRIDGE");
        pRCBridge.addInput(requestObjectRelationMO);
        pRCBridge.addInput(recognizedObjectsMO);
        pRCBridge.addOutput(recognizedObjectsMO);
        pRCBridge.addOutput(requestObjectRelationMO);
        m.insertCodelet(pRCBridge, "VENTRAL_Cs");
        
        Codelet pRCProcess1=new PRCProcess1();
        pRCProcess1.setName("PRC_PROCESS_1");
        pRCProcess1.addInput(recognizedObjectsMO);
        pRCProcess1.addInput(requestObjectRelationMO);
        pRCProcess1.addOutput(recognizedObjectsMO);
        m.insertCodelet(pRCProcess1, "VENTRAL_Cs");
        
        Codelet pRCProcess2=new PRCProcess2();
        pRCProcess2.setName("PRC_PROCESS_2");
        pRCProcess2.addInput(recognizedObjectsMO);
        pRCProcess2.addOutput(objectRelationMO);
        m.insertCodelet(pRCProcess2, "VENTRAL_Cs");
        
        Codelet pRCProcess3=new PRCProcess3();
        pRCProcess3.setName("PRC_PROCESS_3");
        pRCProcess3.addInput(recognizedObjectsMO);
        pRCProcess3.addOutput(retrieveObjectRelationsMO);
        pRCProcess3.addOutput(pRCProcess3toVLPFC);
        m.insertCodelet(pRCProcess3, "VENTRAL_Cs");
        
        Codelet pRCStorageHandler=new PRCStorageHandler();
        pRCStorageHandler.setName("PRC_STORAGE_HANDLER");
        pRCStorageHandler.addInput(objectRelationMO);
        pRCStorageHandler.addInput(retrieveObjectRelationsMO);
        pRCStorageHandler.addOutput(midTermMemoryObjectRelationsMO);
        m.insertCodelet(pRCStorageHandler, "VENTRAL_Cs");
        
        Codelet eNC=new ENC();
        eNC.setName("ENC");
        eNC.addInput(unintegratedScenePatternMO);
        eNC.addInput(recognizedObjectsMO);
        eNC.addInput(sBtoENC);
        eNC.addOutput(eNCtoDGandCA3);
        eNC.addOutput(eNCtoMPFC);
        eNC.addOutput(requestSomethingMO);
        m.insertCodelet(eNC, "ENC_Cs");
        
        Codelet cA3=new CA3();
        cA3.setName("CA3");
        cA3.addInput(eNCtoDGandCA3);
        cA3.addInput(requestSomethingMO);
        cA3.addInput(newEncodedSceneMO);
        cA3.addOutput(newEncodedSceneMO);
        cA3.addOutput(cA3toCA3Process);
        cA3.addOutput(requestSomethingMO);
        m.insertCodelet(cA3, "CA3_Cs");
        
        Codelet cA3Process1=new CA3Process1();
        cA3Process1.setName("CA3_PROCESS_1");
        cA3Process1.addInput(newEncodedSceneMO);
        cA3Process1.addOutput(sceneMO);
        cA3Process1.addOutput(newEncodedSceneMO);
        m.insertCodelet(cA3Process1, "CA3_Cs");
        
        Codelet cA3Process2=new CA3Process2();
        cA3Process2.setName("CA3_PROCESS_2");
        cA3Process2.addInput(cA3toCA3Process);
        cA3Process2.addInput(similarScenesBottomUpMO);
        cA3Process2.addOutput(retrieveSimilarScenesBottomUpMO);
        cA3Process2.addOutput(cA3Process2toCA1);
        m.insertCodelet(cA3Process2, "CA3_Cs");
        
        Codelet cA3Process3=new CA3Process3();
        cA3Process3.setName("CA3_PROCESS_3");
        cA3Process3.addInput(requestSomethingMO);
        cA3Process3.addInput(similarScenesTopDownMO);
        cA3Process3.addOutput(retrieveSimilarScenesTopDownMO);
        cA3Process3.addOutput(retrievedSceneTopDownMO);
        m.insertCodelet(cA3Process3, "CA3_Cs");
        
        Codelet cA3StorageHandler=new CA3StorageHandler();
        cA3StorageHandler.setName("CA3_STORAGE_HANDLER");
        cA3StorageHandler.addInput(sceneMO);
        cA3StorageHandler.addInput(retrieveSimilarScenesBottomUpMO);
        cA3StorageHandler.addInput(retrieveSimilarScenesTopDownMO);
        cA3StorageHandler.addOutput(similarScenesBottomUpMO);
        cA3StorageHandler.addOutput(similarScenesTopDownMO);
        cA3StorageHandler.addOutput(sceneStoreMO);
        m.insertCodelet(cA3StorageHandler, "CA3_Cs");
        
        Codelet cA1=new CA1();
        cA1.setName("CA1");
        cA1.addInput(newEncodedSceneMO);
        cA1.addInput(cA3Process2toCA1);
        cA1.addInput(retrievedSceneTopDownMO);
        cA1.addOutput(newEncodedSceneMO);
        cA1.addOutput(cA1toCA1Process2andSB);
        cA1.addOutput(retrievedSceneTopDownLTMMO);
        cA1.addOutput(currentSceneMO);
        m.insertCodelet(cA1, "CA1_Cs");
        
        Codelet cA1Process1=new CA1Process1();
        cA1Process1.setName("CA1_PROCESS_1");
        cA1Process1.addInput(newEncodedSceneMO);
        cA1Process1.addOutput(sceneRelationMO);
        m.insertCodelet(cA1Process1, "CA1_Cs");
        
        Codelet cA1Process2=new CA1Process2();
        cA1Process2.setName("CA1_PROCESS_2");
        cA1Process2.addInput(cA1toCA1Process2andSB);
        cA1Process2.addInput(newEncodedSceneMO);
        cA1Process2.addInput(sceneRelationsArrayMO);
        cA1Process2.addOutput(sceneIDMO);
        cA1Process2.addOutput(retrievedSceneRelationTopDownMO);
        cA1Process2.addOutput(sceneRelationsMO);
        m.insertCodelet(cA1Process2, "CA1_Cs");
        
        Codelet cA1Process3=new CA1Process3();
        cA1Process3.setName("CA1_PROCESS_3");
        cA1Process3.addInput(retrievedSceneTopDownLTMMO);
        cA1Process3.addOutput(retrievedSceneRelationTopDownLTMMO);
        cA1Process3.addOutput(retrievedSceneRelationTopDownMO);
        m.insertCodelet(cA1Process3, "CA1_Cs");
        
        Codelet cA1StorageHandler=new CA1StorageHandler();
        cA1StorageHandler.setName("CA1_STORAGE_HANDLER");
        cA1StorageHandler.addInput(sceneRelationMO);
        cA1StorageHandler.addInput(sceneIDMO);
        cA1StorageHandler.addOutput(midTermMemorySceneRelationsMO);
        cA1StorageHandler.addOutput(sceneRelationsArrayMO);
        m.insertCodelet(cA1StorageHandler, "CA1_Cs");
        
        Codelet dGBridge=new DGBridge();
        dGBridge.setName("DG_BRIDGE");
        dGBridge.addInput(eNCtoDGandCA3);
        dGBridge.addOutput(dGtoDGProcess);
        m.insertCodelet(dGBridge, "DG_Cs");
        
        Codelet dGProcess=new DGProcess();
        dGProcess.setName("DG_PROCESS");
        dGProcess.addInput(dGtoDGProcess);
        dGProcess.addOutput(newEncodedSceneMO);
        m.insertCodelet(dGProcess, "DG_Cs");
        
        Codelet mPFC=new MPFC();
        mPFC.setName("MPFC");
        mPFC.addInput(eNCtoMPFC);
        mPFC.addOutput(mPFCtoMPFCProcess1andDLPFCMO);
        m.insertCodelet(mPFC, "PFC_Cs");
        
        Codelet mPFCProcess1=new MPFCProcess1();
        mPFCProcess1.setName("MPFC_PROCESS_1");
        mPFCProcess1.addInput(mPFCtoMPFCProcess1andDLPFCMO);
        mPFCProcess1.addInput(taskSetMO);
        mPFCProcess1.addInput(dLPFCtoMPFCProcess1MO);
        mPFCProcess1.addOutput(mPFCProcess1toENCMO);
        mPFCProcess1.addOutput(mPFCProcess1toDLPFCMO);
        m.insertCodelet(mPFCProcess1, "PFC_Cs");
        
        Codelet dLPFC=new DLPFC();
        dLPFC.setName("DLPFC");
        dLPFC.addInput(mPFCProcess1toDLPFCMO);
        dLPFC.addInput(mPFCtoMPFCProcess1andDLPFCMO);
        dLPFC.addInput(retrievedObjectTopDownMO);
        dLPFC.addInput(retrievedObjectRelationsTopDownMO);
        dLPFC.addOutput(taskSetMO);
        m.insertCodelet(dLPFC, "PFC_Cs");
        
        Codelet dLPFCPlanning1=new DLPFCPlanning();
        dLPFCPlanning1.setName("DLPFC_PLANNING");
        dLPFCPlanning1.addOutput(taskSetMO);
        m.insertCodelet(dLPFCPlanning1, "PFC_Cs");
        
        Codelet dLPFCBaseController=new DLPFCBaseController();
        dLPFCBaseController.setName("DLPFC_PLANNING");
        dLPFCBaseController.addOutput(requestObjectMO);
        dLPFCBaseController.addOutput(requestObjectRelationMO);
        dLPFCBaseController.addOutput(vLPFCtoVLPFCProcess1MO);
        m.insertCodelet(dLPFCBaseController, "PFC_Cs");
        
        Codelet vLPFC=new VLPFC();
        vLPFC.setName("VLPFC");
        vLPFC.addInput(pRCProcess3toVLPFC);
        vLPFC.addInput(retrievedObjectTopDownMO);
        vLPFC.addInput(iTCFeaturesMO);
        vLPFC.addOutput(vLPFCtoVLPFCProcess1MO);
        m.insertCodelet(vLPFC, "PFC_Cs");
        
        Codelet vLPFCProcess1=new VLPFCProcess1();
        vLPFCProcess1.setName("VLPFC_PROCESS_1");
        vLPFCProcess1.addInput(vLPFCtoVLPFCProcess1MO);
        vLPFCProcess1.addInput(requestObjectMO);
        vLPFCProcess1.addOutput(retrievedObjectTopDownMO);
        vLPFCProcess1.addOutput(requestObjectMO);
        m.insertCodelet(vLPFCProcess1, "PFC_Cs");
        
        Codelet vLPFCProcess2=new VLPFCProcess2();
        vLPFCProcess2.setName("VLPFC_PROCESS_2");
        vLPFCProcess2.addInput(requestObjectRelationMO);
        vLPFCProcess2.addOutput(requestObjectRelationMO);
        vLPFCProcess2.addOutput(retrievedObjectRelationsTopDownMO);
        m.insertCodelet(vLPFCProcess2, "PFC_Cs");
        
        Codelet sB=new SB();
        sB.setName("SB");
        sB.addInput(newEncodedSceneMO);
        sB.addInput(currentSceneMO);
        sB.addInput(cA1toCA1Process2andSB);
        sB.addInput(retrievedSceneTopDownLTMMO);
        sB.addInput(sceneRelationsMO);
        sB.addInput(retrievedSceneRelationTopDownMO);
        sB.addInput(retrievedSceneRelationTopDownMO);
        sB.addOutput(sBtoENC);
        m.insertCodelet(sB, "SB_Cs");
       
        m.start();
        return(m);
    }
    
    public App() {
        Mind m = prepareMind();
        // The next line can be commented if you don't want the Desktop MindViewer
        createAndShowGUI(m);
        // The next line can be commented if you don't use the MindViewer Web
        RESTServer rs = new RESTServer(m,5001,true);
    }
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        
    }
}
