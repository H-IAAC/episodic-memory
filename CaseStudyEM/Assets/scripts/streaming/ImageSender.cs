using System.Collections;
using System.Collections.Generic;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;

public class ImageSender {
    private static ILogger logger = Debug.unityLogger;
    private Socket sender;

    public ImageSender()
    {

    }

    public string connect(string hostIp, int hostPort)
    {
        string result = "connected";
        IPAddress ipAddress = IPAddress.Parse(hostIp);
        IPEndPoint remoteEP = new IPEndPoint(ipAddress, hostPort);

        sender = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

        try
        {
            sender.Connect(remoteEP);

        }catch(Exception e)
        {
            result = e.ToString();
        }

        return result;
    }

    public void sendImage(byte[] imageBytes)
    {
        Debug.Log("Sending image");
        Debug.Log(imageBytes);
        Thread newThread = new Thread(() => this.sendImageWorker(imageBytes));
        newThread.Start();
    }

    public void sendImageWorker(byte[] imageBytes)
    {

        try
        {
            byte[] fileLenght = BitConverter.GetBytes(imageBytes.Length);

            
            //sender.NoDelay = true;


            byte[] imageSizeBytes = BitConverter.GetBytes(imageBytes.Length);

            sender.Send(imageSizeBytes);
            int bytesSent = sender.Send(imageBytes);

            //logger.Log("JPEG size: " + imageBytes.Length);

            //logger.Log("STM", "sent " + bytesSent);

            //sender.NoDelay = false;

            //sender.Close();
        }
        catch (Exception e)
        {
            
        }
    }

    public void close()
    {
        try
        {
            sender.Shutdown(SocketShutdown.Both);
            sender.Close();
        }
        catch (Exception e)
        {

        }
    }

    /* Funciona para un solo envio pero consume un hilo por envio
    public void sendImage(byte[] imageBytes)
    {
        Thread newThread = new Thread(() => this.sendImageWorker(imageBytes));
        newThread.Start();
    }

    public void sendImageWorker(byte[] imageBytes)
    {
        IPAddress ipAddress = IPAddress.Parse("10.0.5.150");
        IPEndPoint remoteEP = new IPEndPoint(ipAddress, 10000);

        Socket sender = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

        try
        {
            sender.Connect(remoteEP);

            int bytesSent = sender.Send(imageBytes);

            sender.Shutdown(SocketShutdown.Both);
            sender.Close();
        }
        catch (Exception e)
        {

        }
    }*/

}
