/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.storage.dg;

import episodicv2.core.configuration.Configuration;
import static episodicv2.core.configuration.Configuration.DG_SIZE_FILENAME;
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
public class DGStorageHandler {

    private static final String STORAGE_PATH = Configuration.STORAGE_BASE_PATH + "/dg/";
    private static final String FILENAME = Configuration.DG_STORAGE_FILENAME;
    private static final double SIMILARITY_THRESHOLD = Configuration.SIMILARITY_THRESHOLD;

    private static DGStorageHandler instance = null;
    private Timer consolidationTimer;

    //CARGA LAS ESCENAS EXISTENTES
    private static int DG_SIZE = 0;
    private static ArrayList<Scene> midTermMemoryScenes;
    private static Map<Integer, Scene> midTermMemoryScenesByID;

    private DGStorageHandler() {

        midTermMemoryScenes = new ArrayList<>();
        midTermMemoryScenesByID = new ConcurrentHashMap<>();

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        loadDGSize();

        startConsolidation();
    }

    private DGStorageHandler(boolean load) {

        midTermMemoryScenes = new ArrayList<>();
        midTermMemoryScenesByID = new HashMap<>();

        if (load) {
            load();
        }

        loadDGSize();

        consolidationTimer = new Timer("Consolidation." + this.getClass().getName());

        startConsolidation();
    }

    public void startConsolidation() {

        long delay = Configuration.CONSOLIDATION_INTERVAL;

        consolidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //SimpleLogger.log(this, "Storing...");
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
        return midTermMemoryScenes;
    }

    /**
     * *
     * CARGA EL ARCHIVO DG_SIZE PARA SABER EL INDICE DE LA ULTIMA ESCENA
     * GUARDADA
     */
    public void loadDGSize() {
        try {
            File file = new File(STORAGE_PATH + DG_SIZE_FILENAME);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + DG_SIZE_FILENAME));

                String line = br.readLine();

                int dgSize = Integer.parseInt(line);

                DG_SIZE = dgSize;

                SimpleLogger.log(this, "Last Scene Index: " + dgSize);

            } else {
                SimpleLogger.log(this, "Archivo no existe, primera ejecucion");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *
     * LOAD THE PATTERNS ON MEMORY
     */
    public void load() {
        try {

            File file = new File(STORAGE_PATH + FILENAME);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME));

                String line;

                while ((line = br.readLine()) != null) {
                    Scene scene = new Scene(line);
                    midTermMemoryScenes.add(scene);
                }
            } else {
                SimpleLogger.log(this, "Archivo no existe, primera ejecucion");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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

            File updatedKnowledge = new File(STORAGE_PATH + "dg_scenes_temp.txt");
            PrintWriter updatedKnowledgeWriter = new PrintWriter(new FileWriter(updatedKnowledge));

            HashMap<Integer, Scene> scenesToUpdate = new HashMap<>();
            scenesToUpdate.putAll(midTermMemoryScenesByID);

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

            }

            SortedSet<Integer> sceneIDs = new TreeSet<>(scenesToUpdate.keySet());

            for (Integer sceneID : sceneIDs) {

                Scene scene = scenesToUpdate.get(sceneID);

                //if(scene.getActivation() >= Configuration.CONSOLIDATION_THRESHOLD){
                    //FALTA EVALUAR SI TIENE LA ACTIVACION SUFICIENTE PARA SER GUARDADO
                    updatedKnowledgeWriter.println(scene.toString());
                    updatedKnowledgeWriter.flush();
                //}
            }

            updatedKnowledgeWriter.close();
            previousKnowledgeReader.close();

            if (previousKnowledge.delete()) {

                if (!updatedKnowledge.renameTo(previousKnowledge)) {
                    SimpleLogger.log(this, "Error on updating file");
                }

            } else {
                SimpleLogger.log(this, "Error on updating file");
            }

            //SAVE THE CURRENT DG_SIZE
            File dgSizeFile = new File(STORAGE_PATH + DG_SIZE_FILENAME);
            PrintWriter dgSizeFileWriter = new PrintWriter(new FileWriter(dgSizeFile));

            dgSizeFileWriter.println(DG_SIZE);
            dgSizeFileWriter.flush();

            dgSizeFileWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * CHECKS IF EXISTS A 2D STRING SIMILAR INTO THE STORED ONES
     *
     * @param pattern
     * @param affect
     * @return
     */
    public Scene getSimilar(String pattern, double affect) {

        Scene similar = null;

        for (int i = 0; i < midTermMemoryScenes.size(); i++) {

            Scene scene = midTermMemoryScenes.get(i);
            String patternToCompare = scene.getPattern();

            float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

            if (similarity >= SIMILARITY_THRESHOLD) {
                similar = scene;

                scene.updateActivationWithAffect(affect);
                scene.setTimeStamp(System.currentTimeMillis());

                break;
            }

        }

        //SI NO HAY UN PATRON SIMILAR EN MID-TERM LO BUSCA EN LONG-TERM
        if (similar == null) {

            try {

                File file = new File(STORAGE_PATH + FILENAME);

                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(STORAGE_PATH + FILENAME));

                    String line;

                    while ((line = br.readLine()) != null) {
                        Scene scene = new Scene(line);

                        String patternToCompare = scene.getPattern();

                        float similarity = T2DString.lcs2DString(pattern, patternToCompare, T2DString.SIMILARITY_TYPE_1);

                        if (similarity >= SIMILARITY_THRESHOLD) {

                            similar = scene;

                            scene.updateActivationWithAffect(affect);
                            scene.setTimeStamp(System.currentTimeMillis());

                            midTermMemoryScenes.add(scene);
                            midTermMemoryScenesByID.put(scene.getId(), scene);

                            break;
                        }
                    }

                    br.close();

                } else {
                    SimpleLogger.log(this, "Archivo no existe, primera ejecucion");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return similar;
    }

    /**
     * STORES A NEW 2D STRING PATTERN
     *
     * @param pattern
     * @param positiveAffect
     * @param negativeAffect
     * @param affectIntensity
     * @param time
     * @return
     */
    public Scene createNewScene(String pattern, double positiveAffect, double negativeAffect, double affectIntensity, int time) {

        int assignedId = 0;
        Scene scene = getSimilar(pattern, affectIntensity);

        //SI NO HAY UNA ESCENA SIMILAR CREA UNA NUEVA
        if (scene == null) {

            DG_SIZE = DG_SIZE + 1;

            assignedId = DG_SIZE;

            scene = new Scene(assignedId, positiveAffect, negativeAffect, pattern, time);

            midTermMemoryScenes.add(scene);
            midTermMemoryScenesByID.put(scene.getId(), scene);

        }else{
            scene.updatePositiveAffect(positiveAffect);
            scene.updateNegativeAffect(negativeAffect);
        }

        //SimpleLogger.log(this, "Created scene: " + scene.toString());
        return scene;
    }

    /**
     * CONSTRUCTORS
     */
    public static DGStorageHandler getInstance() {
        if (instance == null) {
            instance = new DGStorageHandler();
        }
        return instance;
    }

    public static DGStorageHandler getInstance(boolean load) {
        if (instance == null) {
            instance = new DGStorageHandler(load);
        }
        return instance;
    }
}
