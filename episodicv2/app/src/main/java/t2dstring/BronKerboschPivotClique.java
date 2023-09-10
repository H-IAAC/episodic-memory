/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t2dstring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author karenlima
 */
public class BronKerboschPivotClique {
    
    private static void bronKerbosch(Set<Integer> p, Set<Integer> r, Set<Integer> x, HashMap<Integer, Set<Integer>> n, ArrayList<Set<Integer>> maximalCliques) {
        //System.out.println("p: "+p+" r: "+r+" x: "+x);

        Set<Integer> pUx = new HashSet<>(p);
        pUx.addAll(x);

        if (pUx.isEmpty()) {
            //System.out.println("Maximal clique");
            //System.out.println(r);
            maximalCliques.add(r);
        }

        //Find the vertex with most neigbhors
        int max = -1;
        int pivot = -1;

        for (Integer v : pUx) {
            int nSize = n.get(v).size();
            if (max < nSize) {
                max = nSize;
                pivot = v;
            }
        }

        Set<Integer> nbrs = new HashSet<>();

        if (!pUx.isEmpty()) {
            nbrs = n.get(pivot);
        }

        //System.out.println("Pivot is "+pivot);
        Set<Integer> pSubn = new HashSet<>(p);
        pSubn.removeAll(nbrs);

        Iterator<Integer> iterator = pSubn.iterator();

        while (iterator.hasNext()) {

            Integer vertex = iterator.next();

            Set<Integer> nVertex = n.get(vertex);

            Set<Integer> pIn = new HashSet<>(p);
            pIn.retainAll(nVertex);

            Set<Integer> rUv = new HashSet<>(r);
            rUv.add(vertex);

            Set<Integer> xIn = new HashSet<>(x);
            xIn.retainAll(nVertex);

            bronKerbosch(pIn, rUv, xIn, n, maximalCliques);

            iterator.remove();
            x.add(vertex);
        }

    }

    public static ArrayList<Set<Integer>> findMaxCliques(HashMap<Integer, Set<Integer>> graph) {

        Set<Integer> r = new HashSet<>();
        Set<Integer> p = graph.keySet();
        Set<Integer> x = new HashSet<>();

        ArrayList<Set<Integer>> maximalCliques = new ArrayList<>();
        
        bronKerbosch(p, r, x, graph, maximalCliques);

        Collections.sort(maximalCliques, new Comparator<Set<Integer>>(){
            @Override
            public int compare(Set<Integer> o1, Set<Integer> o2) {
                return o2.size() - o1.size();
            }
        });
                
        return maximalCliques;

    }
}
