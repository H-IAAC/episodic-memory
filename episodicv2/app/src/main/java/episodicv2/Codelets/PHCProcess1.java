/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;

import t2dstring.OccupancyGrid;
import t2dstring.T2DString;

/**
 *
 * @author karenlima
 */
public class PHCProcess1 extends Codelet {
    
    MemoryObject unintegratedScenePatternMO;
    Idea unintegratedScenePatternIdea;
    
    MemoryObject centerPointsSpikeMO;
    Idea centerPointsSpikeIdea;
    
    public PHCProcess1() {
        setIsMemoryObserver(true);
    }
    
    @Override
    public void accessMemoryObjects() {
        
        System.out.println("Executing accessMemoryObjects PHCProcess1");

        centerPointsSpikeMO = (MemoryObject) getInput("centerPointsSpikeMO");
        centerPointsSpikeIdea = (Idea) centerPointsSpikeMO.getI();
        unintegratedScenePatternMO = (MemoryObject) getOutput("unintegratedScenePatternMO");
        unintegratedScenePatternIdea = (Idea) unintegratedScenePatternMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("Executing proc PHCProcess1");
        if (centerPointsSpikeIdea.get("objects") != null) {
            ArrayList<Idea> objects = (ArrayList<Idea>) centerPointsSpikeIdea.get("objects").getValue();
            Idea imageWidth1 = new Idea("imageWidth",640, "Property", 1);
            Idea imageHeight1 = new Idea("imageHeight",480, "Property", 1);
            Idea gridColumnsX1 = new Idea("gridColumns",10, "Property", 1);
            Idea gridRowsY1 = new Idea("gridRows",7, "Property", 1);
            
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

            System.out.println("2DStringPattern generated:");
            System.out.println(pattern);
            unintegratedScenePatternIdea.setValue(pattern);
            unintegratedScenePatternMO.setI(unintegratedScenePatternIdea);
        }
       
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
