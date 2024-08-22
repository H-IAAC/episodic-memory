/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage.prc;

import episodicv2.core.ConsolidationNodeHandler;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.storage.ActivationFunctions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author luis_
 */
public class PRCAffectiveStorageHandler {

    private static final String STORAGE_PATH = Configuration.STORAGE_BASE_PATH + "/prc/";
    private static final String FILENAME = Configuration.PRC_STORAGE_FILENAME;
    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;
    private static final double RETRIEVAL_SIMILARITY_THRESHOLD = Configuration.RETRIEVAL_SIMILARITY_THRESHOLD;

    public static final int BOTTOM_UP_RETRIEVAL = 1;
    public static final int TOP_DOWN_RETRIEVAL = 2;

    private static PRCAffectiveStorageHandler instance = null;

    private Timer consolidationTimer;

    //CARGA LAS ESCENAS EXISTENTES
    private static Map<Integer, ObjectRelations> midTermMemoryObjectsByID;

    private static ConsolidationNodeHandler consolidationService;

    private PRCAffectiveStorageHandler() {
        midTermMemoryObjectsByID = new ConcurrentHashMap<>();

        consolidationService = new ConsolidationNodeHandler();

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();

    }

    private PRCAffectiveStorageHandler(boolean load) {
        midTermMemoryObjectsByID = new ConcurrentHashMap<>();
        consolidationService = new ConsolidationNodeHandler();

        if (load) {
            load();
        }

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();
    }

