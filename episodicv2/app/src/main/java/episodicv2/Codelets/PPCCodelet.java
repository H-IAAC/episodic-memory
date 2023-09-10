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
public class PPCCodelet extends Codelet {
    
    Idea centerPointsandClassesIdea;
    MemoryObject centerPointsandClassesMO;
        
    Idea centerPointsSpikeIdea;
    MemoryObject centerPointsMO;
    
    public PPCCodelet() {
        
        setIsMemoryObserver(true);
        
    }

    @Override
    public void accessMemoryObjects() {
        System.out.println("Executing accessMemoryObjects PPCCodelet");
        
        centerPointsandClassesMO = (MemoryObject) getInput("centerPointsandClassesMO");
        centerPointsandClassesIdea = (Idea) centerPointsandClassesMO.getI();
        
        centerPointsMO = (MemoryObject) getOutput("centerPointsSpikeMO");
        centerPointsSpikeIdea = (Idea) centerPointsMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("Executing proc PPCCodelet");
        saveObjectsPoints();
    }
    
    private void saveObjectsPoints(){
        if (!centerPointsandClassesIdea.getL().isEmpty()) {
               //TODO: ver de dar copy na Idea
            centerPointsSpikeIdea.setL(new ArrayList());
            ArrayList<Idea> objectsPoints = (ArrayList<Idea>) centerPointsandClassesIdea.get("objectsPoints").getValue();
            Integer currentFrame = (Integer) centerPointsandClassesIdea.get("currentFrame").getValue();

            ArrayList<Idea> objects = new ArrayList<Idea>();


            for (int i = 0; i < objectsPoints.size(); i++){
                Idea x = new Idea("x",objectsPoints.get(i).get("x").getValue());
                Idea y = new Idea("y",objectsPoints.get(i).get("y").getValue());
                Idea pid = new Idea("pid",i);

                Idea object = new Idea("object",null, "Property", 1);
                object.add(x);
                object.add(y);
                object.add(pid);  
                objects.add(object);

            }
            Idea objectsArray = new Idea("objects", objects, "Property", 1);
            centerPointsSpikeIdea.add(objectsArray);

            Idea currrentFrame = new Idea("currrentFrame",currentFrame, "Property", 1);
            Idea time = new Idea("time",null, "Property", 1);
            time.add(currrentFrame);
            centerPointsSpikeIdea.add(time);

            centerPointsMO.setI(centerPointsSpikeIdea);
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
