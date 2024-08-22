/*
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.prc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.spike.TopDownParameters;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.storage.prc.PRCAffectiveStorageHandler;
import episodicv2.core.storage.prc.PRCStorageHandler;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class PRCProcess3Codelet extends Codelet {
    
    private MemoryObject prcProcess3SpikeMO;
    private Idea prcProcess3SpikeIdea;
    
    private MemoryObject vlpfcSpikeMO;
    private Idea vlpfcSpikeIdea;
    
    private byte[] data = null;
    
    private PRCStorageHandler storageHandler = PRCStorageHandler.getInstance(Configuration.LOAD_MEMORY);
    private PRCAffectiveStorageHandler affectStorageHandler = PRCAffectiveStorageHandler.getInstance(Configuration.LOAD_MEMORY);

    private EmotionalDecay emotionalDecay = null;
    private double affectIntensity = 0.0;
    private double affectiveValues[] = new double[]{0, 0};

    
    public PRCProcess3Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        prcProcess3SpikeMO = (MemoryObject) getInput(Configuration.PRC_PROCESS_3_SPIKE_MO);
        prcProcess3SpikeIdea = (Idea) prcProcess3SpikeMO.getI();
        data = (byte[]) prcProcess3SpikeIdea.get(Configuration.SPIKE_PRC_PROCESS_3_DATA_IDEA).getValue();
        
        vlpfcSpikeMO = (MemoryObject) getOutput(Configuration.VLPFC_SPIKE_MO);
        vlpfcSpikeIdea = (Idea) vlpfcSpikeMO.getI();
    }
    
    private void initComponents() {
        if (emotionalDecay == null) {
            emotionalDecay = new EmotionalDecay(this.getClass().getName());
            emotionalDecay.start();
        }
    }
    
    @Override
    public void proc() {
        

        try {

            initComponents();

            SpikeObject rawSpike = SpikeObject.fromBytes(data);

            if (rawSpike.getId() == SpikeType.REQUEST_OBJECT_RELATION) {

                SpikeObject<Integer> spike = rawSpike;

                affectIntensity = emotionalDecay.getActivation();

                ObjectRelations or = storageHandler.retrieveObjectRelations(spike.getObject(), affectIntensity, System.currentTimeMillis());

                //RECUPERA EL VALOR AFECTIVO ASIGNADO A LA CLASE DE OBJETO
                ObjectRelations objectAffect = affectStorageHandler.retrieveObjectByID(spike.getObject(), affectIntensity, System.currentTimeMillis(), false);

                if (objectAffect != null) {
                    or.setPositiveAffect(objectAffect.getPositiveAffect());
                    or.setNegativeAffect(objectAffect.getNegativeAffect());
                    //or.setAffect(objectAffect.getAffect());
                }

                SpikeObject<ObjectRelations> spikeRelations = new SpikeObject(SpikeType.RETRIEVED_OBJECT_RELATIONS_TOP_DOWN, or, 0);
                vlpfcSpikeIdea.setValue(spikeRelations.toBytes());
                vlpfcSpikeMO.setI(vlpfcSpikeIdea);
                
//                send(AreaNames.VLPFC, spikeRelations.toBytes());

            } else if (rawSpike.getId() == SpikeType.REQUEST_OBJECT_BY_AFFECT) {

                int request = (int) rawSpike.getObject();

                ObjectRelations affectiveObject = null;
                int returnedType = SpikeType.RETRIEVED_MOST_AFFECTIVE_OBJECT_TOP_DOWN;

                if (request == TopDownParameters.MAX_AFFECT) {

                    affectiveObject = affectStorageHandler.retrieveMostPositiveAffectiveObject();
                    returnedType = SpikeType.RETRIEVED_MOST_AFFECTIVE_OBJECT_TOP_DOWN;

                } else if (request == TopDownParameters.MIN_AFFECT) {

                    affectiveObject = affectStorageHandler.retrieveLessPositiveAffectiveObject();
                    returnedType = SpikeType.RETRIEVED_LESS_AFFECTIVE_OBJECT_TOP_DOWN;
                }

                //REVISAR EL AFFECT INTENSITY
                ObjectRelations or = storageHandler.retrieveObjectRelations(affectiveObject.getObjectId(), affectIntensity, System.currentTimeMillis());

                affectiveObject.setRelations(or.getRelations());

                SpikeObject<ObjectRelations> spikeRelations = new SpikeObject(returnedType, affectiveObject, 0);
//                send(AreaNames.VLPFC, spikeRelations.toBytes());
                vlpfcSpikeIdea.setValue(spikeRelations.toBytes());
                vlpfcSpikeMO.setI(vlpfcSpikeIdea);
            } else if (rawSpike.getId() == SpikeType.AFFECTIVE_VALUE) {

                SpikeObject<double[]> affectSpike = rawSpike;

                affectiveValues = affectSpike.getObject();

                affectIntensity = ActivationFunctions.affect(affectiveValues[0], affectiveValues[1]);

                emotionalDecay.setNewActivation(affectiveValues[0], affectiveValues[1]);

            } else {

                SpikeObject<ArrayList<CObject>> spike = SpikeObject.fromBytes(data);

                affectIntensity = emotionalDecay.getActivation();

                for (CObject co : spike.getObject()) {

                    ObjectRelations or = storageHandler.retrieveObjectRelations(co.getClassId(), affectIntensity, System.currentTimeMillis());

                    //RECUPERA EL VALOR AFECTIVO ASIGNADO A LA CLASE DE OBJETO
                    ObjectRelations objectAffect = affectStorageHandler.retrieveObjectByID(co.getClassId(), affectIntensity, System.currentTimeMillis(), true);

                    if (objectAffect != null) {
                        or.setPositiveAffect(objectAffect.getPositiveAffect());
                        or.setNegativeAffect(objectAffect.getNegativeAffect());
                        //or.setAffect(objectAffect.getAffect());
                    }

                    SpikeObject<ObjectRelations> spikeRelations = new SpikeObject(SpikeType.OBJECT_RELATIONS, or, spike.getTime());
//                    send(AreaNames.VLPFC, spikeRelations.toBytes());
                    vlpfcSpikeIdea.setValue(spikeRelations.toBytes());
                    vlpfcSpikeMO.setI(vlpfcSpikeIdea);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
