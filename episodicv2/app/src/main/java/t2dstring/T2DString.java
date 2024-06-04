/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t2dstring;



//import dwm.core.entities.CObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author karenlima
 */
public class T2DString {
    
    public static final int SIMILARITY_TYPE_0 = 0;
    public static final int SIMILARITY_TYPE_1 = 1;
    public static final int SIMILARITY_TYPE_2 = 2;
    
    private T2DString() {
    }

    public static OccupancyGrid createOccupancyGrid(ArrayList<Idea> objectList, int imageWidth, int imageHeight, int columnsX, int rowsY){
        
        int dx = imageWidth / columnsX;
        int dy = imageHeight / rowsY;

        int itemCount = objectList.size();

        ArrayList<Integer> aux[][] = new ArrayList[rowsY][columnsX];
        ArrayList<Integer> f2[][] = new ArrayList[rowsY][columnsX];

        for (int i = 0; i < objectList.size(); i++) {

            Idea object = objectList.get(i);
            //TODO como pegar os valores das Ideas pelo seu nome?
//            Double xdouble = (Double) object.get("x").getValue();
//            Double ydouble = (Double) object.get("y").getValue();
//            
            int id = (int) object.get("pid").getValue();
            int cx = (int) object.get("x").getValue();
            int cy = (int) object.get("y").getValue();
            int px = cx / dx;
            int py = cy / dy;
            if (f2[py][px] == null) {
                f2[py][px] = new ArrayList<>();
                aux[py][px] = new ArrayList<>();
            }

            aux[py][px].add(i + 1);

            f2[py][px].add(id);
        }
        
        OccupancyGrid occupancyGrid = new OccupancyGrid(itemCount, f2, aux);

        return occupancyGrid;
    }
    
    /**
     * Esta funcion crea un patron de 2D string de la lista de objetos recibida
     *
     * @param objectListPoints Lista de los ID, centros en x, y centros en y de
     * los objetos en la escena
     */
    
    public static String create2DStringPattern(OccupancyGrid occupancyGrid, int imageWidth, int imageHeight, int columnsX, int rowsY) {
        
        int itemCount = occupancyGrid.getItemCount();
        ArrayList<Integer> f2[][] = occupancyGrid.getGrid();
        ArrayList<Integer> aux[][] = occupancyGrid.getOrderGrid();
        

        /**
         * Create the x-string
         */
        int l = 0;
        StringBuilder u = new StringBuilder();

        for (int i = 0; i < columnsX; i++) {

            for (int j = rowsY - 1; j >= 0; j--) {

                if (f2[j][i] == null) {
                    continue;
                }

                if (f2[j][i].size() != 0) { //Considerar que pueden haber varios objetos en una sola casilla

                    u.append("(");
                    boolean first = true;
                    for (Integer classId : f2[j][i]) {

                        l = l + 1;

                        if (!first) {
                            u.append(":");
                        }

                        u.append(classId);

                        first = false;

                    }
                    u.append(")");

                }

            }

            if (i < columnsX && l < itemCount) {
                u.append("<");
            }

        }

        /**
         * Create the y-string and permutation
         */
        int k = 0;
        StringBuilder w = new StringBuilder();
        StringBuilder p = new StringBuilder();

        for (int j = rowsY - 1; j >= 0; j--) {
            for (int i = 0; i < columnsX; i++) {

                if (f2[j][i] == null) {
                    continue;
                }

                if (f2[j][i].size() != 0) { //Considerar que pueden haber varios objetos en una sola casilla

                    w.append("(");
                    boolean first = true;
                    for (int t = 0; t < f2[j][i].size(); t++) {

                        Integer classId = f2[j][i].get(t);
                        Integer classNum = aux[j][i].get(t);

                        k = k + 1;

                        if (!first) {
                            w.append(":");
                        }

                        w.append(classId);
                        p.append("(").append(classNum).append(")");

                        first = false;

                    }
                    w.append(")");

                }
            }
            if (j < columnsX && k < itemCount) {
                w.append("<");
            }
        }

        //Rank
        StringBuilder rankX = new StringBuilder();
        StringBuilder rankY = new StringBuilder();

        for (int i = 0; i < columnsX; i++) {
            int r = 0;
            for (int j = rowsY - 1; j >= 0; j--) {
                r++;

                if (f2[j][i] == null) {
                    continue;
                }

                if (f2[j][i].size() != 0) {

                    for (Integer classId : f2[j][i]) {
                        rankX.append("(").append(i + 1).append(")");
                        rankY.append("(").append(r).append(")");
                    }
                }

            }

        }

        return u.append(",").append(w).append(",").append(p).append(",").append(rankX).append(",").append(rankY).toString();

    }

