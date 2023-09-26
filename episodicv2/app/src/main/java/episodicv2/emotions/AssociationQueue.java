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
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author luis_
 */
public class AssociationQueue {

    private static AssociationQueue instance = null;
    private static Queue<Integer> associationQueue = new LinkedList<>();

    private AssociationQueue() {
    }

    public int[] addSceneId(int sceneId) {

        int tuple[] = new int[2];

        //RECUPERA EL PRIMER ELEMENTO AGREGADO
        Integer current = associationQueue.peek();

        // SI LA COLA NO ESTA VACIA
        if (current != null) {

            //SI NO ES LA MISMA ESCENA LO AGREGA A LA COLA
            if (current != sceneId) {
                associationQueue.add(sceneId);
            }
            
        //SI LA COLA ESTA VACIA SE AGREGA COMO PRIMER ELEMENTO
        } else {
            associationQueue.add(sceneId);
        }

        //SI YA SON DOS ELEMENTOS EN LA COLA DE ASOCIACION, SE GENERA UNA TUPLA
        if (associationQueue.size() >= 2) {

            tuple = new int[2];

            tuple[0] = associationQueue.remove();
            tuple[1] = sceneId;
            
        //SINO REGRESA UNA TUPLA DUMMY QUE NO SERA ALMACENADA
        } else {
            
            tuple[0] = sceneId;
            tuple[1] = 0;
        }

        return tuple;
    }

    public static AssociationQueue getInstance() {

        if (instance == null) {
            instance = new AssociationQueue();
        }

        return instance;
    }

}
