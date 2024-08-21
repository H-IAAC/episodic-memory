/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.Codelets.storage.vlpfc;

import episodicv2.core.entities.CObject;
import episodicv2.core.entities.ObjectRelations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author luis_
 */
public class RelationsWMQueue {

    private int time;
    private int maxSize = 4;
    private LinkedList<ObjectRelations> relations;
    private ConcurrentHashMap<Integer, ObjectRelations> relationsMap;

    public RelationsWMQueue() {
        relations = new LinkedList<>();
        relationsMap = new ConcurrentHashMap<>();
    }

    public RelationsWMQueue(int maxSize) {
        this.maxSize = maxSize;
        relations = new LinkedList<>();
        relationsMap = new ConcurrentHashMap<>();
    }

    /**
     * *
     * FALTA CREAR UNA CLASE WM ITEM PARA IR REFORZANDO LOS ITEMS USADOS
     */
    public void addItem(ObjectRelations item, int itemTime) {

        //Solo son maximo 7 relations o 4, no mames
        //Si quieres optimizarlo pues sobrecarga el contains del CObject
        boolean exists = false;
        for (ObjectRelations or : relations) {
            if (or.getObjectId() == item.getObjectId()) {
                exists = true;
                break;
            }
        }
        
        if (!exists) {

            if (relations.size() >= maxSize) { //Si es menor, si puede agregar
                
                relationsMap.remove(relations.peek().getObjectId());
                relations.remove();
                
            }

            relations.add(item);
            relationsMap.put(item.getObjectId(), item);
        }

    }

    public ObjectRelations getRelatedItems(int searchedId) {

        ObjectRelations result = null;
                
        for (ObjectRelations or : relations) {
            if (or.getObjectId() == searchedId) {
                result = or;
            }
        }
        
        return result;
    }

    public ArrayList<ObjectRelations> getItems() {
        return new ArrayList<>(relations);
    }
    
    public ConcurrentHashMap<Integer,ObjectRelations> getObjectRelations(){
        return relationsMap;
    }

    //Verifica si puede reemplazar a un item deacuerdo a relevancia(si esta siendo usado)
    public boolean canReplace(CObject item) {
        return true;
    }

}
