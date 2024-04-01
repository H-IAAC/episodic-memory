/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis
 */
public class BigNodeBridge extends Thread {

    private ServerSocket listener;
//    private int serverPort = 10500;
    private int serverPort = 10000;
//    private AreaBridgeListener areaListener;

//    public BigNodeBridge(int serverPort, AreaBridgeListener areaListener) {
    public BigNodeBridge(int serverPort) {
        this.serverPort = serverPort;
        createServer();
//        start();
    }

    private void createServer() {
        try {
            listener = new ServerSocket(serverPort);
        } catch (IOException ex) {
            Logger.getLogger(BigNodeBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {

            while (true) {
                System.out.println("Waiting conections...");

                Socket socket = listener.accept();

//                BigNodeBridgeHandler singleClient = new BigNodeBridgeHandler(socket, areaListener);
                BigNodeBridgeHandler singleClient = new BigNodeBridgeHandler(socket);

            }

        } catch (IOException ex) {
            Logger.getLogger(BigNodeBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
