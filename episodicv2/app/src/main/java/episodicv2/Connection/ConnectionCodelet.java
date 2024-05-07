/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Connection;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import episodicv2.configuration.Configuration;
import static episodicv2.configuration.Configuration.IMAGE_RECEIVED_PATH_MO;
import static episodicv2.configuration.Configuration.SOCKET_CONNECTION_PORT_MO;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;


/**
 *
 * @author karenlima
 */
public class ConnectionCodelet extends Codelet {
    int port = 10000;
    Socket socket;
    
    
    MemoryObject socketConnectionPortMO;
    Idea socketConnectionPortIdea;
    
    MemoryObject imageReceivedFromConnectionMO;
    Idea imageReceivedFromConnectionIdea;
    

    @Override
    public void accessMemoryObjects() {
        System.out.println("Executing accessMemoryObjects Vision Codelet");
        socketConnectionPortMO = (MemoryObject) getInput(SOCKET_CONNECTION_PORT_MO);
        socketConnectionPortIdea = (Idea) socketConnectionPortMO.getI();
        
        imageReceivedFromConnectionMO = (MemoryObject) getOutput(Configuration.IMAGE_RECEIVED_FROM_CONNECTION_MO);
        imageReceivedFromConnectionIdea = (Idea) imageReceivedFromConnectionMO.getI();
    }
    
    @Override
    public void proc() {
        Integer port = (Integer) socketConnectionPortIdea.getValue();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for connections...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());
                Integer i = 0;
                while (i<300) {
                    // Receive image size
                    InputStream inputStream = socket.getInputStream();
                    byte[] sizeBytes = new byte[4];
                    inputStream.read(sizeBytes);
                    int imageSize = ByteBuffer.wrap(sizeBytes).getInt();
                    System.out.println("Image size: " + imageSize);
                    if (imageSize <= 0) {
                        System.err.println("Received invalid image size: " + imageSize);
                        continue; // Continue para a próxima iteração do loop
                    }
                    
                    if (imageSize > 1697382400) {
                        System.err.println("Received invalid image size: " + imageSize);
                        continue; // Continue para a próxima iteração do loop
                    }

                    // Receive image data
                    byte[] imageData = new byte[imageSize];
                    inputStream.read(imageData);


                    // Convert byte array to BufferedImage
                    InputStream imageInputStream = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(imageInputStream);

                    // Display the received image
                     
                     if (image != null) {
                         System.out.println("showing image");
                         displayImage(image);
                         imageReceivedFromConnectionIdea.setValue(image);
                         imageReceivedFromConnectionMO.setI(imageReceivedFromConnectionIdea);
                     } else {
                         System.out.println("image is null");
                     }
                    
                    i++;
                }
                
                System.out.println("closing connection");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
    public static void displayImage(BufferedImage image) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
