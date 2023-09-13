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
public class PHCProcess1 extends Codelet {
    
    
    MemoryObject centerPointsSpikeMO;
    Idea centerPointsSpikeIdea;
    
    MemoryObject unintegratedScenePatternMO;
    Idea unintegratedScenePatternIdea;
    
    public PHCProcess1() {
        setIsMemoryObserver(true);
    }
    
    @Override
    public void accessMemoryObjects() {
        
        System.out.println("Executing accessMemoryObjects PHCProcess1");

        centerPointsSpikeMO = (MemoryObject) getInput(CENTER_POINTS_SPIKE_MO);
        centerPointsSpikeIdea = (Idea) centerPointsSpikeMO.getI();
        unintegratedScenePatternMO = (MemoryObject) getOutput(UNINTEGRATED_SCENE_PATTERN_MO);
        unintegratedScenePatternIdea = (Idea) unintegratedScenePatternMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("Executing proc PHCProcess1");
        if (centerPointsSpikeIdea.get(OBJECTS_IDEA) != null) {
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
            System.out.println("2DStringPattern generated:");
            System.out.println(pattern);
            unintegratedScenePatternIdea.add(patternIdea);
            
            Integer currentFrame = (Integer) centerPointsSpikeIdea.get(CURRENT_FRAME_IDEA).getValue();
            Idea currentFrameIdea = new Idea(CURRENT_FRAME_IDEA,currentFrame, "Property", 1);
            unintegratedScenePatternIdea.add(currentFrameIdea);
            unintegratedScenePatternMO.setI(unintegratedScenePatternIdea);
        }
       
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
