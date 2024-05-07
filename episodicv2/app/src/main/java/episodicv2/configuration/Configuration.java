/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.configuration;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Luis
 */
public class Configuration {

    
    public static final int BRIDGE_PORT_PPC = 10500;
    public static final int BRIDGE_PORT_ITC = 10501;
    public static final int BRIDGE_PORT_VLPFC = 10502;
    public static final int BRIDGE_PORT_BODY = 10503;
    
    public static final int CONSOLIDATION_NODE_PORT = 11001;

    public static final String CONSOLIDATION_NODE_HOST = "100.112.15.252";
    public static final String PYTHON_CMD = "python";
    public static final String CONSOLIDATION_SERVICE_PATH = "consolidation_server";

    public static final int IMAGE_WIDTH = 640;
    public static final int IMAGE_HEIGHT = 480;
    public static final int GRID_COLUMNS_X = 10;
    public static final int GRID_ROWS_Y = 7;
    public static final double SIMILARITY_THRESHOLD = 0.8;
    public static final double RETRIEVAL_SIMILARITY_THRESHOLD = 0.8;
    public static final double WM_RETRIEVAL_SIMILARITY_THRESHOLD = 0.8;
    public static final double RETRIEVABILITY_DECREASING_SCALE = 1000 * 60 * 60 * 8; //1000 * 60 * 60 * 24; //IN DAYS
    public static final double RETRIEVABILITY_THRESHOLD = 0.0;
    public static final long CONSOLIDATION_INTERVAL = 1000 * 60 * 60 * 24;//1000 * 60 * 60 * 24;
    public static final double CONSOLIDATION_THRESHOLD = 0.0;
    
    
    ////Parameters that control the decaying of shorterm memory
    
    public static final long SHORT_TERM_DECAY_INTERVAL = 1000;
    public static final double ALTMAN_FUNCTION_SCALE = 0.055;
    
    //Parameters that control the activation of a memory trace
    
    public static final double SIGMOID_SCALE = 0.1;
    public static final double ALPHA_MEMORY_INC_RELEVANCE = 0.75;
    public static final double BETA_AFFECT_RELEVANCE = 0.25;
    public static final long EMOTIONAL_DECAY_INTERVAL = 500;
    public static final long DECAY_GAMMA = 500;
    

    public static final String ITC_FEATURES_BASEFILENAME = "class_";
    public static final String PRC_STORAGE_FILENAME = "prc_objects.txt";
    public static final String DG_STORAGE_FILENAME = "dg_scenes.txt";
    public static final String CA3_STORAGE_FILENAME = "ca3_scenes.txt";
    public static final String CA3_STORAGE_POS_AFFECT = "ca3_scenes_positive_affect.txt";
    public static final String CA3_STORAGE_NEG_AFFECT = "ca3_scenes_negative_affect.txt";
    public static final String CA1_STORAGE_BASEFILENAME = "ca1_";
    public static final String PRC_STORAGE_BASEFILENAME = "prc_";
    public static final String STORAGE_BASE_PATH = "knowledge";
    
    public static final String INS_STORAGE_FILENAME = "ins_data.txt";
    public static final String BLA_STORAGE_FILENAME = "bla_data.txt";
    public static final String VSVP_STORAGE_FILENAME = "vsvp_data.txt";
    public static final String BLA_RLV_STORAGE_FILENAME = "bla_relevance_data.txt";

    public static final boolean LOAD_MEMORY = false;
    
    // GENERAL IDEAS
    public static final String X_IDEA = "x";
    public static final String CURRENT_FRAME_IDEA = "currentFrame";
    public static final String Y_IDEA = "y";
    public static final String POINT_IDEA = "point";
    public static final String CLASS_IDEA = "class";
    public static final String LABEL_IDEA = "label";
    public static final String CLASS_ID_IDEA = "classid";
    public static final String ID_IDEA = "id";
    public static final String PID_IDEA = "pid";
    public static final String FEATURES_IDEA = "features";
    public static final String OBJECT_IDEA = "object";
    public static final String PATTERN_IDEA = "pattern";
    public static final String OBJECTS_IDEA = "objects";
    public static final String SCENE_ID_1_IDEA = "scene1IdIdea";
    public static final String SCENE_ID_2_IDEA = "scene2IdIdea";
    
//    public static final String PATTERN_REPLACED = "patternReplaced";
    public static final String TIME_IDEA = "time";
    public static final String POSITIVE_AFFECT_IDEA = "positiveAffect";
    public static final String AFFECT_INTENSITY_IDEA = "affectIntensity";
    public static final String NEGATIVE_AFFECT_IDEA = "negativeAffect";
    public static final String IMAGE_WIDTH_IDEA = "imageWidth";
    public static final String IMAGE_HEIGHT_IDEA = "imageHeight";
    public static final String GRID_COLUMNS_IDEA = "gridColumns";
    public static final String GRID_ROWS_IDEA = "negativeAffect";
    public static final String OBJECTS_CLASSES_IDEA = "objectClasses";
    public static final String OBJECTS_POINTS_IDEA = "objectPoints";
    public static final String REPETITIONS_IDEA = "repetitions";
    public static final String ACTIVATION_IDEA = "activation";
    public static final String TIMESTAMP_IDEA = "timestamp";
    public static final String RELATIONS_IDEA = "relations";
    public static final String ACTIVE_SIMILARITY_IDEA = "activeSimilarity";
    public static final String RECENT_IDEA = "recent";
    public static final String DG_SIZE_IDEA = "dGSize";
    public static final String DG_MEMORY_SCENES_IDEA = "dGMemoyScenes";
    public static final String MID_TERM_MEMORY_SCENES = "midTermMemoryScenes";
    public static final String MID_TERM_MEMORY_SCENES_BY_ID = "midTermMemoryScenesByID";
    public static final String MID_TERM_MEMORY_OBJECT_RELATIONS = "midTermMemoryObjectRelations";
    public static final String MID_TERM_MEMORY_OBJECT_RELATIONS_BY_ID = "midTermMemoryObjectRelationsByID";
    public static final String OBJECT_1_ID_IDEA = "object1IdIdea";
    public static final String OBJECT_2_ID_IDEA = "object2IdIdea2";
    public static final String UPDATED_IDEA = "updatedIdea";
    public static final String INDEX_FILE = "indexFile";
    public static final String RELATION_FILES_PRC = "relationFilesPRC";
    public static final String SOCKET_CONNECTION_PORT_IDEA = "socketConnectionIdea";
    public static final String IMAGE_RECEIVED_FROM_CONNECTION_IDEA = "imageReceivedFromConnectionIdea";
    
