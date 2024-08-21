/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.entities;

import java.util.ArrayList;

/**
 *
 * @author Luis Martin
 */
public class CObjectList {
 
    private ArrayList<CObject> objects;
    private int time;
    
    public CObjectList(){
        
    }

    public ArrayList<CObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<CObject> objects) {
        this.objects = objects;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    
}
