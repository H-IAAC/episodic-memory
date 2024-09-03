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
import episodicv2.core.entities.ObjectRelation;
import episodicv2.core.entities.ObjectRelations;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.storage.ActivationFunctions;
import episodicv2.core.storage.prc.ObjectsAssociation;
import episodicv2.core.storage.prc.PRCAffectiveStorageHandler;
import episodicv2.core.storage.prc.PRCStorageHandler;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class PRCProcess2Codelet extends Codelet {
    
    private MemoryObject prcProcess2SpikeMO;
    private Idea prcProcess2SpikeIdea;
    
    private byte[] data = null;
    
    private ObjectsAssociation objectsAssociation = ObjectsAssociation.getInstance();
    private PRCStorageHandler storageHandler = PRCStorageHandler.getInstance(Configuration.LOAD_MEMORY);
    private PRCAffectiveStorageHandler affectStorageHandler = PRCAffectiveStorageHandler.getInstance(Configuration.LOAD_MEMORY);
    private EmotionalDecay emotionalDecay = null;

    private double affectIntensity = 0.0;
    private double affectiveValues[] = new double[]{0, 0};
    
    public PRCProcess2Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        prcProcess2SpikeMO = (MemoryObject) getInput(Configuration.PRC_PROCESS_2_SPIKE_MO);
        prcProcess2SpikeIdea = (Idea) prcProcess2SpikeMO.getI();
        data = (byte[]) prcProcess2SpikeIdea.getValue();
        
    }
    
     /**
     * ESTA SOLUCION ES PORQUE EL MIDDLEWARE LLAMA DOS VECES AL CONSTRUCTOR DE
     * LA MISMA CLASE (CREANDO DOS INTANCIAS), PERO SOLO LLAMA UNA VEZ AL METODO
     * INIT (DE LA PRIMERA INSTANCIA) PERO EN EJECUCION USA LA SEGUNDA
     * (IGNORANDO LO QUE SE HAYA INICIALIZADO EN LA SEGUNDA) SI SE INICIALIZA
     * ALGO EN EL CONSTRUCTOR (COMO UN TIMER O UNA VENTANA) CREA DOS INSTANCIAS
     */
    private void initComponents() {
        if (emotionalDecay == null) {
            emotionalDecay = new EmotionalDecay(this.getClass().getName());
            emotionalDecay.start();
        }
    }
    
    @Override
    public void proc() {

        initComponents();

        SpikeObject rawSpike = SpikeObject.fromBytes(data);

        if (rawSpike.getId() == SpikeType.AFFECTIVE_VALUE) {

            SpikeObject<double[]> affectSpike = rawSpike;

            affectiveValues = affectSpike.getObject();

            affectIntensity = ActivationFunctions.affect(affectiveValues[0], affectiveValues[1]);

            emotionalDecay.setNewActivation(affectiveValues[0], affectiveValues[1]);

        } else {

            SpikeObject<ArrayList<CObject>> spike = SpikeObject.fromBytes(data);

            affectIntensity = emotionalDecay.getActivation();

            //CREATES THE RELATIONS BETWEEN THE OBJECTS
            ArrayList<ObjectRelation> relations = objectsAssociation.addObjects(spike.getObject());

            storageHandler.addVertex(relations, affectIntensity);

            //ASSIGNS AN AFFECTIVE VALUE TO THE CURRENT OBJECTS
            for (CObject cObject : spike.getObject()) {

                ObjectRelations object = new ObjectRelations(cObject.getClassId(), emotionalDecay.getPositiveActivation(), emotionalDecay.getNegativeActivation(), spike.getTime());

                affectStorageHandler.storeObject(object);
            }

        }

        /*
        SimpleLogger.log(this, "Relations");
        
        for (ObjectRelation object : relations) {
            SimpleLogger.log(this, object.toString());
        }*/
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
