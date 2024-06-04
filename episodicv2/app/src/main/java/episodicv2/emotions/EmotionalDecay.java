/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.emotions;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import episodicv2.configuration.Configuration;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Luis Martin
 */
public class EmotionalDecay extends TimerTask {

    private Timer timer;
    private boolean started = false;
    private int t = 0, t1 = 0, t2 = 0;
    private double gamma = 100;

    private double activation = 0.0;
    private double newActivation = 0.0;

    private double positiveActivation = 0.0;
    private double newPositiveActivation = 0.0;

    private double negativeActivation = 0.0;
    private double newNegativeActivation = 0.0;

    private String tag = "Timer:";

    public EmotionalDecay() {
        timer = new Timer();
    }

    public EmotionalDecay(String name) {
        timer = new Timer();
        this.tag = name;
    }

    public void start() {
        if (!started) {
            timer.scheduleAtFixedRate(this, 0, Configuration.EMOTIONAL_DECAY_INTERVAL);
            started = true;
        }
    }

    public void start(long interval) {
        if (!started) {
            timer.scheduleAtFixedRate(this, 0, interval);
            started = true;
        }
    }

    public void setNewActivation(double newPositiveActivation, double newNegativeActivation) {

        double newActivation = ActivationFunctions.affect(newPositiveActivation, newNegativeActivation);
        
        if (newActivation > activation) {
            this.newActivation = newActivation;
            t = 0;
        }

        if (newPositiveActivation > positiveActivation) {
            this.newPositiveActivation = newPositiveActivation;
            t1 = 0;
        }

        if (newNegativeActivation > negativeActivation) {
            this.newNegativeActivation = newNegativeActivation;
            t2 = 0;
        }
    }

    public double getActivation() {
        return this.activation;
    }

    public double getPositiveActivation() {
        return this.positiveActivation;
    }

    public double getNegativeActivation() {
        return this.negativeActivation;
    }

    @Override
    public void run() {

        activation = newActivation * Math.exp(-1 * (Math.pow(t, 2)) / Configuration.DECAY_GAMMA);

        positiveActivation = newPositiveActivation * Math.exp(-1 * (Math.pow(t1, 2)) / Configuration.DECAY_GAMMA);

        negativeActivation = newNegativeActivation * Math.exp(-1 * (Math.pow(t2, 2)) / Configuration.DECAY_GAMMA);

        t++;
        t1++;
        t2++;
    }

}
