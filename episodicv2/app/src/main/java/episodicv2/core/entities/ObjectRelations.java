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
public class ObjectRelations implements Serializable {

    private int objectId;
    private int repetitions;
    private double positiveAffect;
    private double negativeAffect;
    private double activation;
    private int time;
    private long timeStamp;

    private ArrayList<Integer> relations;

    public ObjectRelations(int objectId, ArrayList<Integer> relations) {
        this.objectId = objectId;
        this.relations = relations;
    }

    public ObjectRelations(int objectId, double positiveAffect, double negativeAffect, int time) {
        this.objectId = objectId;
        this.positiveAffect = positiveAffect;
        this.negativeAffect = negativeAffect;
        this.activation = 0.5;
        this.updateActivationWithAffect(ActivationFunctions.affect(positiveAffect, negativeAffect));
        this.time = time;
        this.timeStamp = System.currentTimeMillis();
    }

    public ObjectRelations(String storedString) {
        String data[] = storedString.split(",");
        this.objectId = Integer.parseInt(data[0]);
        this.repetitions = Integer.parseInt(data[1]);
        this.positiveAffect = Double.parseDouble(data[2]);
        this.negativeAffect = Double.parseDouble(data[3]);
        this.activation = Double.parseDouble(data[4]);
        this.time = Integer.parseInt(data[5]);
        this.timeStamp = Long.parseLong(data[6]);
    }

    public ObjectRelations(int objectId) {
        this.objectId = objectId;
        this.relations = new ArrayList<>();
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public double getPositiveAffect() {
        return positiveAffect;
    }

    public void setPositiveAffect(double affect) {
        this.positiveAffect = affect;
    }

    public double getNegativeAffect() {
        return negativeAffect;
    }

    public void setNegativeAffect(double affect) {
        this.negativeAffect = affect;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void updatePositiveAffect(double affect) {
        this.positiveAffect = (this.positiveAffect + affect) / 2;
    }

    public void updateNegativeAffect(double affect) {
        this.negativeAffect = (this.negativeAffect + affect) / 2;
    }

    public void updateActivationWithAffect(double affectIntensity) {

        this.repetitions++;

        double scale = Configuration.SIGMOID_SCALE;
        double alpha = Configuration.ALPHA_MEMORY_INC_RELEVANCE;
        double beta = Configuration.BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, affectIntensity, alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);

        this.activation = (this.activation + currentActivation) / 2.0;
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

    public ArrayList<Integer> getRelations() {
        return relations;
    }

    public void setRelations(ArrayList<Integer> relations) {
        this.relations = relations;
    }

    @Override
    public String toString() {
        return objectId + "," + String.format("%010d", repetitions) + "," + String.format("%.5f", positiveAffect) + "," + String.format("%.5f", negativeAffect) + "," + String.format("%.5f", activation) + "," + String.format("%020d", time) + "," + timeStamp;
    }

}
