/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class ITCCodelet extends Codelet {
    
    Idea centerPointsandClassesIdea;
    MemoryObject centerPointsandClassesMO;
        
    Idea recognizedObjectsSpikeIdea;
    MemoryObject recognizedObjectsSpikeMO;
    
    public ITCCodelet() {
        
        setIsMemoryObserver(true);
        
    }

    @Override
    public void accessMemoryObjects() {
        System.out.println("Executing accessMemoryObjects ITCCodelet");
        
        centerPointsandClassesMO = (MemoryObject) getInput("centerPointsandClassesMO");
        centerPointsandClassesIdea = (Idea) centerPointsandClassesMO.getI();
        
        recognizedObjectsSpikeMO = (MemoryObject) getOutput("recognizedObjectsSpikeMO");
        recognizedObjectsSpikeIdea = (Idea) recognizedObjectsSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("Executing proc ITCCodelet");
        saveObjectsClasses();
    }
    
        private void saveObjectsClasses(){
        if (!centerPointsandClassesIdea.getL().isEmpty()) {
               //TODO: ver de dar copy na Idea
            recognizedObjectsSpikeIdea.setL(new ArrayList());
            ArrayList<Idea> objectsClasses = (ArrayList<Idea>) centerPointsandClassesIdea.get("objectsClasses").getValue();
            Integer currentFrame = (Integer) centerPointsandClassesIdea.get("currentFrame").getValue();

            ArrayList<Idea> objects = new ArrayList<Idea>();


            for (int i = 0; i < objectsClasses.size(); i++){
                Idea classIdea = new Idea("class",objectsClasses.get(i).get("label").getValue());
                Idea idIdea = new Idea("id",objectsClasses.get(i).get("classId").getValue());
                Idea pid = new Idea("pid",i);
                Idea features = new Idea("features","");

                Idea object = new Idea("object",null, "Property", 1);
                object.add(classIdea);
                object.add(idIdea);
                object.add(pid);  
                object.add(features);  
                objects.add(object);

            }
            Idea objectsArray = new Idea("objects", objects, "Property", 1);
            recognizedObjectsSpikeIdea.add(objectsArray);

            Idea currrentFrame = new Idea("currrentFrame",currentFrame, "Property", 1);
            Idea time = new Idea("time",null, "Property", 1);
            time.add(currrentFrame);
            recognizedObjectsSpikeIdea.add(time);

            recognizedObjectsSpikeMO.setI(recognizedObjectsSpikeIdea);
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
