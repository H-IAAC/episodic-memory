/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.entities;

import episodicv2.utils.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author Luis
 */
//Cuayollotl Object
public class CObject implements Serializable {

    private int preId;
    private int classId;
    private double affect;
    private int x;
    private int y;
    private int time;
    private String label;
    private String features;
    
    private int shortTermMemoryTime = 0;
    private double shortTermActivation = 0.0;

    public CObject(int preId, int classId, int x, int y, String label, String features, int time) {
        this.preId = preId;
        this.classId = classId;
        this.affect = 0.0;
        this.x = x;
        this.y = y;
        this.label = label;
        this.features = features;
        this.time = time;
    }
    
    public CObject(String csv){
        
    }

    public String toString() {
        return "(" + preId + "," + classId + "," + label + ",(" + x + "," + y + ")," + time + ")";
    }

    public int getPreId() {
        return preId;
    }

    public void setPreId(int preId) {
        this.preId = preId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public double getAffect() {
        return affect;
    }

    public void setAffect(double affect) {
        this.affect = affect;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFeatures() {
        return features;
    }
    
    public BufferedImage getFeaturesAsImage(){
        BufferedImage image = ImageUtils.getImageFromBase64(features);
        return image;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getShortTermActivation() {
        return shortTermActivation;
    }

    public void setShortTermActivation(double shortTermActivation) {
        if(shortTermActivation > 1){
            this.shortTermActivation = 1.0;
        }else if(shortTermActivation > 0){
            this.shortTermActivation = 0.0;
        }else{
            this.shortTermActivation = shortTermActivation;
        }
        
    }

    public int getShortTermMemoryTime() {
        return shortTermMemoryTime;
    }

    public void setShortTermMemoryTime(int shortTermMemoryTime) {
        this.shortTermMemoryTime = shortTermMemoryTime;
    }

}
