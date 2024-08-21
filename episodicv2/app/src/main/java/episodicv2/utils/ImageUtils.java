/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.utils;

import episodicv2.core.gui.ImageComponent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author luis_
 */
public class ImageUtils {

    public static BufferedImage getImageFromBase64(String base64Image) {
        try {

            byte[] data = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage bImage = ImageIO.read(bis);
            //ImageIO.write(bImage2, "jpg", new File("output.jpg"));
            return bImage;
        } catch (IOException ex) {
            Logger.getLogger(ImageComponent.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String toBase64(BufferedImage image) {
        
        String base64String = null;
        
        try {
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bos);
            byte[] imageBytes = bos.toByteArray();
            
            base64String = new String(Base64.getEncoder().encode(imageBytes));
            
            return base64String;
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return base64String;
        }
    }

}
