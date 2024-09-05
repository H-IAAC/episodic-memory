using System.Collections;
using System.Collections.Generic;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;

public class StateSender
{

    private Socket sender;

    public StateSender()
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

        }
        catch (Exception e)
        {
            result = e.ToString();
        }

        return result;
    }

    public void sendState(object jsonClass)
    {
        string jsonString = JsonUtility.ToJson(jsonClass) + "\n";
        byte[] stringBytes = Encoding.ASCII.GetBytes(jsonString);

        Thread newThread = new Thread(() => this.sendStateWorker(stringBytes));
        newThread.Start();
    }

    public void sendStateWorker(byte[] stringBytes)
    {

        try
        {
            int bytesSent = sender.Send(stringBytes);
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

}