    //-------------------------------------------------------------------------
    
    /**
     * FUNCIONES PARA DECODIFICAR UN PATRON
     * @param str1DImage
     * @return 
     */
    private static ArrayList<Integer> getItems(String str1DImage) {

        ArrayList<Integer> items = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str1DImage.replace("<", ""));

        while (matcher.find()) {
            items.add(Integer.parseInt(matcher.group()));
        }

        return items;
    }

    public static ArrayList<Integer>[][] decodeMatrix(String strImage, int columnsX, int rowsY) {

        String alpha[] = strImage.split(",");

        String alphax = alpha[0];
        String alphay = alpha[1];
        String alphap = alpha[2];
        String alpharx = alpha[3];
        String alphary = alpha[4];

        ArrayList<Integer> itemsAx = getItems(alphax);
        ArrayList<Integer> itemsAy = getItems(alphay);
        ArrayList<Integer> itemsAp = getItems(alphap);
        ArrayList<Integer> itemsARankX = getItems(alpharx);
        ArrayList<Integer> itemsARankY = getItems(alphary);

        ArrayList<Integer> img[][] = new ArrayList[rowsY][columnsX];

        for (int i = 0; i < itemsAx.size(); i++) {
            int itemClass = itemsAx.get(i);
            int px = itemsARankX.get(i) - 1;
            int py = rowsY - itemsARankY.get(i);

            if (img[py][px] == null) {
                img[py][px] = new ArrayList<>();
            }

            img[py][px].add(itemClass);
        }

        //debugMatrix(img);
        
        return img;

    }
    
    
    /**
     * FUNCION DE HIPPOCAMPO, DECODIFICA EL PATRON DE PHC Y LO INTEGRA CON PRC
     * Y ELIMINA CLASES REPETIDAS EN LA MISMA CASILLA
     */
    
    public static OccupancyGrid decodeMatrixAndReplace(HashMap<Integer,Integer> newIds,String strImage, int columnsX, int rowsY) {

        String alpha[] = strImage.split(",");

        String alphax = alpha[0];
        String alphay = alpha[1];
        String alphap = alpha[2];
        String alpharx = alpha[3];
        String alphary = alpha[4];

        ArrayList<Integer> itemsAx = getItems(alphax);
        ArrayList<Integer> itemsAy = getItems(alphay);
        ArrayList<Integer> itemsAp = getItems(alphap);
        ArrayList<Integer> itemsARankX = getItems(alpharx);
        ArrayList<Integer> itemsARankY = getItems(alphary);

        //Como viene de PHC cada preID es practicamente la permutacion 
        ArrayList<Integer> img[][] = new ArrayList[rowsY][columnsX];
        
        ArrayList<Integer> permutacion[][] = new ArrayList[rowsY][columnsX];

        int itemCount = 0;
        
        for (int i = 0; i < itemsAx.size(); i++) {
            int itemClass = itemsAx.get(i);
            int px = itemsARankX.get(i) - 1;
            int py = rowsY - itemsARankY.get(i);

            if (img[py][px] == null) {
                img[py][px] = new ArrayList<>();
                permutacion[py][px] = new ArrayList<>();
            }

            if(!img[py][px].contains(newIds.get(itemClass))){
                itemCount ++;
                img[py][px].add(newIds.get(itemClass));
                permutacion[py][px].add(itemCount);
               // permutacion[py][px].add(itemClass);
            }

        }

        OccupancyGrid occupancyGrid = new OccupancyGrid(itemCount, img, permutacion);//itemsAx.size(), img, permutacion);
        
        //debugMatrix(img);
        
        return occupancyGrid;

    }
    
    
    /**
     * FUNCION DE COMPARACION ENTRE DOS PATRONES DE IMAGEN
     * @param strImage1
     * @param strImage2
     * @param type
     * @return 
     */
    
    public static float lcs2DString(String strImage1, String strImage2, int type) {
        System.out.println("Executing lcs2DString");
        String alpha[] = strImage1.split(",");
        String beta[] = strImage2.split(",");

        String alphax = alpha[0];
        String alphay = alpha[1];
        String alphap = alpha[2];
        String alpharx = alpha[3];
        String alphary = alpha[4];

        String betax = beta[0];
        String betay = beta[1];
        String betap = beta[2];
        String betarx = beta[3];
        String betary = beta[4];

        ArrayList<Integer> itemsAx = getItems(alphax);
        ArrayList<Integer> itemsAy = getItems(alphay);
        ArrayList<Integer> itemsAp = getItems(alphap);
        ArrayList<Integer> itemsARankX = getItems(alpharx);
        ArrayList<Integer> itemsARankY = getItems(alphary);

        ArrayList<Integer> itemsBx = getItems(betax);
        ArrayList<Integer> itemsBy = getItems(betay);
        ArrayList<Integer> itemsBp = getItems(betap);
        ArrayList<Integer> itemsBRankX = getItems(betarx);
        ArrayList<Integer> itemsBRankY = getItems(betary);

        int m = itemsAx.size();
        int n = itemsBx.size();

        int indexAy[] = new int[m];
        int indexBy[] = new int[n];

        for (int i = 0; i < m; i++) {
            int idx = itemsAp.indexOf(i + 1);
            indexAy[i] = idx;
        }

        for (int i = 0; i < n; i++) {
            int idx = itemsBp.indexOf(i + 1);
            indexBy[i] = idx;
        }

        // Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        ArrayList<int[]> vertex = new ArrayList<>();
        HashMap<Integer, Set<Integer>> neighbors = new HashMap<>();
        HashMap<Integer, int[]> vertexInfo = new HashMap<>();
        
        int vertexNum = 0;
        
        for (int i = 0; i < m; i++) {

            for (int j = 0; j < n; j++) {

                int ai = itemsAx.get(i);
                int bj = itemsBx.get(j);
                
                if (ai == bj) {
                    
                   
                    int pk = indexAy[i];
                    int ql = indexBy[j];

                    int rx1 = itemsARankX.get(i);
                    int ry1 = itemsARankY.get(i);

                    int rx2 = itemsBRankX.get(j);
                    int ry2 = itemsBRankY.get(j);
                    
                    
                    //
//                    boolean skip = false;
//                    for (int k = 0; k < vertex.size(); k++) {
//                        int vv[] = vertex.get(k);
//                        int prx1 = vv[5];
//                        int pry1 = vv[7];
//                        int prx2 = vv[6];
//                        int pry2 = vv[8];
//
//                        if(vv[0] == ai && prx1 == rx1 && pry1 == ry1 && prx2 == rx2 && pry2 == ry2){
//                            System.out.println("Ya existe");
//                            skip = true;
//                            break;
//                        }
//                    }
//                    
//                    if(skip)continue;
                    
                    //

                    vertex.add(new int[]{ai, i, j, pk, ql, rx1, rx2, ry1, ry2, vertexNum});
                    
                    //Para debuguear
                    vertexInfo.put(vertexNum, new int[]{ai, i, j, pk, ql, rx1, rx2, ry1, ry2, vertexNum});
                    
                    //graph.addVertex(ai);
                    neighbors.put(vertexNum, new HashSet<>());

                    //System.out.println("[" + vertexNum + "]" + ai + ":" + i + "," + j + ":" + pk + "," + ql + ":  " + rx1 + "," + rx2 + " " + ry1 + "," + ry2);

                    vertexNum++;
                }

            }

        }

        for (int i = 0; i < vertex.size(); i++) {

            int v1[] = vertex.get(i);

            int ranki = v1[5];
            int rankj = v1[6];
            int rankk = v1[7];
            int rankl = v1[8];

            for (int j = i + 1; j < vertex.size(); j++) {

                boolean connectEdge = false;

                int v2[] = vertex.get(j);

                int ranks = v2[5];
                int rankt = v2[6];
                int ranku = v2[7];
                int rankv = v2[8];

                //Check type
                int c1 = ranks - ranki;
                int c2 = rankt - rankj;
                int c3 = ranku - rankk;
                int c4 = rankv - rankl;

                //Type-0
                if (((c1 * c2 >= 0) && (c3 * c4 >= 0)) && type == SIMILARITY_TYPE_0) {
                    connectEdge = true;
                }

                //Type-1
                if ((((c1 * c2) > 0 || (c1 == 0 && c2 == 0)) && ((c3 * c4) > 0 || (c3 == 0 && c4 == 0))) && type == SIMILARITY_TYPE_1) {
                    connectEdge = true;
                }

                //Type-2
                if ((c1 == c2 && c3 == c4) && type == SIMILARITY_TYPE_2) {
                    connectEdge = true;
                }

                if (connectEdge) {
                    int v1Num = v1[9];
                    int v2Num = v2[9];

                    Set<Integer> nV1 = neighbors.get(v1Num);
                    Set<Integer> nV2 = neighbors.get(v2Num);

                    if (nV1 == null) {
                        nV1 = new HashSet<>();
                    }
                    if (nV2 == null) {
                        nV2 = new HashSet<>();
                    }

                    nV1.add(v2Num);
                    nV2.add(v1Num);
                }

            }

        }

        ArrayList<Set<Integer>> maximalCliques = BronKerboschPivotClique.findMaxCliques(neighbors);

        //Calcular el procentaje de similaridad con base al numero de objetos
        //entre ambas escenas m n
        
        //https://www.geo.arizona.edu/Antevs/ecol438/simindex.html
        //Conjuntos para el Coeficiente de Sorensen-Dice
        // 2 |A U B| / |A| + |B|
        
              
        Set<Integer> clique = maximalCliques.get(0);
        float maxClique = clique.size();
        
        float similarityPercent = (2 * maxClique) / (m + n);

//        System.out.println("============");
//        System.out.println("pattern 1 M: "+m);
//        System.out.println("pattern 2 N: "+n);
//        System.out.println("2 * " + maxClique + "/"+m+"+"+n);        
//        System.out.println(similarityPercent);

//        System.out.println(clique);
//        
//        for (Integer v : clique) {     
//            int data[] = vertexInfo.get(v);
//            int classId = data[0];
//            int rx1 = data[5];
//            int ry1 = data[7];
//            int rx2 = data[6];
//            int ry2 = data[8];        
//            System.out.println("[" + v + "]" + classId + ":"+ rx1 + "," + ry1 + " - " + rx2 + "," + ry2);
//        }
        
        return similarityPercent;

    }

    
    /**
     * 
     * @param imageMatrix Matriz que se desea imprimir
     */
    
    public static void debugMatrix(ArrayList<Integer>[][] imageMatrix) {
        System.out.println("Matrix");
        for (int i = 0; i < imageMatrix.length; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {

                if(imageMatrix[i][j] == null){
                    System.out.print("[0]");
                    continue;
                }
                
                System.out.print("[");
                
                for (int k = 0; k < imageMatrix[i][j].size(); k++) {
                    
                    if (k > 0) {
                        System.out.print(":");
                    }
                    
                    System.out.print(imageMatrix[i][j].get(k));

                }
                System.out.print("]");

            }
            System.out.println("");
        }
    }
}
