/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets.storage.itc;

import br.unicamp.cst.core.entities.Codelet;
import episodicv2.core.configuration.Configuration;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author karenlima
 */
public class ITCStorageHandler {
    
    private String PATH = Configuration.STORAGE_BASE_PATH + "/itc/features/" + Configuration.ITC_FEATURES_BASEFILENAME;
    private final int MAX_FEATURES = 81;
    
    private HashMap<Integer, BufferedImage> classFeatures;
    
    private static ITCStorageHandler instance = null;
    
    private ITCStorageHandler() {
        classFeatures = new HashMap<>();
        loadImages();
    }

//    @Override
//    public void accessMemoryObjects() {
//        
//    }
//    
//    @Override
//    public void proc() {
//        
//    }
//    
//    @Override
//    public void calculateActivation(){
//        
//    }
    
    public BufferedImage getFeatures(int classId) {
        
        BufferedImage features = classFeatures.get(classId);
        
        if(features == null){
            features = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        }
        
        return features;
    }

    public BufferedImage getFeaturesFromLTM(int objectId) {

        try {
            
            BufferedImage features = ImageIO.read(new File(PATH + "" + objectId + ".jpg"));

            return features;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    public HashMap<Integer, BufferedImage> getClassFeatures() {
        return classFeatures;
    }

    public void setClassFeatures(HashMap<Integer, BufferedImage> classFeatures) {
        this.classFeatures = classFeatures;
    }
    
    private void loadImages() {
        try {
            
            String loadingPath = Configuration.STORAGE_BASE_PATH + "/itc/features/";
            
            File listOfImages = new File(loadingPath);
            
            String imagesPath[] = listOfImages.list();
            
            for (String imagePath: imagesPath) {
                
                File imageFile = new File(loadingPath + imagePath);
                BufferedImage features = ImageIO.read(imageFile);
                
                String nameNoExtension= imagePath.split("\\.")[0];
                String imageName = nameNoExtension.replaceFirst("class_", "");
                
                int imageClass = Integer.parseInt(imageName);
                
                classFeatures.put(imageClass, features);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static ITCStorageHandler getInstance() {

        if (instance == null) {
            instance = new ITCStorageHandler();
        }

        return instance;
    }
    
}
