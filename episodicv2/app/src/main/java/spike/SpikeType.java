/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spike;

/**
 *
 * @author Luis
 */
public class SpikeType {

    public static final int ENVIRONMENT_IMAGE = 1; //Image captured from the environment, used in python
    public static final int DETECTED_OBJECTS = 2; //List of detected objects, used in python
    public static final int CENTER_POINTS = 3; //List of center points of each object
    public static final int RECOGNIZED_OBJECTS = 4; //List of recognized objects
    public static final int OCCUPANCY_GRID = 5; //Occupancy grid
    public static final int UNINTEGRATED_SCENE_PATTERN = 6; //2D String pattern without class ID
    public static final int OBJECT_RELATIONS = 7; //Relations between objects
    public static final int NEW_ENCODED_SCENE = 8; //Scene representation
    public static final int SCENE_RELATIONS = 9; //List of relations
    public static final int SIMILAR_SCENES = 10; //List of similar scenes
    public static final int CURRENT_SCENE = 11; //Spikes to send to working memory

    public static final int REQUEST_OBJECT = 12;
    public static final int REQUEST_OBJECT_RELATION = 13;
    public static final int REQUEST_SIMILAR_SCENES = 14;
    public static final int REQUEST_SCENE_BY_ID = 15;

    public static final int RETRIEVED_OBJECT_TOP_DOWN = 16;
    public static final int RETRIEVED_OBJECT_RELATIONS_TOP_DOWN = 17;
    public static final int RETRIEVED_SCENE_TOP_DOWN = 18;
    public static final int RETRIEVED_SCENE_RELATION_TOP_DOWN = 19;

    public static final int RETRIEVED_SCENE_TOP_DOWN_LTM = 20;
    public static final int RETRIEVED_SCENE_RELATION_TOP_DOWN_LTM = 21;

    public static final int TASK_SET_ALLOWED_CLASSES = 22;
    public static final int TASK_SET_ALLOWED_SCENE = 23;
    public static final int TASK_SET = 24;

    public static final int PAIN_VALUES = 25;
    public static final int PLEASURE_VALUES = 26;
    public static final int AFFECTIVE_VALUE = 27;

    public static final int REQUEST_OBJECT_BY_AFFECT = 28;
    public static final int REQUEST_SCENE_BY_AFFECT = 29;
    public static final int RETRIEVED_AFFECTIVE_OBJECT_TOP_DOWN = 30;
    public static final int RETRIEVED_MOST_AFFECTIVE_OBJECT_TOP_DOWN = 31;
    public static final int RETRIEVED_LESS_AFFECTIVE_OBJECT_TOP_DOWN = 32;

    public static final int RETRIEVED_AFFECTIVE_SCENE_TOP_DOWN = 33;
    public static final int RETRIEVED_MOST_POS_AFFECTIVE_SCENE_TOP_DOWN = 34;
    public static final int RETRIEVED_LESS_POS_AFFECTIVE_SCENE_TOP_DOWN = 35;
    public static final int RETRIEVED_AFFECTIVE_SCENE_RELATION_TOP_DOWN = 36;
    public static final int RETRIEVED_MOST_NEG_AFFECTIVE_SCENE_TOP_DOWN = 37;
    public static final int RETRIEVED_LESS_NEG_AFFECTIVE_SCENE_TOP_DOWN = 38;

}
