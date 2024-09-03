/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.itc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.CObjectList;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.utils.Utils2DString;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class ITCCodelet extends Codelet {
    
    private MemoryObject itcSpikeMO;
    private Idea itcSpikeIdea;
    private MemoryObject itcProcess1SpikeMO;
    private Idea itcProcess1SpikeIdea;
    
    private MemoryObject prcSpikeMO;
    private Idea prcSpikeIdea;
    
    
    private byte[] dataSpike = null;
    
    private String dataString = null;
    
    
    public ITCCodelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        itcSpikeMO = (MemoryObject) getInput(Configuration.ITC_SPIKE_MO);
        itcSpikeIdea = (Idea) itcSpikeMO.getI();
        
        Object dataObject = itcSpikeIdea.getValue();
        dataSpike = null;
        dataString = null;
        if (dataObject instanceof byte[]) {
            dataSpike = (byte[]) dataObject;
            System.out.println("Recebeu um spike");

        } else if (dataObject instanceof String) {
            dataString = (String) dataObject;
            System.out.println("Recebeu uma string");
        } else {
            // Caso o tipo não seja o esperado
            System.out.println("Tipo de dado inesperado");
        }
                
        itcProcess1SpikeMO = (MemoryObject) getOutput(Configuration.ITC_PROCESS_1_SPIKE_MO);
        itcProcess1SpikeIdea = (Idea) itcProcess1SpikeMO.getI();
        
        prcSpikeMO = (MemoryObject) getOutput(Configuration.PRC_SPIKE_MO);
        prcSpikeIdea = (Idea) prcSpikeMO.getI();
        
    }
    
    @Override
    public void proc() {
        System.out.println("Entrou o proc de ITC Codelet : ");
        
        if (dataSpike != null) {
            
            SpikeObject spike = SpikeObject.fromBytes(dataSpike);
            System.out.println("Processando ITC Codelet : " + spike.getName());
            switch (spike.getId()) {
                case SpikeType.REQUEST_OBJECT:
                    //SEARCH IN MTM
                    itcProcess1SpikeIdea.setValue(dataSpike);
                    itcProcess1SpikeMO.setI(itcProcess1SpikeIdea);
                    //send(AreaNames.ITCProcess1, data);

                    //SEARCH IN LTM
                    //send(AreaNames.ITCProcess2, data);
                    break;
            }
        } else if (dataString != null) {
            //received from connection the image
            //Convert the json to spike
            CObjectList encapsulatedObjects = Utils2DString.getRecognizedObjectsList(dataString);

            if (encapsulatedObjects != null) {

                SpikeObject<ArrayList<CObject>> spike = new SpikeObject(SpikeType.RECOGNIZED_OBJECTS, encapsulatedObjects.getObjects(), encapsulatedObjects.getTime());
                System.out.println("Processando ITC Codelet dataString : " + spike.getName());
                
                prcSpikeIdea.setValue(spike.toBytes());
                System.out.println("Spike: " + spike.toBytes());
                prcSpikeMO.setI(prcSpikeIdea);
//                send(AreaNames.PRC, spike.toBytes());
            }
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