    public void startConsolidation() {

        long delay = Configuration.CONSOLIDATION_INTERVAL;

        consolidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                persistScenes();
            }
        }, delay, delay);
    }

    /**
     * Retrieve the scenes
     *
     * @return
     */
    public ArrayList<ObjectRelations> getObjects() {
        return (ArrayList<ObjectRelations>) midTermMemoryObjectsByID.values();
    }

    /**
     * *
     * LOAD THE PATTERNS ON MEMORY
     */
    public void load() {
        try {

            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists() && midTermMemoryObjectsByID.isEmpty()) {
                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME));

                String line;

                while ((line = br.readLine()) != null) {
                    ObjectRelations object = new ObjectRelations(line);

                    //By ID
                    midTermMemoryObjectsByID.put(object.getObjectId(), object);
                }
            } else {
                System.out.println("Archivo no existe, primera ejecucion LOAD");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param object
     */
    public void storeObject(ObjectRelations object) {

        if (!midTermMemoryObjectsByID.containsKey(object.getObjectId())) {

            midTermMemoryObjectsByID.put(object.getObjectId(), object);

        } else {

            ObjectRelations existingObject = midTermMemoryObjectsByID.get(object.getObjectId());

            //existingObject.updateActivationWithAffect(object.getAffect());
            existingObject.updateActivationWithAffect(ActivationFunctions.affect(object.getPositiveAffect(), object.getNegativeAffect()));
            existingObject.updatePositiveAffect(object.getPositiveAffect());
            existingObject.updateNegativeAffect(object.getNegativeAffect());
            //existingObject.updateAffect(object.getAffect());
            existingObject.setTimeStamp(object.getTimeStamp());
        }

    }

    /**
     * STORES ALL THE TEMPORAL PATTERNS
     */
    public void persistScenes() {
        try {

            File path = new File(STORAGE_PATH);

            //VERIFICA QUE EXISTA LA CARPETA DE ALMACENAMIENTO, SINO LA CREA
            if (!path.exists()) {
                path.mkdirs();
            }

            File previousKnowledge = new File(STORAGE_PATH + FILENAME);

            if (!previousKnowledge.exists()) {
                previousKnowledge.createNewFile();
            }

            BufferedReader previousKnowledgeReader = new BufferedReader(new FileReader(previousKnowledge));

            File updatedKnowledge = new File(STORAGE_PATH + "prc_objects_temp.txt");
            PrintWriter updatedKnowledgeWriter = new PrintWriter(new FileWriter(updatedKnowledge));

            HashMap<Integer, ObjectRelations> objectsToUpdate = new HashMap<>();
            objectsToUpdate.putAll(midTermMemoryObjectsByID);

            //OBJECTS SORTED BY AFFECTIVE VALUE
            HashMap<Integer, ObjectRelations> objectsSortedByAffect = new HashMap<>();

            //
            String objectLineString = null;

            while ((objectLineString = previousKnowledgeReader.readLine()) != null) {

                ObjectRelations object = new ObjectRelations(objectLineString);

                if (objectsToUpdate.containsKey(object.getObjectId())) {

                    //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER ACTUALIZADO ANTES DE GUARDAR
                    object = objectsToUpdate.get(object.getObjectId());

                    objectsToUpdate.remove(object.getObjectId());
                }

                updatedKnowledgeWriter.println(object.toString());
                updatedKnowledgeWriter.flush();

                //OBJECTS SORTED BY AFFECTIVE VALUE
                objectsSortedByAffect.put(object.getObjectId(), object);
            }

            SortedSet<Integer> objectIDs = new TreeSet<>(objectsToUpdate.keySet());

            for (Integer objectID : objectIDs) {

                ObjectRelations object = objectsToUpdate.get(objectID);

                //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER GUARDADO
                updatedKnowledgeWriter.println(object.toString());
                updatedKnowledgeWriter.flush();

                //OBJECTS SORTED BY AFFECTIVE VALUE
                objectsSortedByAffect.put(object.getObjectId(), object);
            }

            updatedKnowledgeWriter.close();
            previousKnowledgeReader.close();

            if (previousKnowledge.delete()) {

                if (!updatedKnowledge.renameTo(previousKnowledge)) {
                    System.out.println("Error on updating file");
                }

            } else {
                System.out.println("Error on updating file");
            }

            //WRITE THE OBJECTS SORTED BY POSITIVE AFFECT
            File affectIndexedObjects = new File(STORAGE_PATH + "prc_objects_positive_affect.txt");
            PrintWriter affectIndexedObjectsWriter = new PrintWriter(new FileWriter(affectIndexedObjects));

            HashMap<Integer, ObjectRelations> sortedAHM = sortByPositiveAffect(objectsSortedByAffect, true);

            for (Map.Entry<Integer, ObjectRelations> aSO : sortedAHM.entrySet()) {
                affectIndexedObjectsWriter.println(aSO.getValue().toString());
                affectIndexedObjectsWriter.flush();
            }

            affectIndexedObjectsWriter.close();

            //WRITE THE OBJECTS SORTED BY POSITIVE AFFECT
            File nAffectIndexedObjects = new File(STORAGE_PATH + "prc_objects_negative_affect.txt");
            PrintWriter nAffectIndexedObjectsWriter = new PrintWriter(new FileWriter(nAffectIndexedObjects));

            HashMap<Integer, ObjectRelations> sortedNAHM = sortByNegativeAffect(objectsSortedByAffect, true);

            for (Map.Entry<Integer, ObjectRelations> aSO : sortedNAHM.entrySet()) {
                nAffectIndexedObjectsWriter.println(aSO.getValue().toString());
                nAffectIndexedObjectsWriter.flush();
            }

            nAffectIndexedObjectsWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<Integer, ObjectRelations> sortByPositiveAffect(Map<Integer, ObjectRelations> hm, boolean desc) {
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, ObjectRelations>> list
                = new LinkedList<>(hm.entrySet());

        if (desc) {

            Collections.sort(list, new Comparator<Map.Entry<Integer, ObjectRelations>>() {
                public int compare(Map.Entry<Integer, ObjectRelations> o1,
                        Map.Entry<Integer, ObjectRelations> o2) {
                    return Double.compare(o2.getValue().getPositiveAffect(), o1.getValue().getPositiveAffect());
                }
            });

        } else {

            Collections.sort(list, new Comparator<Map.Entry<Integer, ObjectRelations>>() {
                public int compare(Map.Entry<Integer, ObjectRelations> o1,
                        Map.Entry<Integer, ObjectRelations> o2) {
                    return Double.compare(o1.getValue().getPositiveAffect(), o2.getValue().getPositiveAffect());
                }
            });

        }

        // put data from sorted list to hashmap  
        HashMap<Integer, ObjectRelations> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, ObjectRelations> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static HashMap<Integer, ObjectRelations> sortByNegativeAffect(Map<Integer, ObjectRelations> hm, boolean desc) {
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, ObjectRelations>> list
                = new LinkedList<>(hm.entrySet());

        if (desc) {

            Collections.sort(list, new Comparator<Map.Entry<Integer, ObjectRelations>>() {
                public int compare(Map.Entry<Integer, ObjectRelations> o1,
                        Map.Entry<Integer, ObjectRelations> o2) {
                    return Double.compare(o2.getValue().getNegativeAffect(), o1.getValue().getNegativeAffect());
                }
            });

        } else {

            Collections.sort(list, new Comparator<Map.Entry<Integer, ObjectRelations>>() {
                public int compare(Map.Entry<Integer, ObjectRelations> o1,
                        Map.Entry<Integer, ObjectRelations> o2) {
                    return Double.compare(o1.getValue().getNegativeAffect(), o2.getValue().getNegativeAffect());
                }
            });

        }

        // put data from sorted list to hashmap  
        HashMap<Integer, ObjectRelations> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, ObjectRelations> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     *
     * @param objectId
     * @param affectIntensity
     * @param timeStamp
     * @param updateAffect
     * @return
     */
    public ObjectRelations retrieveObjectByID(int objectId, double affectIntensity, long timeStamp, boolean updateAffect) {

        ObjectRelations retrievedObject = midTermMemoryObjectsByID.get(objectId);

        if (retrievedObject != null) {

            double elapsedTime = (timeStamp - retrievedObject.getTimeStamp());

            elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

            double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, retrievedObject.getActivation());

            if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                if (updateAffect) {
                    retrievedObject.updateActivationWithAffect(affectIntensity);
                } else {
                    retrievedObject.updateActivation();
                }

                retrievedObject.setTimeStamp(timeStamp);

            } else {
                retrievedObject = null;
            }

        } else {

            retrievedObject = retrieveObjectByIDLTM(objectId, affectIntensity, timeStamp, updateAffect);

        }

        //AQUI FALTA EVALUAR LA RECUPERABILIDAD, YA QUE APESAR DE QUE EXISTA PUEDE SER NO RECUPERADO
        return retrievedObject;
    }

    /**
     *
     * @param objectId
     * @param affectIntensity
     * @param timeStamp
     * @return
     */
    public ObjectRelations retrieveObjectByIDLTM(int objectId, double affectIntensity, long timeStamp, boolean updateAffect) {

        ObjectRelations retrievedObject = null;

        try {

            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME));

                String line;

                while ((line = br.readLine()) != null) {

                    ObjectRelations object = new ObjectRelations(line);

                    if (object.getObjectId() == objectId) {
                        retrievedObject = object;
                        break;
                    }

                }

                //RETRIEVABILITY
                if (retrievedObject != null) {

                    double elapsedTime = (timeStamp - retrievedObject.getTimeStamp());

                    elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                    double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, retrievedObject.getActivation());

                    if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                        if (updateAffect) {
                            retrievedObject.updateActivationWithAffect(affectIntensity);
                        } else {
                            retrievedObject.updateActivation();
                        }

                        retrievedObject.setTimeStamp(timeStamp);

                        midTermMemoryObjectsByID.put(retrievedObject.getObjectId(), retrievedObject);

                    } else {

                        /**
                         * SE REQUIERE VOLVER A VER EL ESTIMULO FISICAMENTE
                         */
                        retrievedObject = null;

                    }

                }

                return retrievedObject;

            } else {
                System.out.println("Archivo no existe, primera ejecucion");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retrievedObject;
    }

    /**
     * *
     * AFFECTIVE RETRIEVAL
     *
     * @return
     */
    public ObjectRelations retrieveMostPositiveAffectiveObject() {

        ObjectRelations objectInMTM = null;

        if (!midTermMemoryObjectsByID.isEmpty()) {

            HashMap<Integer, ObjectRelations> sortedAHM = sortByPositiveAffect(midTermMemoryObjectsByID, true);

            objectInMTM = sortedAHM.entrySet().iterator().next().getValue();
        }

        ObjectRelations objectInLTM = retrieveObjectByPositiveAffectLTM(1);

        ObjectRelations mostAffective = new ObjectRelations(0);

        if (objectInMTM != null) {
            if (objectInLTM != null) {

                if (objectInMTM.getPositiveAffect()> objectInLTM.getPositiveAffect()) {
                    mostAffective = objectInMTM;
                } else {
                    mostAffective = objectInLTM;
                }

            } else {
                mostAffective = objectInMTM;
            }
        } else {
            if (objectInLTM != null) {
                mostAffective = objectInLTM;
            }
        }

        return mostAffective;
    }

    public ObjectRelations retrieveLessPositiveAffectiveObject() {

        ObjectRelations objectInMTM = null;

        if (!midTermMemoryObjectsByID.isEmpty()) {

            HashMap<Integer, ObjectRelations> sortedAHM = sortByPositiveAffect(midTermMemoryObjectsByID, true);

            objectInMTM = sortedAHM.entrySet().iterator().next().getValue();
        }

        ObjectRelations objectInLTM = retrieveObjectByPositiveAffectLTM(-1);

        ObjectRelations lessAffective = new ObjectRelations(0);

        if (objectInMTM != null) {
            if (objectInLTM != null) {

                if (objectInMTM.getPositiveAffect()< objectInLTM.getPositiveAffect()) {
                    lessAffective = objectInMTM;
                } else {
                    lessAffective = objectInLTM;
                }

            } else {
                lessAffective = objectInMTM;
            }
        } else {
            if (objectInLTM != null) {
                lessAffective = objectInLTM;
            }
        }

        return lessAffective;
    }

    private ObjectRelations retrieveObjectByPositiveAffectLTM(int lineNumber) {
        try {

            File file = new File(STORAGE_PATH + "prc_objects_positive_affect.txt");

            if (file.exists()) {

                ObjectRelations object = null;

                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + "prc_objects_positive_affect.txt"));

                int count = 0;

                String line;

                while ((line = br.readLine()) != null) {

                    object = new ObjectRelations(line);

                    count++;

                    if (count == lineNumber) {
                        break;
                    }

                }

                return object;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * CONSTRUCTORS
     */
    public static PRCAffectiveStorageHandler getInstance() {
        if (instance == null) {
            instance = new PRCAffectiveStorageHandler();
        }
        return instance;
    }

    public static PRCAffectiveStorageHandler getInstance(boolean load) {
        if (instance == null) {
            instance = new PRCAffectiveStorageHandler(load);
        }
        return instance;
    }
}
