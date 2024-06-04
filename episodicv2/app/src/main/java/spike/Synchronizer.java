/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spike;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;

/**
 *
 * @author Luis
 */
public class Synchronizer {
    
    private int time = 0;
    private int stackSize = 2;
    private HashMap<Integer,Object> elements;
    
    public Synchronizer(){
        elements = new HashMap<>();
    }
    
    public Synchronizer(int stackSize){
        elements = new HashMap<>();
        this.stackSize = stackSize;
    }
    public void addElement(int key, Object element, int elementTime){
        
        if(elementTime >= time){
            if (elementTime > time) {
                elements.clear();
                time = elementTime;
            }
            
            elements.put(key, element);     
        }
        
          
    }
    
    public Object getElement(int key){
        return elements.get(key);
    }
    
    
    public boolean isFull(){
        return stackSize == elements.size();
    }
    
    public void setStackSize(int stackSize){
        this.stackSize = stackSize;
    }
    
    public int getTime(){
        return time;
    }
}
