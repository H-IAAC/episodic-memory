/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core;

import episodicv2.core.configuration.Configuration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Luis Martin
 */
public class ConsolidationNodeHandler extends Thread {

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader in;

    public ConsolidationNodeHandler() {
        try {

            socket = new Socket(Configuration.CONSOLIDATION_NODE_HOST, Configuration.CONSOLIDATION_NODE_PORT);
            pw = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void createClusters(){
        try {

            Socket consolidationSocket = new Socket(Configuration.CONSOLIDATION_NODE_HOST, Configuration.CONSOLIDATION_NODE_PORT);
            PrintWriter consolidationPW = new PrintWriter(consolidationSocket.getOutputStream());

            JSONObject commandJSON = new JSONObject();

            commandJSON.put("command", "1");

            consolidationPW.println(commandJSON.toJSONString());
            consolidationPW.flush();

            consolidationPW.close();
            consolidationSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getClusterSynchronous(String pattern) {

        long cluster = -1;
        
        try {
            String parts[] = pattern.split(",");
            
            JSONObject commandJSON = new JSONObject();
            
            commandJSON.put("command", "2");
            commandJSON.put("data", parts[0]);
            
            pw.println(commandJSON.toJSONString());
            pw.flush();
            
            String response = in.readLine();
            
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);

            cluster = (Long) jsonObject.get("cluster");
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return (int)cluster;
        
    }

    public void close() {
        try {
            pw.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            char buffer[] = new char[4096];
            StringBuilder strBuilder = new StringBuilder();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int read = 0;

            while ((read = in.read(buffer)) > 0) {

                strBuilder.append(new String(buffer).trim());

                if (read < buffer.length) {

                    System.out.println(strBuilder.toString());

                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(strBuilder.toString());

                    System.out.println(obj.get("cluster"));

                    strBuilder.delete(0, strBuilder.length());

                }

                buffer = new char[4096];

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
