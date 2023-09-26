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
    
    private static Map<Integer, ConcurrentHashMap<Integer, Idea>> midTermMemoryObjectRelations = new ConcurrentHashMap<>();;
    
    //CARGA LAS ESCENAS EXISTENTES
    private static Map<Integer, Idea> midTermMemoryObjectsByID = new ConcurrentHashMap<>();

    public PRCProcess2Codelet() {
        setIsMemoryObserver(true);
        
    }
    
    @Override
    public void accessMemoryObjects() {
        System.out.println("[PRC2] Executing accessMemoryObjects PRCProcess2Codelet");
        recognizedObjectsSpikeMO = (MemoryObject) getInput(RECOGNIZED_OBJECTS_SPIKE_MO);
        recognizedObjectsSpikeIdea = (Idea) recognizedObjectsSpikeMO.getI();
        
        pRCMidTermMemoryObjectRelationsMO = (MemoryObject) getOutput(PRC_MID_TERM_OBJECT_RELATIONS_MO);
        pRCMidTermMemoryObjectRelationsIdea = (Idea) pRCMidTermMemoryObjectRelationsMO.getI();
    }
    
    @Override
    public void proc() {
        System.out.println("[PRC2] Executing proc PRCProcess2Codelet");
        initComponents();
        affectIntensity = emotionalDecay.getActivation();

        ArrayList<Idea> objects = (ArrayList<Idea>) recognizedObjectsSpikeIdea.get(OBJECTS_IDEA).getValue();
        Integer currentFrame = (Integer) recognizedObjectsSpikeIdea.get(CURRENT_FRAME_IDEA).getValue();
        //CREATES THE RELATIONS BETWEEN THE OBJECTS
        ArrayList<Idea> relations = objectsAssociation.addObjects(objects);
        addVertexArray(relations, affectIntensity);
        //ASSIGNS AN AFFECTIVE VALUE TO THE CURRENT OBJECTS
        for (Idea cObject : objects) {
            Integer objectId = (Integer) cObject.get(ID_IDEA).getValue();
            Idea object = createObjectRelationsIdea(objectId, emotionalDecay.getPositiveActivation(), emotionalDecay.getNegativeActivation(), currentFrame);
            storeObject(object);
        }
        Idea midTermMemoryObjectRelationsIdea = new Idea(MID_TERM_MEMORY_OBJECT_RELATIONS, midTermMemoryObjectRelations,"Property", 1);
        Idea midTermMemoryObjectRelationsByIdIdea = new Idea(MID_TERM_MEMORY_OBJECT_RELATIONS_BY_ID, midTermMemoryObjectsByID,"Property", 1);
        pRCMidTermMemoryObjectRelationsIdea.setL(new ArrayList<>());
        pRCMidTermMemoryObjectRelationsIdea.add(midTermMemoryObjectRelationsIdea);
        pRCMidTermMemoryObjectRelationsIdea.add(midTermMemoryObjectRelationsByIdIdea);
        pRCMidTermMemoryObjectRelationsMO.setI(pRCMidTermMemoryObjectRelationsIdea);
        
        System.out.println("pRCMidTermMemoryObjectRelationsIdea content");
        System.out.println(pRCMidTermMemoryObjectRelationsMO.getI());
        Idea testeIdea = (Idea) pRCMidTermMemoryObjectRelationsMO.getI();
        System.out.println(testeIdea.getL());
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
    
    private Idea createObjectRelationsIdea(Integer objectId, double positiveAffect, double negativeAffect, Integer time) {
        Idea objectRelationsIdea = new Idea(OBJECT_RELATIONS_IDEA, null,"Property", 1);
        Idea objectIdIdea = new Idea(ID_IDEA, objectId,"Property", 1);
        Idea positiveAffectIdea = new Idea(POSITIVE_AFFECT_IDEA, positiveAffect,"Property", 1);
        Idea negativeAffectIdea = new Idea(NEGATIVE_AFFECT_IDEA, negativeAffect,"Property", 1);
        Idea activationIdea = new Idea(ACTIVATION_IDEA, 0.5,"Property", 1);
        Idea timeIdea = new Idea(TIME_IDEA, time,"Property", 1);
        Idea timestampIdea = new Idea(TIMESTAMP_IDEA, System.currentTimeMillis(),"Property", 1);
        Idea repetitionsIdea = new Idea(REPETITIONS_IDEA, 0,"Property", 1);

        objectRelationsIdea.add(objectIdIdea);
        objectRelationsIdea.add(positiveAffectIdea);
        objectRelationsIdea.add(negativeAffectIdea);
        objectRelationsIdea.add(activationIdea);
        objectRelationsIdea.add(timeIdea);
        objectRelationsIdea.add(timestampIdea);
        objectRelationsIdea.add(repetitionsIdea);
        objectRelationsIdea = updateActivationWithAffect(objectRelationsIdea, ActivationFunctions.affect(positiveAffect, negativeAffect));
        return objectRelationsIdea;
    }
    
    /**
     *
     * @param object
     */
    public void storeObject(Idea object) {
        Integer objectId = (Integer) object.get(ID_IDEA).getValue();
        Double positiveAffect = (Double) object.get(POSITIVE_AFFECT_IDEA).getValue();
        Double negativeAffect = (Double) object.get(NEGATIVE_AFFECT_IDEA).getValue();
        Long timeStamp = (Long) object.get(TIMESTAMP_IDEA).getValue();
        

        if (!midTermMemoryObjectsByID.containsKey(objectId)) {

            midTermMemoryObjectsByID.put(objectId, object);

        } else {

            Idea existingObject = midTermMemoryObjectsByID.get(objectId);

            //existingObject.updateActivationWithAffect(object.getAffect());
            existingObject = incrementRepetitions(existingObject, ActivationFunctions.affect(positiveAffect, negativeAffect));
            existingObject = updatePositiveAffect(existingObject, positiveAffect);
            existingObject = updateNegativeAffect(existingObject, negativeAffect);
            //existingObject.updateAffect(object.getAffect());
            existingObject.get(TIMESTAMP_IDEA).setValue(timeStamp);
        }

    }
    
    public Idea updatePositiveAffect(Idea idea, double affect) {
        Double positiveAffect = (Double) idea.get(POSITIVE_AFFECT_IDEA).getValue();
        positiveAffect = (positiveAffect + affect) / 2;
        idea.get(POSITIVE_AFFECT_IDEA).setValue(positiveAffect);
        
        return idea;
    }
    
    public Idea updateNegativeAffect(Idea idea, double affect) {
        Double negativeAfffect = (Double) idea.get(NEGATIVE_AFFECT_IDEA).getValue();
        negativeAfffect = (negativeAfffect + affect) / 2;
        idea.get(NEGATIVE_AFFECT_IDEA).setValue(negativeAfffect);
        
        return idea;
    }
    
    public Idea updateActivationWithAffect(Idea idea, double affectIntensity) {
        Integer repetitions = (Integer) idea.get(REPETITIONS_IDEA).getValue();
        repetitions++;

        double scale = SIGMOID_SCALE;
        double alpha = ALPHA_MEMORY_INC_RELEVANCE;
        double beta = BETA_AFFECT_RELEVANCE;

        double weight = ActivationFunctions.weight(repetitions, scale, affectIntensity, alpha, beta);

        double currentActivation = ActivationFunctions.sigmoid(weight);
        Double activation = (Double) idea.get(ACTIVATION_IDEA).getValue();
        activation = (activation + currentActivation) / 2.0;
        idea.get(ACTIVATION_IDEA).setValue(activation);
        
        return idea;
    }
    
}
