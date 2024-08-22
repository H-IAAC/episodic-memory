/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.model.phc;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.t2string.OccupancyGrid;
import episodicv2.core.t2string.T2DString;
import java.util.ArrayList;

/**
 *
 * @author karenlima
 */
public class PHCProcess1Codelet extends Codelet {
    
    private MemoryObject phcProcess1SpikeMO;
    private Idea phcProcess1SpikeIdea;

    private MemoryObject encSpikeMO;
    private Idea encSpikeIdea;    
    
    private byte[] data = null;
    
    public PHCProcess1Codelet() {
        setIsMemoryObserver(true);
    }

    @Override
    public void accessMemoryObjects() {
        phcProcess1SpikeMO = (MemoryObject) getInput(Configuration.PHC_PROCESS_1_SPIKE_MO);
        phcProcess1SpikeIdea = (Idea) phcProcess1SpikeMO.getI();
        data = (byte[]) phcProcess1SpikeIdea.get(Configuration.SPIKE_PHC_PROCESS_1_DATA_IDEA).getValue();
        
        encSpikeMO = (MemoryObject) getOutput(Configuration.ENC_SPIKE_MO);
        encSpikeIdea = (Idea) encSpikeMO.getI();
    }
    
    @Override
    public void proc() {
        

        try {
            SpikeObject<ArrayList<CObject>> spike = SpikeObject.fromBytes(data);

            ArrayList<CObject> objects = spike.getObject();

            //Creates the occupancy grid with the objects
            OccupancyGrid occupancyGrid = T2DString.createOccupancyGrid(
                    objects,
                    Configuration.IMAGE_WIDTH,
                    Configuration.IMAGE_HEIGHT,
                    Configuration.GRID_COLUMNS_X,
                    Configuration.GRID_ROWS_Y);

            //Creates the 2D String pattern
            String pattern = T2DString.create2DStringPattern(occupancyGrid,
                    Configuration.IMAGE_WIDTH,
                    Configuration.IMAGE_HEIGHT,
                    Configuration.GRID_COLUMNS_X,
                    Configuration.GRID_ROWS_Y);

            //Spike to send
            SpikeObject<String> patternSpike = new SpikeObject(SpikeType.UNINTEGRATED_SCENE_PATTERN, pattern, spike.getTime());
            
            encSpikeIdea.setValue(patternSpike.toBytes());
            encSpikeMO.setI(encSpikeIdea);
//            send(AreaNames.ENC, patternSpike.toBytes());

            //GUI AND LOG
            /*
            if (this.mv1 == null) {
                this.mv1 = new MemoryViewer(this.getClass().getName(),occupancyGrid.getGrid());
                this.mv1.setColorPerClass(false);
            } else {
                this.mv1.setImage(occupancyGrid.getGrid());
            }

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    mv1.setVisible(true);
                }
            });
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
}
