/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.Codelets.storage.vlpfc;

import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.storage.ActivationFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author luis_
 */
public class ItemWMQueue extends TimerTask {

    private Timer timer;
    private int time;
    private int maxSize = 4;
    private LinkedList<CObject> items;
    private STMChangesListener listener;

    private static ItemWMQueue instance = null;

    private ItemWMQueue() {
        items = new LinkedList<>();
        timer = new Timer();
        //timer.scheduleAtFixedRate(this, 0, Configuration.SHORT_TERM_DECAY_INTERVAL);
    }

    private ItemWMQueue(int maxSize) {
        this.maxSize = maxSize;
        items = new LinkedList<>();
        timer = new Timer();
        //timer.scheduleAtFixedRate(this, 0, Configuration.SHORT_TERM_DECAY_INTERVAL);
    }

    public static ItemWMQueue getInstance() {

        if (instance == null) {
            instance = new ItemWMQueue();
        }

        return instance;
    }

    public static ItemWMQueue getInstance(int maxSize) {

        if (instance == null) {
            instance = new ItemWMQueue(maxSize);
        }

        return instance;
    }

    /**
     * CALLBACK que notifica cuando hubo un cambio en el buffer de memoria
     *
     * @return
     */
    public STMChangesListener getListener() {
        return listener;
    }

    public void setListener(STMChangesListener listener) {
        this.listener = listener;
    }

    /**
     * *
     * FALTA CREAR UNA CLASE WM ITEM PARA IR REFORZANDO LOS ITEMS USADOS
     */
    public void addItem(CObject item, int itemTime) {

        //Solo son maximo 7 items o 4
        //Si quieres optimizarlo pues sobrecarga el contains del CObject, a mi me dio mucha hueva
        boolean exists = false;
        for (CObject co : items) {
            if (co.getClassId() == item.getClassId() && co.getPreId() == item.getPreId()) {
                exists = true;
                co.setShortTermActivation(1.0);
                co.setShortTermMemoryTime(0);
                break;
            }
        }

        if (!exists) {
            if (items.size() >= maxSize) { //Si es menor, si puede agregar sino elimita el ultimo 
                //AGREGAR LA COMPROBACION DE ACTIVACION PARA ELIMINAR EL MENOS ACTIVO
                items.remove();
            }
            item.setShortTermActivation(1.0);
            item.setShortTermMemoryTime(0);
            items.add(item);
        }

    }

    /**
     * Busca secuencialmente los objetos, no hay un break al encontrarlo porque
     * la memoria no funciona asi. La memoria es serial exhaustive, depende del
     * tamaño del span pero no de la posicion del objeto
     *
     * @param classId
     * @return
     */
    public CObject getItem(int classId) {

        CObject item = null;

        for (CObject co : items) {
            if (co.getClassId() == classId) {
                co.setShortTermActivation(1.0);
                co.setShortTermMemoryTime(0);
                item = co;
            }
        }

        return item;
    }

    public ArrayList<CObject> getItems() {
        return new ArrayList<>(items);
    }

    //Verifica si puede reemplazar a un item deacuerdo a relevancia(si esta siendo usado)
    public boolean canReplace(CObject item) {
        return true;
    }

    @Override
    public void run() {

        Iterator<CObject> iterator = items.iterator();

        while (iterator.hasNext()) {
            CObject co = iterator.next();
            co.setShortTermMemoryTime(co.getShortTermMemoryTime() + 1);

            double activation = ActivationFunctions.altmannSchunn(co.getShortTermMemoryTime(), Configuration.ALTMAN_FUNCTION_SCALE);

            co.setShortTermActivation(activation);

            //System.out.println(activation);

            if (co.getShortTermActivation() < 0.0) {
                
                iterator.remove();
                listener.itemDeleted();
               
            }
        }
        
        
    }

}
