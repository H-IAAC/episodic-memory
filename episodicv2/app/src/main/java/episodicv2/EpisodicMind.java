/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
//import support.MindView;

/**
 *
 * @author karenlima
 */
public class EpisodicMind extends Mind {
    
    public EpisodicMind(){
        super();
        
        //Declare Memory Objects
        MemoryObject imagePerceptedMO;
        
        imagePerceptedMO=super.createMemoryObject("IMAGE_PERCEPTED");
    
                
        // Create and populate MindViewer
//        MindView mv = new MindView("MindView");
//        mv.addMO(imagePerceptedaaMO);
        
        //Create Sensor Codelets
        Codelet vision=new Vision();
        vision.addOutput(imagePerceptedMO);
        insertCodelet(vision);
        
        start();
               
    }
}
