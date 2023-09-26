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
public class ITCCodelet extends Codelet {
    
    MemoryObject centerPointsandClassesMO;
    Idea centerPointsandClassesIdea;
       
    MemoryObject recognizedObjectsSpikeMO;
    Idea recognizedObjectsSpikeIdea;
    
    public ITCCodelet() {
        
        setIsMemoryObserver(true);
        
    }

    @Override
    public void accessMemoryObjects() {
        
        System.out.println("[ITC] Executing accessMemoryObjects ITCCodelet");
        System.out.println("[ITC] inputs"); 
        System.out.println(getInputs());
        centerPointsandClassesMO = (MemoryObject) getInput(CENTER_POINTS_CLASSES_MO);
        centerPointsandClassesIdea = (Idea) centerPointsandClassesMO.getI();
        
        recognizedObjectsSpikeMO = (MemoryObject) getOutput(RECOGNIZED_OBJECTS_SPIKE_MO);
        recognizedObjectsSpikeIdea = (Idea) recognizedObjectsSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("[ITC] Executing proc ITCCodelet");
        saveObjectsClasses();
    }
    
        private void saveObjectsClasses(){
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
                Idea object = new Idea(OBJECT_IDEA,null, "Property", 1);
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
    
    @Override
    public void calculateActivation(){
        
    }
    
}
