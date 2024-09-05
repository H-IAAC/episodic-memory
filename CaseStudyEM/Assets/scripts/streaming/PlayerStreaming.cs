using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class PlayerStreaming : MonoBehaviour
{
    public RawImage rawImage;
    public int sendEachFrames = 60;
    private static ImageSender imageSender;
    public static bool streaming = false;

    public string hostIp = "10.0.5.162";
    public int hostPort = 10000;

    private float currCountdownValue;


    void saveMap()
    {
        
    }

    void Start()
    {

        if (rawImage == null) { print("this is where my problem is"); }

        imageSender = new ImageSender();

    }

    void Update()
    {
        if (Time.frameCount % sendEachFrames == 0)
        {
            //imageSender.sendImage(getFPVMapBytes());
            if (streaming)
            {
                imageSender.sendImage(getFPVMapBytes());
            }
        }

    }


    
    public IEnumerator StartCountdown(float countdownValue = 10)
    {
        System.DateTime theTime = System.DateTime.Now;
        string time = theTime.ToString("HH:mm:ss\\Z");
        Debug.Log("Start: "+time);
        currCountdownValue = countdownValue;
        while (currCountdownValue > 0)
        {
            //Debug.Log("Countdown: " + currCountdownValue);
            yield return new WaitForSeconds(1.0f);
            currCountdownValue--;
        }

        Debug.Log("End");
        close();
    }

    public void startTask(float seconds)
    {
        StartCoroutine(StartCountdown(seconds));

    }

    //La magia del streaming

    public string connect()
    {
        string retval = imageSender.connect(hostIp, hostPort);

        return retval;
    }

    public void close()
    {
        imageSender.close();
    }


    byte[] getFPVMapBytes()
    {
        RenderTexture renderTexture = (RenderTexture)rawImage.mainTexture;

        Texture2D tex2d = new Texture2D(renderTexture.width, renderTexture.height, TextureFormat.RGB24, false);

        RenderTexture.active = renderTexture;
        tex2d.ReadPixels(new Rect(0, 0, renderTexture.width, renderTexture.height), 0, 0);
        tex2d.Apply();

        //SaveTextureToFile(tex2d, "captures/current_view" + Time.frameCount + ".png");
        print("BOM DIAAAA PRINCESA");
        print("Original size: " + tex2d.GetRawTextureData().Length);
        print("JPEG size: " + tex2d.EncodeToJPG().Length);

        Debug.Log("hit1");

        //return tex2d.EncodeToPNG();
        return tex2d.EncodeToJPG();
    }

    void captureFrame()
    {
        RenderTexture renderTexture = (RenderTexture)rawImage.mainTexture;

        Texture2D tex2d = new Texture2D(renderTexture.width, renderTexture.height, TextureFormat.RGB24, false);

        RenderTexture.active = renderTexture;
        tex2d.ReadPixels(new Rect(0, 0, renderTexture.width, renderTexture.height), 0, 0);
        tex2d.Apply();

        SaveTextureToFile(tex2d, "captures/current_view" + Time.frameCount + ".png");
    }

    void SaveTextureToFile(Texture2D texture, string filename)
    {
        System.IO.File.WriteAllBytes(filename, texture.EncodeToPNG());
    }
}
