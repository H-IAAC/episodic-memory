/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectsassociation;


import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.configuration.Configuration.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author luis_
 */
public class ObjectsAssociation {

    private static ObjectsAssociation instance = null;
    private static Queue<ArrayList<Idea>> associationQueue = new LinkedList<>();

    private ObjectsAssociation() {
    }

    public ArrayList<Idea> addObjects(ArrayList<Idea> objects) {

        ArrayList<Idea> relations = null;

        associationQueue.add(objects);

        if (associationQueue.size() >= 2) {

            //Remueve la primera lista T1
            ArrayList<Idea> previousList = associationQueue.remove();

            //Se crea la relacion de los objetos en T2
            relations = createRelations(objects, objects);

            //Se crea la relacion de los objetos de T1 con T2 
            relations.addAll(createRelations(objects, previousList));

        } else {
            //La primera vez solo asocia con si mismo y eso envia de regreso
            relations = createRelations(objects, objects);
        }

        return relations;
    }

    public ArrayList<Idea> createRelations(ArrayList<Idea> objects1, ArrayList<Idea> objects2) {

        ArrayList<Idea> relations = new ArrayList<>();

        for (Idea cObject1 : objects1) {
            for (Idea cObject2 : objects2) {
                Integer cObject1classIdea = (Integer) cObject1.get(ID_IDEA).getValue();
                Integer cObject2classIdea = (Integer) cObject2.get(ID_IDEA).getValue();
                
                Idea objectRelationIdea = new Idea(OBJECT_RELATION_IDEA, null,"Property", 1);
                Idea object1IdIdea = new Idea(OBJECT_1_ID_IDEA, cObject1classIdea,"Property", 1);
                Idea object2IdIdea = new Idea(OBJECT_2_ID_IDEA, cObject2classIdea,"Property", 1);
                Idea timeIdea = new Idea(TIME_IDEA, 0,"Property", 1);
                Idea activationIdea = new Idea(ACTIVATION_IDEA, 0.5,"Property", 1);
                Idea timestampIdea = new Idea(TIMESTAMP_IDEA, System.currentTimeMillis(),"Property", 1);
                Idea recentIdea = new Idea(RECENT_IDEA, true,"Property", 1);
                Idea updatedIdea = new Idea(UPDATED_IDEA, false,"Property", 1);
                Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, 0,"Property", 1);
                
                objectRelationIdea.add(object1IdIdea);
                objectRelationIdea.add(object2IdIdea);
                objectRelationIdea.add(timeIdea);
                objectRelationIdea.add(activationIdea);
                objectRelationIdea.add(timestampIdea);
                objectRelationIdea.add(recentIdea);
                objectRelationIdea.add(updatedIdea);
                objectRelationIdea.add(repetitionsIdea);
                
                if (!relations.contains(objectRelationIdea)) {
                    relations.add(objectRelationIdea);
                }
            }
        }

        return relations;

    }

    public static ObjectsAssociation getInstance() {

        if (instance == null) {
            instance = new ObjectsAssociation();
        }

        return instance;
    }
}
