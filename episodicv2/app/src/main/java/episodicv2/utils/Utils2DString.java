/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.utils;

import episodicv2.core.entities.CObject;
import episodicv2.core.entities.CObjectList;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Luis
 */
public class Utils2DString {

    public static CObjectList getRecognizedObjectsList(String sceneData) {

        CObjectList objectList = new CObjectList();

        try {

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(sceneData);

            long time = (Long) jsonObject.get("time");

            JSONArray msg = (JSONArray) jsonObject.get("objects");

            if (msg.isEmpty()) {
                return null;
            }

            Iterator<JSONObject> iterator = msg.iterator();

            ArrayList<CObject> sceneObjects = new ArrayList<>();

            while (iterator.hasNext()) {

                JSONObject sceneObject = iterator.next();

                long preId = (Long) sceneObject.get("pid");
                long classId = (Long) sceneObject.get("id");
                String label = (String) sceneObject.get("class");
                String features = (String) sceneObject.get("features");

                sceneObjects.add(new CObject((int) preId, (int) classId, 0, 0, label, features, (int) time));

            }

            objectList.setObjects(sceneObjects);
            objectList.setTime((int) time);

            return objectList;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public static ArrayList<CObject> getRecognizedObjects(String sceneData) {
        try {

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(sceneData);

            long time = (Long) jsonObject.get("time");

            JSONArray msg = (JSONArray) jsonObject.get("objects");

            if (msg.isEmpty()) {
                return null;
            }

            Iterator<JSONObject> iterator = msg.iterator();

            ArrayList<CObject> sceneObjects = new ArrayList<>();

            while (iterator.hasNext()) {

                JSONObject sceneObject = iterator.next();

                long preId = (Long) sceneObject.get("pid");
                long classId = (Long) sceneObject.get("id");
                String label = (String) sceneObject.get("class");
                String features = (String) sceneObject.get("features");

                sceneObjects.add(new CObject((int) preId, (int) classId, 0, 0, label, features, (int) time));

            }

            return sceneObjects;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public static CObjectList getObjectsList(String sceneData) {

        CObjectList objectList = new CObjectList();
        
        try {

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(sceneData);

            long time = (Long) jsonObject.get("time");

            JSONArray msg = (JSONArray) jsonObject.get("objects");

            if (msg.isEmpty()) {
                return null;
            }

            Iterator<JSONObject> iterator = msg.iterator();

            ArrayList<CObject> sceneObjects = new ArrayList<>();

            while (iterator.hasNext()) {

                JSONObject sceneObject = iterator.next();

                long preId = (Long) sceneObject.get("pid");
                double x = (double) sceneObject.get("x");
                double y = (double) sceneObject.get("y");

                sceneObjects.add(new CObject((int) preId, 0, (int) x, (int) y, "", "", (int) time));

            }
            
            objectList.setObjects(sceneObjects);
            objectList.setTime((int) time);

            return objectList;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

}
