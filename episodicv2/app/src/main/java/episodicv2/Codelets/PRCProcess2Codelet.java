/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import static episodicv2.configuration.Configuration.*;
import episodicv2.emotions.ActivationFunctions;
import episodicv2.emotions.EmotionalDecay;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import objectsassociation.ObjectsAssociation;

/**
 *
 * @author karenlima
 */
public class PRCProcess2Codelet extends Codelet {
    
    MemoryObject recognizedObjectsSpikeMO;
    Idea recognizedObjectsSpikeIdea;
    
    MemoryObject pRCMidTermMemoryObjectRelationsMO;
    Idea pRCMidTermMemoryObjectRelationsIdea;
    
    private EmotionalDecay emotionalDecay = null;
    
    private ObjectsAssociation objectsAssociation = ObjectsAssociation.getInstance();
    private double affectIntensity = 0.0;
    
    private static Map<Integer, ConcurrentHashMap<Integer, Idea>> midTermMemoryObjectRelations;

    public PRCProcess2Codelet() {
        midTermMemoryObjectRelations = new ConcurrentHashMap<>();
        setIsMemoryObserver(true);
        
    }
    @Override
    public void accessMemoryObjects() {
        //output midTermMemoryObjectRelations com as infos de PRCstoragehandler e Affective storage
        //output 
        recognizedObjectsSpikeMO = (MemoryObject) getInput(RECOGNIZED_OBJECTS_SPIKE_MO);
        recognizedObjectsSpikeIdea = (Idea) recognizedObjectsSpikeMO.getI();
        
        pRCMidTermMemoryObjectRelationsMO = (MemoryObject) getOutput(PRC_MID_TERM_OBJECT_RELATIONS_MO);
        pRCMidTermMemoryObjectRelationsIdea = (Idea) pRCMidTermMemoryObjectRelationsMO.getI();
    }
    
    @Override
    public void proc() {
        initComponents();
        affectIntensity = emotionalDecay.getActivation();

        ArrayList<Idea> objects = (ArrayList<Idea>) recognizedObjectsSpikeIdea.get(OBJECTS_IDEA).getValue();
        
        //CREATES THE RELATIONS BETWEEN THE OBJECTS
        ArrayList<Idea> relations = objectsAssociation.addObjects(objects);
        addVertexArray(relations, affectIntensity);
        //storageHandler.addVertex(relations, affectIntensity);
        //ASSIGNS AN AFFECTIVE VALUE TO THE CURRENT OBJECTS
//        for (CObject cObject : spike.getObject()) {
//
//            ObjectRelations object = new ObjectRelations(cObject.getClassId(), emotionalDecay.getPositiveActivation(), emotionalDecay.getNegativeActivation(), spike.getTime());
//
//            affectStorageHandler.storeObject(object);
//        }
    }
    
    @Override
    public void calculateActivation(){
        
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
    
    /**
     * *
     * Add
     *
     * @param relations
     * @param affectIntensity
     */
    public void addVertexArray(ArrayList<Idea> relations, double affectIntensity) {
        for (Idea or : relations) {
            addVertex(or, affectIntensity);
        }
    }
    
    public void addVertex(Idea edge, double affectIntensity) {
        //TODO: acho que isso aqui não está fazendo nada no código original, verificar

        Integer source = (Integer) edge.get(OBJECT_1_ID_IDEA).getValue();
        Integer destination = (Integer) edge.get(OBJECT_2_ID_IDEA).getValue();
        Integer edgeTime = (Integer) edge.get(TIME_IDEA).getValue();
        long timeStamp = System.currentTimeMillis();

        //No se guardan las relaciones incompletas
        if (source == 0 || destination == 0) {
            return;
        }

        createIfNotExists(source);
        createIfNotExists(destination);

        ConcurrentHashMap<Integer, Idea> sourceRelations = midTermMemoryObjectRelations.get(source);
        ConcurrentHashMap<Integer, Idea> destinationRelations = midTermMemoryObjectRelations.get(destination);

        Idea sourceRelation = sourceRelations.get(destination);
        Idea destinationRelation = destinationRelations.get(source);

        if (sourceRelation == null) {
            sourceRelations.put(destination, createObjectRelationIdea(destination, source, edgeTime));
        } else {
            sourceRelation = incrementRepetitions(sourceRelation, affectIntensity);
            sourceRelation.get(TIME_IDEA).setValue(edgeTime);
            sourceRelation.get(TIMESTAMP_IDEA).setValue(timeStamp);
            sourceRelation.get(UPDATED_IDEA).setValue(true);

            //reinforceRelation(sourceRelation);
        }

        if (destinationRelation == null) {
            destinationRelations.put(source, createObjectRelationIdea(source, destination, edgeTime));
        } else {
            destinationRelation = incrementRepetitions(destinationRelation, affectIntensity);
            destinationRelation.get(TIME_IDEA).setValue(edgeTime);
            destinationRelation.get(TIMESTAMP_IDEA).setValue(timeStamp);
            destinationRelation.get(UPDATED_IDEA).setValue(true);
            
            //reinforceRelation(destinationRelation);
        }

    }
    
    
    public Idea incrementRepetitions(Idea idea, double affectIntensity) {
        Integer repetionsValue = (Integer) idea.get(REPETITIONS_IDEA).getValue();
        repetionsValue++;
        idea.get(REPETITIONS_IDEA).setValue(repetionsValue);
     
        double scale = SIGMOID_SCALE;
        double affect = affectIntensity;
        double alpha = ALPHA_MEMORY_INC_RELEVANCE;
        double beta = BETA_AFFECT_RELEVANCE;       
        
        double weight = ActivationFunctions.weight(repetionsValue, scale, affect, alpha, beta);
  
        double currentActivation = ActivationFunctions.sigmoid(weight);
        Double activationValue = (Double) idea.get(ACTIVATION_IDEA).getValue();
        activationValue  = (activationValue + currentActivation) / 2.0;
        idea.get(ACTIVATION_IDEA).setValue(activationValue);
        
        return idea;
    }
    
    private void createIfNotExists(int key) {
        if (!midTermMemoryObjectRelations.containsKey(key)) {
            midTermMemoryObjectRelations.put(key, new ConcurrentHashMap<Integer, Idea>());
        }
    }
    
    private Idea createObjectRelationIdea(Integer cObject1classIdea, Integer cObject2classIdea, Integer time) {
        Idea objectRelationIdea = new Idea(OBJECT_RELATION_IDEA, null,"Property", 1);
        Idea object1IdIdea = new Idea(OBJECT_1_ID_IDEA, cObject1classIdea,"Property", 1);
        Idea object2IdIdea = new Idea(OBJECT_2_ID_IDEA, cObject2classIdea,"Property", 1);
        Idea timeIdea = new Idea(TIME_IDEA, time,"Property", 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, 0.5,"Property", 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA, System.currentTimeMillis(),"Property", 1);
        Idea recentIdea = new Idea(RECENT_IDEA, true,"Property", 1);
        Idea updatedIdea = new Idea(UPDATED_IDEA, false,"Property", 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, 0,"Property", 1);

        objectRelationIdea.add(object1IdIdea);
        objectRelationIdea.add(object2IdIdea);
        objectRelationIdea.add(timeIdea);
        objectRelationIdea.add(activationIdea);
        objectRelationIdea.add(timestampIdea);
        objectRelationIdea.add(recentIdea);
        objectRelationIdea.add(updatedIdea);
        objectRelationIdea.add(repetitionsIdea);
        return objectRelationIdea;
    }
    
}
