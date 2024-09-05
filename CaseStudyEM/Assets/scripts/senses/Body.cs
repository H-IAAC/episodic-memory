using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Body : MonoBehaviour
{
    public Spike spike;
    private StateSender stateSender;
    public string hostIp = "192.168.1.73";
    public int hostPort = 10503;

    public float radius = 8.0f;
    public int sendEachFrames = 60;
    private int timeCount = 0;
    
    // Start is called before the first frame update
    void Start()
    {
        stateSender = new StateSender();
        stateSender.connect(hostIp, hostPort);
    }

    // Update is called once per frame
    void Update()
    {
        //InvokeRepeating("ObjectsAround", 10.0f, 5.0f);

        if (Time.frameCount % sendEachFrames == 0)
        {

            timeCount++;

            ObjectsAround();

        }

    }

    void ObjectsAround()
    {

        List<int> objectsNearby = new List<int>();

        Collider[] hitColliders = Physics.OverlapSphere(this.transform.position, radius);
        foreach (var hitCollider in hitColliders)
        {
            try
            {
                if (hitCollider.tag == "perceptible")
                {
                    
                    Vector3 targetDir = hitCollider.gameObject.transform.position - this.transform.position;
                    float angleToObject = Vector3.Angle(targetDir, transform.forward);
                    bool inFov = false;

                    if (angleToObject >= -30 && angleToObject <= 30)
                    {
                        inFov = true;
                    }

                    if (inFov)
                    {
                        int classId = hitCollider.gameObject.GetComponent<ClassAssign>().classId;
                        objectsNearby.Add(classId);
                        //Debug.Log(classId+"");
                    }
                    
                }
                    
            }
            catch (Exception e)
            {
                Debug.LogException(e, this);
            }


        }

        //AGREGAR A INFOV LOS ITEMS A ENVIAR
        spike = new Spike();
        spike.name = "Body";
        spike.modality = 1;
        spike.intensity = objectsNearby.ToArray();
        spike.location = objectsNearby.ToArray();
        spike.duration = timeCount;


       stateSender.sendState(spike);
        
        
    }

}
