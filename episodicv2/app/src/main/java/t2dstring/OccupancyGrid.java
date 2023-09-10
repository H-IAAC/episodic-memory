/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t2dstring;
import java.util.ArrayList;
/**
 *
 * @author karenlima
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
