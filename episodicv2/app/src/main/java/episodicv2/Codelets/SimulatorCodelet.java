/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import episodicv2.Connection.BigNodeBridge;
import static episodicv2.configuration.Configuration.*;

/**
 *
 * @author karenlima
 */
public class SimulatorCodelet extends Codelet {
    
    private BigNodeBridge nodeBridge;
    
    public SimulatorCodelet() {
        nodeBridge = new BigNodeBridge(BRIDGE_PORT_ITC);
        nodeBridge.start();
    }

    @Override
    public void accessMemoryObjects() {
        
    }
    
    @Override
    public void proc() {
        
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
