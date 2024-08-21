/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.entities;

import episodicv2.core.configuration.Configuration;
import episodicv2.core.storage.ActivationFunctions;
import java.io.Serializable;

/**
 *
 * @author luis_
 */
public class ObjectRelation implements Serializable{

    private int object1Id;
    private int object2Id;
    private double activation;
    private int repetitions;
    private int time;
    private long timeStamp;
    private boolean recent = true;
    private boolean updated = false;

    public ObjectRelation(int object1Id, int object2Id, int time) {
        this.object1Id = object1Id;
        this.object2Id = object2Id;
        this.time = time;
        this.activation = 0.5;
        this.timeStamp = System.currentTimeMillis();
    }

    public ObjectRelation(String str) {
        String data[] = str.split(",");
        this.object1Id = Integer.parseInt(data[0]);
        this.object2Id = Integer.parseInt(data[1]);
        this.repetitions = Integer.parseInt(data[2]);
        this.activation = Double.parseDouble(data[3]);
        this.time = Integer.parseInt(data[4]);
        this.timeStamp = Long.parseLong(data[5]);
        this.recent = false;
        this.updated = false;
    }

    @Override
    public boolean equals(Object obj) {
        ObjectRelation other = (ObjectRelation)obj;
        
        if(this.object1Id == other.getObject1Id() && this.object2Id == other.getObject2Id() && this.time == other.getTime()){
            return true;
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.object1Id;
        hash = 59 * hash + this.object2Id;
        hash = 59 * hash + this.time;
        return hash;
    }
    
    

    public void incrementRepetitions(double affectIntensity) {
        
        this.repetitions++;
        
        double scale = Configuration.SIGMOID_SCALE;
        double affect = affectIntensity;
        double alpha = Configuration.ALPHA_MEMORY_INC_RELEVANCE;
        double beta = Configuration.BETA_AFFECT_RELEVANCE;       
        
        double weight = ActivationFunctions.weight(repetitions, scale, affect, alpha, beta);
  
        double currentActivation = ActivationFunctions.sigmoid(weight);
        
        this.activation  = (this.activation + currentActivation) / 2.0;
    }

    public int getObject1Id() {
        return object1Id;
    }

    public void setObject1Id(int scene1Id) {
        this.object1Id = scene1Id;
    }

    public int getObject2Id() {
        return object2Id;
    }

    public void setObject2Id(int scene2Id) {
        this.object2Id = scene2Id;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRecent() {
        return recent;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {

        return object1Id + "," + object2Id + "," + String.format("%010d", repetitions) + "," + String.format("%.5f", activation) + "," + String.format("%020d", time) + "," + timeStamp;
    }

}
