/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Connection;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis
 */
public class BigNodeBridgeHandler extends Thread {

    private Socket socket;
//    private AreaBridgeListener areaListener;

//    public BigNodeBridgeHandler(Socket socket, AreaBridgeListener areaListener) {
    public BigNodeBridgeHandler(Socket socket) {
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;

            while ((input = in.readLine()) != null) {
                
//                areaListener.receiveFromBridge(input);
                System.out.println("Input from Unity 3D");
                System.out.println(input);

            }

            in.close();
            socket.close();

        } catch (Exception ex) {
            Logger.getLogger(BigNodeBridgeHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(BigNodeBridgeHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
