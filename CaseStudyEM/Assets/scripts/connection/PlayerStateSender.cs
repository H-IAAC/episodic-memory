using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerStateSender : MonoBehaviour
{
    // Start is called before the first frame update

    public Spike spike;
    private StateSender stateSender;
    public string hostIp = "10.0.5.150";
    public int hostPort = 10503;
    public int sendEachFrames = 60;

    void Start()
    {
        spike = new Spike();
        /*spike.name = "State";
        spike.modality = 1;
        spike.intensity = new int[] { 20, 30, 40 };
        spike.location = new int[] { 40, 50, 60};
        spike.duration = 10;
        */
        stateSender = new StateSender();
        stateSender.connect(hostIp, hostPort);
    }

    // Update is called once per frame
    void Update()
    {
        if (Time.frameCount % sendEachFrames == 0)
        {
            stateSender.sendState(spike);
        }
    }
}
