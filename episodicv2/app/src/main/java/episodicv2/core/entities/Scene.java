/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.entities;

import episodicv2.core.configuration.Configuration;
import episodicv2.core.storage.ActivationFunctions;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author luis_
 */
public class Scene implements Serializable, Comparable<Scene> {

    private int id;
    private String pattern;
    private int time;
    private int repetitions;
    private double positiveAffect;
    private double negativeAffect;
    private double activation;
    private long timeStamp;
    private ArrayList<SceneRelation> relations;

    //The similarity is used as an activation of the retrieved scene
    private double activeSimilarity;

    private boolean recent = true;

    public Scene() {
    }

    public Scene(int id, String pattern, int time) {
        this.id = id;
        this.pattern = pattern;
        this.positiveAffect = 0.0;
        this.negativeAffect = 0.0;
        this.activation = 0.5;
        this.updateActivationWithAffect(0.0);
        this.time = time;
        this.timeStamp = System.currentTimeMillis();
    }

    public Scene(int id, double positiveAffect, double negativeAffect, String pattern, int time) {
        this.id = id;
        this.pattern = pattern;
        this.positiveAffect = positiveAffect;
        this.negativeAffect = negativeAffect;
        this.activation = 0.5;
        this.updateActivationWithAffect(ActivationFunctions.affect(positiveAffect, negativeAffect));
        this.time = time;
        this.timeStamp = System.currentTimeMillis();
    }

    public Scene(String storedString) {

        String data[] = storedString.split(",");
        this.id = Integer.parseInt(data[0]);
        this.pattern = data[1] + "," + data[2] + "," + data[3] + "," + data[4] + "," + data[5];
        this.repetitions = Integer.parseInt(data[6]);
        this.positiveAffect = Double.parseDouble(data[7]);
        this.negativeAffect = Double.parseDouble(data[8]);
        this.activation = Double.parseDouble(data[9]);
        this.time = Integer.parseInt(data[10]);
        this.timeStamp = Long.parseLong(data[11]);
        this.recent = false;

    }

    public void updateActivationWithAffect(double affectIntensity) {

        
        this.repetitions++;

        double scale = Configuration.SIGMOID_SCALE;
        double alpha = Configuration.ALPHA_MEMORY_INC_RELEVANCE;
        double beta = Configuration.BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, affectIntensity, alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);

        this.activation = (this.activation + currentActivation) / 2.0;
        
//        SimpleLogger.log(this, "Scene: "+getId()+" activation: "+this.activation+" repetition: "+repetitions);
    }

    public void updateActivation() {

        this.repetitions++;

        double scale = Configuration.SIGMOID_SCALE;
        double alpha = Configuration.ALPHA_MEMORY_INC_RELEVANCE;
        double beta = Configuration.BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, ActivationFunctions.affect(this.positiveAffect, this.negativeAffect), alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);

        this.activation = (this.activation + currentActivation) / 2.0;
    }

    public void updatePositiveAffect(double affect) {
        this.positiveAffect = (this.positiveAffect + affect) / 2;
    }

    public void updateNegativeAffect(double affect) {
        this.negativeAffect = (this.negativeAffect + affect) / 2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public double getPositiveAffect() {
        return this.positiveAffect;
    }

    public void setPositiveAffect(double affect) {
        this.positiveAffect = affect;
    }

    public double getNegativeAffect() {
        return this.negativeAffect;
    }

    public void setNegativeAffect(double affect) {
        this.negativeAffect = affect;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
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

    public ArrayList<SceneRelation> getRelations() {
        return relations;
    }

    public void setRelations(ArrayList<SceneRelation> relations) {
        this.relations = relations;
    }

    public boolean isRecent() {
        return recent;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public double getActiveSimilarity() {
        return activeSimilarity;
    }

    public void setActiveSimilarity(double activeSimilarity) {
        this.activeSimilarity = activeSimilarity;
    }

    @Override
    public String toString() {

        return id + "," + pattern + "," + String.format("%010d", repetitions) + "," + String.format("%.5f", positiveAffect) + "," + String.format("%.5f", negativeAffect) + "," + String.format("%.5f", activation) + "," + String.format("%020d", time) + "," + timeStamp;
    }

    @Override
    public int compareTo(Scene o) {
        return (this.getActiveSimilarity() > o.getActiveSimilarity() ? -1
                : (this.getActiveSimilarity() == o.getActiveSimilarity() ? 0 : 1));
    }

}
