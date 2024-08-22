/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage.mpfc;

import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.Scene;
import episodicv2.core.entities.SceneRelation;
import episodicv2.core.t2string.T2DString;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author luis_
 */
public class ScenesWMQueue {

    private int time;
    private int maxSize = 4;
    private LinkedList<Scene> items;
    private ConcurrentHashMap<Integer, ArrayList<SceneRelation>> relations;
    private ConcurrentHashMap<Integer, Scene> scenesByID;

    private static ScenesWMQueue instance = null;

    private ScenesWMQueue() {
        items = new LinkedList<>();
        relations = new ConcurrentHashMap<>();
        scenesByID = new ConcurrentHashMap<>();
    }

    private ScenesWMQueue(int maxSize) {
        this.maxSize = maxSize;
        items = new LinkedList<>();
        relations = new ConcurrentHashMap<>();
        scenesByID = new ConcurrentHashMap<>();
    }

    public static ScenesWMQueue getInstance() {

        if (instance == null) {
            instance = new ScenesWMQueue();
        }

        return instance;
    }

    public static ScenesWMQueue getInstance(int maxSize) {

        if (instance == null) {
            instance = new ScenesWMQueue(maxSize);
        }

        return instance;
    }

    public Scene getMostSimilarItem(String pattern) {

        Scene retrievedScene = null;
        
        for (Scene scene : items) {

            float similarity = T2DString.lcs2DString(scene.getPattern(), pattern, T2DString.SIMILARITY_TYPE_1);

            //SimpleLogger.log(this, "WM scene " + scene.getPattern()+", "+pattern+", "+similarity);
            //EL 0 EVITA que sea una current scene
            if (similarity >= Configuration.WM_RETRIEVAL_SIMILARITY_THRESHOLD && scene.getId() != 0) {

                scene.setRelations(getRelations().get(scene.getId()));

                retrievedScene = scene;
                //return scene;
            }
        }

        return retrievedScene;
    }

    /**
     * *
     * FALTA CREAR UNA CLASE WM ITEM PARA IR REFORZANDO LOS ITEMS USADOS
     */
    public void addItem(Scene scene, int itemTime) {

        //Solo son maximo 7 items o 4, no mames
        //Si quieres optimizarlo pues sobrecarga el contains del CObject
        boolean exists = false;
        for (Scene s : items) {

            float similarity = T2DString.lcs2DString(scene.getPattern(), s.getPattern(), T2DString.SIMILARITY_TYPE_1);

            if (similarity >= 1.0) {
                exists = true;
                break;
            }
        }

        if (!exists) {

            if (items.size() >= maxSize) { //Si es menor, si puede agregar
                removeRelations(items.peek().getId());
                scenesByID.remove(items.peek().getId());
                items.remove();

            }

            items.add(scene);
            scenesByID.put(scene.getId(), scene);
            this.addRelations(scene.getId(), new ArrayList<SceneRelation>());

        }

    }

    public void addRelations(int sceneId, ArrayList<SceneRelation> addRelations) {

        if (sceneId != 0) {
            getRelations().put(sceneId, addRelations);
        }
    }

    public void removeRelations(int sceneId) {
        if (sceneId != 0) {
            getRelations().remove(sceneId);
        }
    }

    public ArrayList<Scene> getItems() {
        return new ArrayList<>(items);
    }

    //Verifica si puede reemplazar a un item deacuerdo a relevancia(si esta siendo usado)
    public boolean canReplace(CObject item) {
        return true;
    }

    public Scene getScene(int sceneId) {

        Scene returnedScene = scenesByID.get(sceneId);
        
        if(returnedScene != null){
            returnedScene.setRelations(getRelations().get(sceneId));
        }
        
        return returnedScene;

    }

    public ConcurrentHashMap<Integer, ArrayList<SceneRelation>> getRelations() {
        return relations;
    }

    public void setRelations(ConcurrentHashMap<Integer, ArrayList<SceneRelation>> relations) {
        this.relations = relations;
    }

}
