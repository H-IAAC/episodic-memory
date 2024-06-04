/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.configuration.Configuration.*;
import java.util.ArrayList;
import t2dstring.OccupancyGrid;
import t2dstring.T2DString;

/**
 *
 * @author karenlima
 */
public class PPCPHCITCCodelet extends Codelet {
    
    MemoryObject centerPointsandClassesMO;
    Idea centerPointsandClassesIdea;
    
    MemoryObject recognizedObjectsSpikeAndunintegratedScenePatternMO;
    Idea recognizedObjectsSpikeAndunintegratedScenePatternIdea;
    
    MemoryObject recognizedObjectsSpikeMO;
    Idea recognizedObjectsSpikeIdea;
    
    Idea centerPointsSpikeIdea = new Idea(CENTER_POINTS_SPIKE_IDEA, null,"Property",1);;
    Idea unintegratedScenePatternIdea = new Idea(UNINTEGRATED_SCENE_PATTERN_IDEA, null,"Property",1);;
    
    public PPCPHCITCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        
        centerPointsandClassesMO = (MemoryObject) getInput(CENTER_POINTS_CLASSES_MO);
        centerPointsandClassesIdea = (Idea) centerPointsandClassesMO.getI();
        
        recognizedObjectsSpikeAndunintegratedScenePatternMO = (MemoryObject) getOutput(RECOGNIZED_OBJECTS_SPIKE_AND_UNINTEGRATED_SCENE_PATTERN_MO);
        recognizedObjectsSpikeAndunintegratedScenePatternIdea = (Idea) recognizedObjectsSpikeAndunintegratedScenePatternMO.getI();
        
        recognizedObjectsSpikeMO = (MemoryObject) getOutput(RECOGNIZED_OBJECTS_SPIKE_MO);
        recognizedObjectsSpikeIdea = (Idea) recognizedObjectsSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        saveObjectsPoints();
        createnintegratedScenePattern();
        createRecognizedObjects();
        setOutputIdeas();
    }
    
    private void saveObjectsPoints(){
        if (!centerPointsandClassesIdea.getL().isEmpty()) {
            //TODO: ver de dar copy na Idea
            centerPointsSpikeIdea.setL(new ArrayList());
            ArrayList<Idea> objectsPoints = (ArrayList<Idea>) centerPointsandClassesIdea.get(OBJECTS_POINTS_IDEA).getValue();
            Integer currentFrame = (Integer) centerPointsandClassesIdea.get(CURRENT_FRAME_IDEA).getValue();

            ArrayList<Idea> objects = new ArrayList<Idea>();

            for (int i = 0; i < objectsPoints.size(); i++){
                Idea x = new Idea(X_IDEA,objectsPoints.get(i).get(X_IDEA).getValue());
                Idea y = new Idea(Y_IDEA,objectsPoints.get(i).get(Y_IDEA).getValue());
                Idea pid = new Idea(PID_IDEA,i);

                Idea object = new Idea(OBJECT_IDEA, null, "Property", 1);
                object.add(x);
                object.add(y);
                object.add(pid);  
                objects.add(object);

            }
            Idea objectsArray = new Idea(OBJECTS_IDEA, objects, "Property", 1);
            centerPointsSpikeIdea.add(objectsArray);

            Idea currentFrameIdea = new Idea(CURRENT_FRAME_IDEA,currentFrame, "Property", 1);
            centerPointsSpikeIdea.add(currentFrameIdea);
        }
    }
    
     private void createnintegratedScenePattern() {
        if (centerPointsSpikeIdea.get(OBJECTS_IDEA) != null) {
            unintegratedScenePatternIdea.setL(new ArrayList());
            ArrayList<Idea> objects = (ArrayList<Idea>) centerPointsSpikeIdea.get(OBJECTS_IDEA).getValue();
            Idea imageWidth1 = new Idea(IMAGE_WIDTH_IDEA,640, "Property", 1);
            Idea imageHeight1 = new Idea(IMAGE_HEIGHT_IDEA,480, "Property", 1);
            Idea gridColumnsX1 = new Idea(GRID_COLUMNS_IDEA,10, "Property", 1);
            Idea gridRowsY1 = new Idea(GRID_ROWS_IDEA,7, "Property", 1);
            
            //Creates the Occupancy Grid
            OccupancyGrid occupancyGrid = T2DString.createOccupancyGrid(
                        objects,
                        (int) imageWidth1.getValue(),
                        (int) imageHeight1.getValue(),
                        (int) gridColumnsX1.getValue(),
                        (int) gridRowsY1.getValue());

            //Creates the 2D String pattern
            String pattern = T2DString.create2DStringPattern(occupancyGrid,
                    (int) imageWidth1.getValue(),
                    (int) imageHeight1.getValue(),
                    (int) gridColumnsX1.getValue(),
                    (int) gridRowsY1.getValue());
            Idea patternIdea = new Idea(PATTERN_IDEA, pattern, "Property", 1);
            
            unintegratedScenePatternIdea.add(patternIdea);
            
            Integer currentFrame = (Integer) centerPointsSpikeIdea.get(CURRENT_FRAME_IDEA).getValue();
            Idea currentFrameIdea = new Idea(CURRENT_FRAME_IDEA, currentFrame, "Property", 1);
            System.out.println("[PHC] 2DStringPattern generated: " + pattern + " on frame: " + currentFrame);
            unintegratedScenePatternIdea.add(currentFrameIdea);
        }
    }
    
    private void createRecognizedObjects() {
        if (!centerPointsandClassesIdea.getL().isEmpty()) {
               //TODO: ver de dar copy na Idea
            recognizedObjectsSpikeIdea.setL(new ArrayList());
            ArrayList<Idea> objectsClasses = (ArrayList<Idea>) centerPointsandClassesIdea.get(OBJECTS_CLASSES_IDEA).getValue();
            Integer currentFrame = (Integer) centerPointsandClassesIdea.get(CURRENT_FRAME_IDEA).getValue();

            ArrayList<Idea> objects = new ArrayList<Idea>();


            for (int i = 0; i < objectsClasses.size(); i++){
                Idea classIdea = new Idea(CLASS_IDEA,objectsClasses.get(i).get(LABEL_IDEA).getValue());
                Idea idIdea = new Idea(ID_IDEA,objectsClasses.get(i).get(CLASS_ID_IDEA).getValue());
                Idea pid = new Idea(PID_IDEA,i);
                Idea features = new Idea(FEATURES_IDEA,"");
                Idea object = new Idea(OBJECT_IDEA, null, "Property", 1);
                object.add(classIdea);
                object.add(idIdea);
                object.add(pid);  
                object.add(features);  
                objects.add(object);

            }
            Idea objectsArray = new Idea(OBJECTS_IDEA, objects, "Property", 1);
            recognizedObjectsSpikeIdea.add(objectsArray);

            Idea currentFrameIdea = new Idea(CURRENT_FRAME_IDEA,currentFrame, "Property", 1);

            recognizedObjectsSpikeIdea.add(currentFrameIdea);

            recognizedObjectsSpikeMO.setI(recognizedObjectsSpikeIdea);
        }
    }
    
    private void setOutputIdeas() {
        recognizedObjectsSpikeAndunintegratedScenePatternIdea.add(recognizedObjectsSpikeIdea);
        recognizedObjectsSpikeAndunintegratedScenePatternIdea.add(unintegratedScenePatternIdea);
        recognizedObjectsSpikeAndunintegratedScenePatternMO.setI(recognizedObjectsSpikeAndunintegratedScenePatternIdea);
        recognizedObjectsSpikeMO.setI(recognizedObjectsSpikeIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
