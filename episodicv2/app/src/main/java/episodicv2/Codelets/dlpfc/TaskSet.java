/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.Codelets.dlpfc;

import java.io.Serializable;

/**
 *
 * @author Luis Martin
 */
public class TaskSet implements Serializable {

    public static final int ALLOW_BOTTOM_UP = 1;
    public static final int ALLOW_TOP_DOWN = 2;

    private int allowedProcessingFlow = 0;
    private String allowedScenePattern = "";
    private int allowedObjectRelation = 0;
    //Can be added pair of relations
    //Can be added additional features like shapes, colors, etc

    public TaskSet() {

    }

    public int getAllowedProcessingFlow() {
        return allowedProcessingFlow;
    }

    public void setAllowedProcessingFlow(int allowedProcessingFlow) {
        this.allowedProcessingFlow = allowedProcessingFlow;
    }

    public String getAllowedScenePattern() {
        return allowedScenePattern;
    }

    public void setAllowedScenePattern(String allowedScenePattern) {
        this.allowedScenePattern = allowedScenePattern;
    }

    public int getAllowedObjectRelation() {
        return allowedObjectRelation;
    }

    public void setAllowedObjectRelation(int allowedObjectRelation) {
        this.allowedObjectRelation = allowedObjectRelation;
    }

}
