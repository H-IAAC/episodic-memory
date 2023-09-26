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

/**
 *
 * @author karenlima
 */
public class PPCCodelet extends Codelet {
    
    MemoryObject centerPointsandClassesMO;
    Idea centerPointsandClassesIdea;
    
    MemoryObject centerPointsMO;
    Idea centerPointsSpikeIdea;
    
    public PPCCodelet() {
        
        setIsMemoryObserver(true);
        
    }

    @Override
    public void accessMemoryObjects() {
        System.out.println("[PPC] Executing accessMemoryObjects PPCCodelet");
        
        centerPointsandClassesMO = (MemoryObject) getInput(CENTER_POINTS_CLASSES_MO);
        centerPointsandClassesIdea = (Idea) centerPointsandClassesMO.getI();
        
        centerPointsMO = (MemoryObject) getOutput(CENTER_POINTS_SPIKE_MO);
        centerPointsSpikeIdea = (Idea) centerPointsMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("[PPC] Executing proc PPCCodelet");
        saveObjectsPoints();
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

            centerPointsMO.setI(centerPointsSpikeIdea);
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
