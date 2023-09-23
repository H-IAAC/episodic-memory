/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.configuration.Configuration;

/**
 *
 * @author karenlima
 */
public class ImageCodelet extends Codelet {
    
    MemoryObject imageReceivedPathMO;
    Idea imageReceivedPathIdea;
    
    Integer imageFlag = 1;
    
    @Override
    public void accessMemoryObjects() {
        imageReceivedPathMO = (MemoryObject) getOutput(Configuration.IMAGE_RECEIVED_PATH_MO);
        imageReceivedPathIdea = (Idea) imageReceivedPathMO.getI();
        
    }
    
    @Override
    public void proc() {
        switch (imageFlag) {
            case 1:
                System.out.printf("Pegando a imagem simulador ");
                imageReceivedPathIdea.setValue("/yolo/files/simu.jpeg");
                imageFlag=1;
                break;
            case 2:
                System.out.printf("Pegando a imagem do cachorro ");
                imageReceivedPathIdea.setValue("/yolo/files/lab.jpeg");
                imageFlag+=1;
                break;
                
            case 3:
                System.out.printf("Pegando a imagem do lab ");
                imageReceivedPathIdea.setValue("/yolo/files/imagemteste.jpeg");
                imageFlag+=1;
                break;
            
            case 4:
                System.out.printf("Pegando a imagem da praia");
                imageReceivedPathIdea.setValue("/yolo/files/imagemteste.jpeg");
                imageFlag = 1;
                break;
            default:
                throw new AssertionError();
        }
        imageReceivedPathMO.setI(imageReceivedPathIdea);
        
//        imageReceivedPathIdea = new Idea("imageReceivedPathIdea","/yolo/files/imagemteste.jpeg", "Property", 3);
      
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