    // COMPLEX IDEAS
    
    public static final String IMAGE_RECEIVED_PATH_IDEA = "imageReceivedPathIdea";
    public static final String CENTER_POINTS_CLASSES_IDEA = "centerPointsandClassesIdea";
    public static final String RECOGNIZED_OBJECTS_SPIKE_IDEA = "recognizedObjectsSpikeIdea";
    public static final String CENTER_POINTS_SPIKE_IDEA = "centerPointsSpikeIdea";
    public static final String UNINTEGRATED_SCENE_PATTERN_IDEA = "unintegratedScenePatternIdea";
    public static final String NEW_ENCODED_SCENE_SPIKE_IDEA = "newEncodedSceneSpikeIdea";
    public static final String PRC_MID_TERM_OBJECT_RELATIONS_IDEA = "pRCMidTermMemoryObjectRelationsIdea";
    public static final String PATTERN_REPLACED_IDEA = "patternReplacedIdea";
    public static final String DG_DATA_IDEA = "dgDataIdea";
    public static final String ROOT_IDEA = "rootIdea";
    public static final String SCENE_IDEA = "sceneIdea";
    public static final String STORED_SCENE_IDEA = "storedSceneIdea";
    public static final String DG_MID_TERM_MEMORY_SCENES_IDEA = "dGMidTermMemoryScenesIdea";
    public static final String OBJECT_RELATION_IDEA = "objectRelationIdea";
    public static final String OBJECT_RELATIONS_IDEA = "objectRelationsIdea";
    public static final String PRC_DATA_IDEA = "pRCDataIdea";
    public static final String PRC_DATA_RELATIONS_IDEA = "pRCDataRelationsIdea";
    public static final String PRC_DATA_AFFECT_IDEA = "pRCDataAffectIdea";
    public static final String RECENT_NEW_ENCODED_SCENE_SPIKE_IDEA = "recentNewEncodedSceneSpikeIdea";
    public static final String NEW_ENCODED_SCENE_TO_STORE_IDEA = "newEncodedSceneToStoreIdea";
    public static final String SCENE_RELATION_VERTEX_TO_STORE_IDEA = "sceneRelationVertexToStoreIdea";
    public static final String SCENE_RELATION_IDEA = "sceneRelationIdea";
    
    
    
    
    // MEMORY OBJECTS
    
    public static final String ROOT_MO = "rootMO";
    public static final String IMAGE_RECEIVED_PATH_MO = "imageReceivedPathMO";
    public static final String SOCKET_CONNECTION_PORT_MO = "socketConnectionPortMO";
    public static final String IMAGE_RECEIVED_FROM_CONNECTION_MO= "imageReceivedFromConnectionMO";    
    public static final String CENTER_POINTS_CLASSES_MO = "centerPointsandClassesMO";
    public static final String CENTER_POINTS_SPIKE_MO = "centerPointsSpikeMO";
    public static final String UNINTEGRATED_SCENE_PATTERN_MO = "unintegratedScenePatternMO";
    public static final String RECOGNIZED_OBJECTS_SPIKE_MO = "recognizedObjectsSpikeMO";
    public static final String NEW_ENCODED_SCENE_SPIKE_MO = "newEncodedSceneSpikeMO";
    public static final String PRC_MID_TERM_OBJECT_RELATIONS_MO = "pRCMidTermMemoryObjectRelationsMO";
    public static final String PATTERN_REPLACED_MO = "patternReplacedMO";
    public static final String DG_MID_TERM_MEMORY_SCENES_MO = "dgMidTermMemoryScenesMO";
    public static final String RECENT_NEW_ENCODED_SCENE_SPIKE_MO = "recentNewEncodedSceneSpikeMO";
    public static final String NEW_ENCODED_SCENE_TO_STORE_MO = "newEncodedSceneToStoreMO";
    public static final String SCENE_RELATION_VERTEX_TO_STORE_MO = "sceneRelationVertexToStoreMO";
    
    
    
    
    
    


}
