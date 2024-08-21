/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.t2string;

import java.util.ArrayList;

/**
 *
 * @author Luis
 */
public class OccupancyGrid {
    
    private int itemCount;
    private ArrayList<Integer> grid[][]; //Contiene la matriz principal
    private ArrayList<Integer> orderGrid[][]; //Contiene la matriz pero creada con el orden de los objetos con los ID
    
    public OccupancyGrid(int itemCount, ArrayList<Integer> grid[][], ArrayList<Integer> orderGrid[][]){
        this.itemCount = itemCount;
        this.grid = grid;
        this.orderGrid = orderGrid;
    }

    public ArrayList<Integer>[][] getGrid() {
        return grid;
    }

    public void setGrid(ArrayList<Integer>[][] grid) {
        this.grid = grid;
    }

    public ArrayList<Integer>[][] getOrderGrid() {
        return orderGrid;
    }

    public void setOrderGrid(ArrayList<Integer>[][] orderGrid) {
        this.orderGrid = orderGrid;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
