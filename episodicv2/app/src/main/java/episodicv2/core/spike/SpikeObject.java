/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.spike;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Luis Martin
 * @param <O>
 */
public class SpikeObject<O extends Serializable> implements Serializable {

    /**
     *IDENTIFICA AL SPIKE COMO UN SPIKE DE TIPO BOTTOM UP
     */
    public static final int BOTTOM_UP_PROCESSING = 1;

    /**
     *IDENTIFICA AL SPIKE COMO UN SPIKE DE TIPO TOP DOWN
     */
    public static final int TOP_DOWN_PROCESSING = 2;
    private int id;
    private String name = this.getClass().getTypeName();
    private int time;
    private int processingFlow = BOTTOM_UP_PROCESSING;
    private O object;

    public SpikeObject(int id, O object, int time) {
        this.id = id;
        this.object = object;
        this.time = time;
    }

    //

    /**
     *DECODIFICA UN ARREGLO DE BYTES A UN OBJETO DE TIPO SpikeObject
     * @param spikeBytes
     * @return
     */
    public static SpikeObject fromBytes(byte spikeBytes[]) {

        ByteArrayInputStream bis = new ByteArrayInputStream(spikeBytes);
        ObjectInput in = null;

        try {

            in = new ObjectInputStream(bis);
            Object object = in.readObject();
            SpikeObject spike = (SpikeObject) object;

            return spike;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     *CONVIERTE EL OBJETO SpikeObject en un arreglo de bytes para ser enviados
     * @return
     */
    public byte[] toBytes() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {

            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();

            byte[] spikeBytes = bos.toByteArray();

            return spikeBytes;

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    //
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public O getObject() {
        return object;
    }

    public void setObject(O object) {
        this.object = object;
    }

    public int getProcessingFlow() {
        return processingFlow;
    }

    public void setProcessingFlow(int processingFlow) {
        this.processingFlow = processingFlow;
    }
}
