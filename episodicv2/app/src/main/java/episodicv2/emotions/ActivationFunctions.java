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

/**
 *
 * @author Luis
 */
public class ActivationFunctions {

    /**
     * SE EJECUTA CADA CIERTO TIEMPO DURANTE EL DECAIMIENTO DE MEMORIA A LARGO
     * PLAZO EL REINFORCEMENT ACTUALIZA EL STRENGHT
     * https://en.wikipedia.org/wiki/Forgetting_curve
     * https://psychology.stackexchange.com/questions/5199/which-equation-is-ebbinghauss-forgetting-curve-and-what-do-the-constants-repres
     *
     * @return
     */
    public static double ebbinghausR(double time, double strength) {
        
        double retrievability = 0.0f;

        retrievability = Math.exp(-1.0 * (time / strength));

        return retrievability;
    }
    
    /***
     * LA SIGMOIDE DETERMINA EL NIVEL DE ACTIVACION DE UN TRAZO DE MEMORIA
     * @param coefficient
     * @return 
     */

    public static double sigmoid(double coefficient) {
        
        double s = 1 / (1 + Math.exp(-1 * coefficient));
        
        return s;
    }
    
    /***
     * FUNCION DE DECAIMIENTO DE WORKING MEMORY
     * @param time
     * @return 
     */
    
    public static double altmannSchunn(double time, double scale){
        
        double activation = -0.5 * Math.log(time*scale);
        
        return activation;
    }
    
    /**
     * FUNCION DE ACTIVACION DE UN TRAZO DE MEMORIA TOMANDO EN CUENTA EL AFECTO
     * w(t) = alpha*(repetitions*scale) + beta*affect
     * @param repetitions
     * @param scale
     * @param affect
     * @param alpha
     * @param beta
     * @return 
     */
    
    public static double weight(double repetitions, double scale, double affect, double alpha, double beta){
                
        double w1 = alpha*(repetitions*scale);
        double w2 = beta*affect;
        
        double wt = w1 + w2;
        
        
        return wt;
        
    }
    
    
    public static double affect(double positive, double negative){
        
        double iny = Math.max(positive, negative);
        
        return iny;
    }
    
    
}
