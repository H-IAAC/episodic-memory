using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ButtonHandler : MonoBehaviour
{

    public PlayerStreaming playerStreaming;
    private bool streaming = false;

    public void stream()
    {
        Text txt = transform.Find("Text").GetComponent<Text>();

        if (!streaming)
        {

            print(playerStreaming.connect());
            
            PlayerStreaming.streaming = true;
            streaming = true; 

            txt.text = "Streaming...";

            playerStreaming.startTask(60*30);
        }
        else
        {
            PlayerStreaming.streaming = false;
            streaming = false;

            txt.text = "Start streaming";

            playerStreaming.close();

        }
    }
    

}
