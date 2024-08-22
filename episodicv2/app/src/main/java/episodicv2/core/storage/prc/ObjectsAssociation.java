/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage.prc;

import episodicv2.core.entities.CObject;
import episodicv2.core.entities.ObjectRelation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author luis_
 */
public class ObjectsAssociation {

    private static ObjectsAssociation instance = null;
    private static Queue<ArrayList<CObject>> associationQueue = new LinkedList<>();

    private ObjectsAssociation() {
    }

    public ArrayList<ObjectRelation> addObjects(ArrayList<CObject> objects) {

        ArrayList<ObjectRelation> relations = null;

        associationQueue.add(objects);

        if (associationQueue.size() >= 2) {

            //Remueve la primera lista T1
            ArrayList<CObject> previousList = associationQueue.remove();

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

    public ArrayList<ObjectRelation> createRelations(ArrayList<CObject> objects1, ArrayList<CObject> objects2) {

        ArrayList<ObjectRelation> relations = new ArrayList<>();

        for (CObject cObject1 : objects1) {
            for (CObject cObject2 : objects2) {
                ObjectRelation objectRelation = new ObjectRelation(cObject1.getClassId(), cObject2.getClassId(), 0);
                if (!relations.contains(objectRelation)) {
                    relations.add(objectRelation);
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
