/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage;

import episodicv2.core.ConsolidationNodeHandler;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.Scene;
import episodicv2.core.t2string.T2DString;
import episodicv2.utils.SimpleLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CA3StorageHandler {

    private static final String STORAGE_PATH = Configuration.STORAGE_BASE_PATH + "/ca3/";
    private static final String FILENAME = Configuration.CA3_STORAGE_FILENAME;
    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;
    private static final double RETRIEVAL_SIMILARITY_THRESHOLD = Configuration.RETRIEVAL_SIMILARITY_THRESHOLD;

    public static final int BOTTOM_UP_RETRIEVAL = 1;
    public static final int TOP_DOWN_RETRIEVAL = 2;

    private static CA3StorageHandler instance = null;

    private Timer consolidationTimer;

    //CARGA LAS ESCENAS EXISTENTES
    private static Map<Integer, Scene> midTermMemoryScenesByID;

    private static ConsolidationNodeHandler consolidationService;

    private CA3StorageHandler() {
        midTermMemoryScenesByID = new ConcurrentHashMap<>();

        consolidationService = new ConsolidationNodeHandler();

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();

    }

    private CA3StorageHandler(boolean load) {
        midTermMemoryScenesByID = new ConcurrentHashMap<>();
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
                SimpleLogger.log(this, "Storing...");
                persistScenes();
            }
        }, delay, delay);
    }

    /**
     * Retrieve the scenes
     *
     * @return
     */
    public ArrayList<Scene> getScenes() {
        return (ArrayList<Scene>) midTermMemoryScenesByID.values();
    }

    /**
     * *
     * LOAD THE PATTERNS ON MEMORY
     */
    public void load() {
        try {

            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists() && midTermMemoryScenesByID.isEmpty()) {
                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME));

                String line;

                while ((line = br.readLine()) != null) {
                    Scene scene = new Scene(line);

                    //By ID
                    midTermMemoryScenesByID.put(scene.getId(), scene);
                }
            } else {
                SimpleLogger.log(this, "Archivo no existe, primera ejecucion LOAD");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * *
     * STORE SCENE, IT DOES NOT REQUIRE VALIDATION OF SIMILARITY
     *
     * @param scene
     * @param positiveAffect
     * @param negativeAffect
     * @param affectIntensity
     */
    public void storeScene(Scene scene, double positiveAffect, double negativeAffect, double affectIntensity) {

        if (!midTermMemoryScenesByID.containsKey(scene.getId())) {

            midTermMemoryScenesByID.put(scene.getId(), scene);

        } else {

            Scene existingScene = midTermMemoryScenesByID.get(scene.getId());

            //existingScene.updateAffect(affect);
            //existingScene.updateActivationWithAffect(scene.getAffect());
            existingScene.updatePositiveAffect(positiveAffect);
            existingScene.updateNegativeAffect(negativeAffect);
            existingScene.updateActivationWithAffect(affectIntensity);
            existingScene.setTimeStamp(scene.getTimeStamp());

            //SimpleLogger.log(this, "Updating "+scene.getPattern()+" , oldpa "+positiveAffect+" oldna "+negativeAffect+" -- new "+existingScene.getPositiveAffect()+" , "+existingScene.getNegativeAffect());
        }

    }

    /**
     * STORES ALL THE TEMPORAL PATTERNS
     */
    public void persistScenes() {
        try {

            //VERIFICA QUE EXISTA LA CARPETA DE ALMACENAMIENTO, SINO LA CREA
            createStoragePaths();

            File previousKnowledge = new File(STORAGE_PATH + FILENAME);

            if (!previousKnowledge.exists()) {
                previousKnowledge.createNewFile();
            }

            BufferedReader previousKnowledgeReader = new BufferedReader(new FileReader(previousKnowledge));

            File updatedKnowledge = new File(STORAGE_PATH + "ca3_temp.txt");
            PrintWriter updatedKnowledgeWriter = new PrintWriter(new FileWriter(updatedKnowledge));

            HashMap<Integer, Scene> scenesToUpdate = new HashMap<>();
            scenesToUpdate.putAll(midTermMemoryScenesByID);

            //OBJECTS SORTED BY AFFECTIVE VALUE
            HashMap<Integer, Scene> scenesSortedByAffect = new HashMap<>();

            //
            String sceneLineString = null;

            while ((sceneLineString = previousKnowledgeReader.readLine()) != null) {

                Scene scene = new Scene(sceneLineString);

                if (scenesToUpdate.containsKey(scene.getId())) {

                    //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER ACTUALIZADO ANTES DE GUARDAR
                    scene = scenesToUpdate.get(scene.getId());

                    scenesToUpdate.remove(scene.getId());
                }

                updatedKnowledgeWriter.println(scene.toString());
                updatedKnowledgeWriter.flush();

                //OBJECTS SORTED BY AFFECTIVE VALUE
                scenesSortedByAffect.put(scene.getId(), scene);
            }

            SortedSet<Integer> sceneIDs = new TreeSet<>(scenesToUpdate.keySet());

            for (Integer sceneID : sceneIDs) {

                Scene scene = scenesToUpdate.get(sceneID);

                if (scene.getActivation() >= Configuration.CONSOLIDATION_THRESHOLD) {
                    //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER GUARDADO
                    updatedKnowledgeWriter.println(scene.toString());
                    updatedKnowledgeWriter.flush();

                    //OBJECTS SORTED BY AFFECTIVE VALUE
                    scenesSortedByAffect.put(scene.getId(), scene);
                }

            }

            updatedKnowledgeWriter.close();
            previousKnowledgeReader.close();

            if (previousKnowledge.delete()) {

                if (!updatedKnowledge.renameTo(previousKnowledge)) {
                    SimpleLogger.log(this, "Error on renaming file");
                }

            } else {
                SimpleLogger.log(this, "Error on deleting file");
            }

            //CREA LOS CLUSTER DE ESCENAS
            createClusters();

            //WRITE THE OBJECTS SORTED BY AFFECT
            File affectIndexedObjects = new File(STORAGE_PATH + Configuration.CA3_STORAGE_POS_AFFECT);
            PrintWriter affectIndexedObjectsWriter = new PrintWriter(new FileWriter(affectIndexedObjects));

            HashMap<Integer, Scene> sortedAHM = sortByPositiveAffect(scenesSortedByAffect, true);

            for (Map.Entry<Integer, Scene> aSS : sortedAHM.entrySet()) {
                affectIndexedObjectsWriter.println(aSS.getValue().toString());
                affectIndexedObjectsWriter.flush();
            }

            affectIndexedObjectsWriter.close();

            //WRITE THE OBJECTS SORTED BY AFFECT
            File nAffectIndexedObjects = new File(STORAGE_PATH + Configuration.CA3_STORAGE_NEG_AFFECT);
            PrintWriter nAffectIndexedObjectsWriter = new PrintWriter(new FileWriter(nAffectIndexedObjects));

            HashMap<Integer, Scene> sortedNAHM = sortByNegativeAffect(scenesSortedByAffect, true);

            for (Map.Entry<Integer, Scene> aSS : sortedNAHM.entrySet()) {
                nAffectIndexedObjectsWriter.println(aSS.getValue().toString());
                nAffectIndexedObjectsWriter.flush();
            }

            nAffectIndexedObjectsWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createClusters() {

        ConsolidationNodeHandler.createClusters();

    }

    public static HashMap<Integer, Scene> sortByPositiveAffect(Map<Integer, Scene> hm, boolean desc) {
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, Scene>> list
                = new LinkedList<>(hm.entrySet());

        if (desc) {
            Collections.sort(list, new Comparator<Map.Entry<Integer, Scene>>() {
                public int compare(Map.Entry<Integer, Scene> o1,
                        Map.Entry<Integer, Scene> o2) {
                    return Double.compare(o2.getValue().getPositiveAffect(), o1.getValue().getPositiveAffect());
                }
            });
        } else {
            Collections.sort(list, new Comparator<Map.Entry<Integer, Scene>>() {
                public int compare(Map.Entry<Integer, Scene> o1,
                        Map.Entry<Integer, Scene> o2) {
                    return Double.compare(o1.getValue().getPositiveAffect(), o2.getValue().getPositiveAffect());
                }
            });
        }
        // put data from sorted list to hashmap  
        HashMap<Integer, Scene> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, Scene> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static HashMap<Integer, Scene> sortByNegativeAffect(Map<Integer, Scene> hm, boolean desc) {
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, Scene>> list
                = new LinkedList<>(hm.entrySet());

        if (desc) {
            Collections.sort(list, new Comparator<Map.Entry<Integer, Scene>>() {
                public int compare(Map.Entry<Integer, Scene> o1,
                        Map.Entry<Integer, Scene> o2) {
                    return Double.compare(o2.getValue().getNegativeAffect(), o1.getValue().getNegativeAffect());
                }
            });
        } else {
            Collections.sort(list, new Comparator<Map.Entry<Integer, Scene>>() {
                public int compare(Map.Entry<Integer, Scene> o1,
                        Map.Entry<Integer, Scene> o2) {
                    return Double.compare(o1.getValue().getNegativeAffect(), o2.getValue().getNegativeAffect());
                }
            });
        }
        // put data from sorted list to hashmap  
        HashMap<Integer, Scene> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, Scene> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     * CHECKS IF EXISTS A 2D STRING SIMILAR INTO THE STORED ONES
     *
     * @param pattern
     * @return
     */
    public String getSimilar(String pattern) {
        String similar = null;

        Set<Entry<Integer, Scene>> entrySet = midTermMemoryScenesByID.entrySet();

        Iterator<Entry<Integer, Scene>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {

            Entry<Integer, Scene> sceneEntry = iterator.next();
            Scene scene = sceneEntry.getValue();
            String patternToCompare = scene.getPattern();

            float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

            //SimpleLogger.log("Similarity: " + similarity);
            if (similarity >= SIMILARITY_THRESHOLD) {
                return patternToCompare;
            }

        }

        return similar;
    }

    /**
     * *
     * RECUPERA LAS ESCENAS A TRAVES DE UN QUERY BOTTOM UP
     *
     * @param pattern
     * @param positiveAffect
     * @param negativeAffect
     * @param affectIntensity
     * @param time
     * @param timeStamp
     * @return
     */
    public ArrayList<Scene> retrieveSimilarScenesBottomUp(String pattern, double positiveAffect, double negativeAffect, double affectIntensity, int time, long timeStamp) {
        return retrieveSimilarScenesMTM(pattern, positiveAffect, negativeAffect, affectIntensity, time, timeStamp, BOTTOM_UP_RETRIEVAL, false);
    }

    /**
     * *
     * RECUPERA LAS ESCENAS A TRAVES DE UN QUERY TOP DOWN
     *
     * @param pattern
     * @param positiveAffect
     * @param negativeAffect
     * @param affectIntensity
     * @param time
     * @param updateAffect
     * @param timeStamp
     * @return
     */
    public ArrayList<Scene> retrieveSimilarScenesTopDown(String pattern, double positiveAffect, double negativeAffect, double affectIntensity, int time, long timeStamp, boolean updateAffect) {
        return retrieveSimilarScenesMTM(pattern, positiveAffect, negativeAffect, affectIntensity, time, timeStamp, TOP_DOWN_RETRIEVAL, updateAffect);
    }

    /**
     * RECUPERA LAS ESCENAS SIMILARES A LA SOLICIDATADA EN MID-TERM MEMORY
     *
     * @param pattern
     * @param time
     * @param timeStamp
     * @return
     */
    private ArrayList<Scene> retrieveSimilarScenesMTM(String pattern, double positiveAffect, double negativeAffect, double affectIntensity, int time, long timeStamp, int retrievalType, boolean updateAffect) {

        ArrayList<Scene> similarScenes = new ArrayList<>();
        HashMap<Integer, Scene> scenesToUpdate = new HashMap<>();

        Set<Entry<Integer, Scene>> entrySet = midTermMemoryScenesByID.entrySet();

        Iterator<Entry<Integer, Scene>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {

            Entry<Integer, Scene> sceneEntry = iterator.next();
            Scene scene = sceneEntry.getValue();
            String patternToCompare = scene.getPattern();

            //CALCULA LA SIMILARIDAD ENTRE LA ESCENA ACTUAL Y LA RECUPERADA
            float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

            scene.setActiveSimilarity(similarity);

            //EVALUA QUE LA SIMILARIDAD SUPERE EL UMBRAL ESTABLECIDO
            if (similarity >= RETRIEVAL_SIMILARITY_THRESHOLD) {

                //CALCULA EL TIEMPO QUE HA PASADO DESDE QUE SE VIO POR ULTIMA VEZ LA ESCENA
                double elapsedTime = (timeStamp - scene.getTimeStamp());

                elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                //SE CALCULA EL NIVEL DE RECUPERABILIDAD
                double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, scene.getActivation());

                //EVALUA LA RECUPERABILIDAD
                if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                    //SI LO PUEDE RECUPERAR, REACTIVA LA MEMORIA SIN IMPORTAR SI ES TOP DOWN O BOTTOM UP
                    if (updateAffect) {
                        scene.updateActivationWithAffect(affectIntensity);
                        scene.updatePositiveAffect(positiveAffect);
                        scene.updateNegativeAffect(negativeAffect);
                        //scene.updateAffect(affect);

                    } else {
                        scene.updateActivation();
                    }

                    scene.setTimeStamp(timeStamp);

                    similarScenes.add(scene);

                    scenesToUpdate.put(scene.getId(), scene);

                    //SI NO LO PUEDE RECUPERAR, LA MEMORIA SE REACTIVA SOLO SI VUELVE A VER EL MISMO ESTIMULO
                } else if (similarity >= 1.0 && retrievalType == BOTTOM_UP_RETRIEVAL) {

                    if (updateAffect) {
                        scene.updateActivationWithAffect(affectIntensity);
                        scene.updatePositiveAffect(positiveAffect);
                        scene.updateNegativeAffect(negativeAffect);
                        //scene.updateAffect(affect);
                    } else {
                        scene.updateActivation();
                    }

                    scene.setTimeStamp(timeStamp);

                    similarScenes.add(scene);

                    scenesToUpdate.put(scene.getId(), scene);
                }

            }

        }

        //SI NO RECUPERO NADA DE MID-TERM, BUSCA EN LONG-TERM
        if (similarScenes.isEmpty()) {

            similarScenes = retrieveSimilarScenesLTM(pattern, positiveAffect, negativeAffect, affectIntensity, time, timeStamp, retrievalType, updateAffect);

        }

        //Sort the memories by the similarity
        Collections.sort(similarScenes);

        //reinforceMemories(scenesToUpdate);
        return similarScenes;

    }

    public void createStoragePaths() {

        File path = new File(STORAGE_PATH);

        if (!path.exists()) {
            path.mkdirs();
        }
    }

    /**
     * RECUPERA LAS ESCENAS SIMILARES A LA SOLICIDATADA EN LA MEMORIA PERSISTIDA
     *
     * @param pattern
     * @param positiveAffect
     * @param negativeAffect
     * @param affectIntensity
     * @param time
     * @param timeStamp
     * @param retrievalType
     * @param updateAffect
     * @return
     */
    public ArrayList<Scene> retrieveSimilarScenesLTM(String pattern, double positiveAffect, double negativeAffect, double affectIntensity, int time, long timeStamp, int retrievalType, boolean updateAffect) {

        //PREDICE A QUE CLUSTER PERTENECE LA ESCENA PARA ACOTAR LA BUSQUEDA
        int cluster = consolidationService.getClusterSynchronous(pattern);

        ArrayList<Scene> similarScenes = new ArrayList<>();

        try {

            //SimpleLogger.log(this, "Searching in cluster: " + cluster);
            //SI HUBO UN FALLO EN LA PREDUCCION BUSCA EN EL ARCHIVO GENERAL
            String clusterFile = STORAGE_PATH + FILENAME;

            if (cluster != -1) {

                clusterFile = STORAGE_PATH + "clusters/cluster" + cluster + ".txt";

                //SI SE LOGRO LA PREDICCION PERO EL ARCHIVO DE CLUSTER NO EXISTE SE UTILIZA EL GENERAL
                if (!new File(clusterFile).exists()) {

                    SimpleLogger.log(this, "The prediction model exists, but the cluster file was deleted: " + cluster);

                    clusterFile = STORAGE_PATH + FILENAME;

                }

            }

            File file = new File(clusterFile);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(clusterFile));

                String line;

                while ((line = br.readLine()) != null) {

                    Scene scene = new Scene(line);
                    String patternToCompare = scene.getPattern();

                    //CALCULA LA SIMILARIDAD ENTRE LA ESCENA ACTUAL Y LA RECUPERADA
                    float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

                    //EVALUA QUE LA SIMILARIDAD SUPERE EL UMBRAL ESTABLECIDO
                    if (similarity >= RETRIEVAL_SIMILARITY_THRESHOLD) {

                        //CALCULA EL TIEMPO QUE HA PASADO DESDE QUE SE VIO POR ULTIMA VEZ LA ESCENA
                        double elapsedTime = (timeStamp - scene.getTimeStamp());

                        elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                        //SE CALCULA EL NIVEL DE RECUPERABILIDAD
                        double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, scene.getActivation());

                        //EVALUA LA RECUPERABILIDAD
                        if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                            //SI LO PUEDE RECUPERAR, REACTIVA LA MEMORIA SIN IMPORTAR SI ES TOP DOWN O BOTTOM UP
                            if (updateAffect) {
                                scene.updateActivationWithAffect(affectIntensity);
                            } else {
                                scene.updateActivation();
                            }

                            scene.setTimeStamp(timeStamp);

                            similarScenes.add(scene);

                            //ALMACENA LA MEMORIA RECUPERADA EN MID-TERM
                            midTermMemoryScenesByID.put(scene.getId(), scene);

                            //SI NO LO PUEDE RECUPERAR, LA MEMORIA SE REACTIVA SOLO SI VUELVE A VER EL MISMO ESTIMULO
                        } else if (similarity >= 1.0 && retrievalType == BOTTOM_UP_RETRIEVAL) {

                            if (updateAffect) {
                                scene.updateActivationWithAffect(affectIntensity);
                            } else {
                                scene.updateActivation();
                            }

                            scene.setTimeStamp(timeStamp);

                            similarScenes.add(scene);

                            //ALMACENA LA MEMORIA RECUPERADA EN MID-TERM
                            midTermMemoryScenesByID.put(scene.getId(), scene);
                        }

                    }

                }

                //Sort the memories by the similarity
                Collections.sort(similarScenes);

                return similarScenes;

            } else {
                createStoragePaths();
                SimpleLogger.log(this, "Archivo no existe, primera ejecucion LTM");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return similarScenes;
    }

    /**
     *
     * @param sceneId
     * @param timeStamp
     * @return
     */
    public Scene retrieveSceneByID(int sceneId, long timeStamp) {

        Scene retrievedScene = midTermMemoryScenesByID.get(sceneId);

        if (retrievedScene != null) {

            double elapsedTime = (timeStamp - retrievedScene.getTimeStamp());

            elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

            double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, retrievedScene.getActivation());

            //SimpleLogger.log(this, "Retrievability MTM " +retrievability);
            if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                //SI TOP DOWN POR ID Y NO SIMILITUD NO DEBE ACTUALIZAR
//                retrievedScene.updateActivation();
//                retrievedScene.setTimeStamp(timeStamp);
            } else {
                retrievedScene = null;
            }

        } else {

            retrievedScene = retrieveSceneByIDLTM(sceneId, timeStamp);

        }

        //AQUI FALTA EVALUAR LA RECUPERABILIDAD, YA QUE APESAR DE QUE EXISTA PUEDE SER NO RECUPERADO
        return retrievedScene;
    }

    /**
     *
     * @param sceneId
     * @param timeStamp
     * @return
     */
    public Scene retrieveSceneByIDLTM(int sceneId, long timeStamp) {

        Scene retrievedScene = null;

        try {

            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME));

                String line;

                while ((line = br.readLine()) != null) {

                    Scene scene = new Scene(line);

                    if (scene.getId() == sceneId) {
                        retrievedScene = scene;
                        break;
                    }

                }

                //RETRIEVABILITY
                if (retrievedScene != null) {

                    double elapsedTime = (timeStamp - retrievedScene.getTimeStamp());

                    elapsedTime = elapsedTime / (Configuration.RETRIEVABILITY_DECREASING_SCALE); //en dias

                    double retrievability = ActivationFunctions.ebbinghausR(elapsedTime, retrievedScene.getActivation());

                    //SimpleLogger.log(this, "Retrievability LTM " +retrievability);
                    if (retrievability >= Configuration.RETRIEVABILITY_THRESHOLD) {

                        //SI TOP DOWN POR ID Y NO SIMILITUD NO DEBE ACTUALIZAR
                        //retrievedScene.updateActivation();
                        //retrievedScene.setTimeStamp(timeStamp);
                        midTermMemoryScenesByID.put(retrievedScene.getId(), retrievedScene);

                    } else {

                        /**
                         * SE REQUIERE VOLVER A VER EL ESTIMULO FISICAMENTE
                         */
                        retrievedScene = null;

                    }

                }

                return retrievedScene;

            } else {
                createStoragePaths();
                SimpleLogger.log(this, "Archivo no existe, primera ejecucion");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retrievedScene;
    }

    /**
     * *
     * AFFECTIVE RETRIEVAL
     *
     * @param isPositiveAffect
     * @return
     */
    public Scene retrieveMostAffectiveScene(boolean isPositiveAffect) {

        String fileName = Configuration.CA3_STORAGE_NEG_AFFECT;

        if (isPositiveAffect) {

            fileName = Configuration.CA3_STORAGE_POS_AFFECT;

        }

        Scene scenetInMTM = null;

        if (!midTermMemoryScenesByID.isEmpty()) {

            HashMap<Integer, Scene> sortedAHM = null;

            if (isPositiveAffect) {

                sortedAHM = sortByPositiveAffect(midTermMemoryScenesByID, true);

            } else {

                sortedAHM = sortByNegativeAffect(midTermMemoryScenesByID, true);
            }

            scenetInMTM = sortedAHM.entrySet().iterator().next().getValue();
        }

        Scene sceneInLTM = retrieveSceneByAffectLTM(1, fileName);

        Scene mostAffective = new Scene();

        if (scenetInMTM != null) {
            if (sceneInLTM != null) {

                if (isPositiveAffect) {

                    if (scenetInMTM.getPositiveAffect() > sceneInLTM.getPositiveAffect()) {
                        mostAffective = scenetInMTM;
                    } else {
                        mostAffective = sceneInLTM;
                    }

                } else {

                    if (scenetInMTM.getNegativeAffect() > sceneInLTM.getNegativeAffect()) {
                        mostAffective = scenetInMTM;
                    } else {
                        mostAffective = sceneInLTM;
                    }
                }

            } else {
                mostAffective = scenetInMTM;
            }
        } else {
            if (sceneInLTM != null) {
                mostAffective = sceneInLTM;
            }
        }

        return mostAffective;
    }

    public Scene retrieveLessAffectiveScene(boolean isPositiveAffect) {

        String fileName = Configuration.CA3_STORAGE_NEG_AFFECT;

        if (isPositiveAffect) {

            fileName = Configuration.CA3_STORAGE_POS_AFFECT;

        }
        Scene sceneInMTM = null;

        if (!midTermMemoryScenesByID.isEmpty()) {

            HashMap<Integer, Scene> sortedAHM = null;

            if (isPositiveAffect) {

                sortedAHM = sortByPositiveAffect(midTermMemoryScenesByID, false);

            } else {

                sortedAHM = sortByNegativeAffect(midTermMemoryScenesByID, false);

            }

            sceneInMTM = sortedAHM.entrySet().iterator().next().getValue();
        }

        Scene sceneInLTM = retrieveSceneByAffectLTM(-1, fileName);

        Scene lessAffective = new Scene();

        if (sceneInMTM != null) {
            if (sceneInLTM != null) {

                if (isPositiveAffect) {
                    if (sceneInMTM.getPositiveAffect() < sceneInLTM.getPositiveAffect()) {
                        lessAffective = sceneInMTM;
                    } else {
                        lessAffective = sceneInLTM;
                    }
                } else {
                    if (sceneInMTM.getNegativeAffect() < sceneInLTM.getNegativeAffect()) {
                        lessAffective = sceneInMTM;
                    } else {
                        lessAffective = sceneInLTM;
                    }
                }

            } else {
                lessAffective = sceneInMTM;
            }
        } else {
            if (sceneInLTM != null) {
                lessAffective = sceneInLTM;
            }
        }

        return lessAffective;
    }

    public Scene retrieveMostPositiveAffectiveScene() {
        return retrieveMostAffectiveScene(true);
    }

    public Scene retrieveLessPositiveAffectiveScene() {
        return retrieveLessAffectiveScene(true);
    }

    public Scene retrieveMostNegativeAffectiveScene() {
        return retrieveMostAffectiveScene(false);
    }

    public Scene retrieveLessNegativeAffectiveScene() {
        return retrieveLessAffectiveScene(false);
    }

    private Scene retrieveSceneByAffectLTM(int lineNumber, String fileName) {
        try {

            File file = new File(STORAGE_PATH + fileName);

            if (file.exists()) {

                Scene object = null;

                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + fileName));

                int count = 0;

                String line;

                while ((line = br.readLine()) != null) {

                    object = new Scene(line);

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
    public static CA3StorageHandler getInstance() {
        if (instance == null) {
            instance = new CA3StorageHandler();
        }
        return instance;
    }

    public static CA3StorageHandler getInstance(boolean load) {
        if (instance == null) {
            instance = new CA3StorageHandler(load);
        }
        return instance;
    }

    /**
     * PUEDE SERVIR PARA ACTUALIZAR DE FORMA UN POCO MAS EFICIENTE PERO SE
     * REQUIERE UN TAMANO DE FILA FIJO
     */
    /*
    public void reinforceMemories(HashMap<Integer, Scene> scenes) {

        try {
            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists()) {

                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                long pointer = raf.getFilePointer();
                String lineData = "";

                while ((lineData = raf.readLine()) != null) {
                    pointer = raf.getFilePointer() - lineData.length() - 2;

                    Scene scene = new Scene(lineData);

                    Scene updatedScene = scenes.get(scene.getId());

                    if (updatedScene != null) {

                        System.out.println("Changing string in file " + file);

                        String replacingString = updatedScene.toString();

                        raf.seek(pointer);
                        raf.writeBytes(replacingString);

                        //if the replacingString has less number of characters than the matching string line then enter blank spaces.
                        if (replacingString.length() == lineData.length()) {

                            raf.seek(raf.getFilePointer() + 2);

                        } else if (replacingString.length() < lineData.length()) {

                            int difference = (lineData.length() - replacingString.length()) + 1;

                            for (int i = 0; i < difference; i++) {
                                raf.writeBytes(" ");
                            }

                            raf.writeBytes(System.getProperty("line.separator"));
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    /**
     * *
     * UPDATE A SCENE VALUE
     */
    /*
    public void replaceStringInFile(int sceneId) {
        try {
            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists()) {

                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                long pointer = raf.getFilePointer();
                String lineData = "";

                while ((lineData = raf.readLine()) != null) {
                    pointer = raf.getFilePointer() - lineData.length() - 2;

                    System.out.println("scene with " + lineData);
                    Scene scene = new Scene(lineData);

                    if (scene.getId() == sceneId) {
                        System.out.println("Changing string in file " + file);

                        scene.incrementRepetitions();

                        String replacingString = scene.toString();

                        raf.seek(pointer);
                        raf.writeBytes(replacingString);

                        //if the replacingString has less number of characters than the matching string line then enter blank spaces.
                        if (replacingString.length() == lineData.length()) {

                            raf.seek(raf.getFilePointer() + 2);

                        } else if (replacingString.length() < lineData.length()) {

                            int difference = (lineData.length() - replacingString.length()) + 1;

                            for (int i = 0; i < difference; i++) {
                                raf.writeBytes(" ");
                            }

                            raf.writeBytes(System.getProperty("line.separator"));
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
