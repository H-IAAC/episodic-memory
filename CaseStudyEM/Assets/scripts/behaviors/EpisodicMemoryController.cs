using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;
using UnityEngine.UI;
using UnityStandardAssets.Characters.ThirdPerson;

public class EpisodicMemoryController : NetworkMonoBehaviour
{
    public Body agentBody;
    public Spike spike;
    private StateSender stateSender;

    public RawImage rawImage;
    private static ImageSender imageSender;
    public static bool streaming = false;
    public string hostIp = "192.168.1.73";
    public int hostPort = 10000;
    public bool capturedSent = false;
    public bool captureBody = true;


    public NavMeshAgent agent;
    public ThirdPersonCharacter character;
    private Vector3 destination;

    private float nextChange = 0.0f;
    private float interval = 7.0f;
    private bool isWaiting = false;
    private ArrayList landMarkPositions;
    private ArrayList startPositions;
    private ArrayList adyacentList;

    private bool start = false;
    private bool isWandering = false;

    private int currentLandMark;
    private int nextLandMark;

    private int MOVING = 1;
    private int WATCHING = 2;

    private int WANDERING_MODE = 1;
    private int ROUTE_MODE = 2;


    private int STATE = 0;
    private int MODE = 0;

    private IEnumerator coroutine;
    private Vector3 lastVelocity;

    private int[] steps;
    private int currentStep = 0;
    private bool triggerNetworkReception = false;
    private float intervalRoute = 1.0f;
    public bool planningMode = false;
    private int startIndex = 0;

    // Start is called before the first frame update
    void Start()
    {

        //STREAMING 

        if (rawImage == null) { print("this is where my problem is"); }

        imageSender = new ImageSender();

        stateSender = new StateSender();
        stateSender.connect(hostIp, 10503);

        string retval = imageSender.connect(hostIp, hostPort);

        print("Perception connection: " + retval);


        agent.updateRotation = false;

        landMarkPositions = new ArrayList();
        startPositions = new ArrayList();
        adyacentList = new ArrayList();


        //Row 1

        landMarkPositions.Add(new Vector3(-37f, 0.0f, -33f)); //1
        landMarkPositions.Add(new Vector3(-17f, 0.0f, -33f)); //2
        landMarkPositions.Add(new Vector3(3f, 0.0f, -33f)); //3
        //indoorPositions.Add(new Vector3(29f, 0.0f, -33f)); 

        //

        landMarkPositions.Add(new Vector3(-37f, 0.0f, -11f)); //4
        landMarkPositions.Add(new Vector3(-17f, 0.0f, -11f)); //5
        landMarkPositions.Add(new Vector3(3f, 0.0f, -11f)); //6
        //indoorPositions.Add(new Vector3(29f, 0.0f, -13f));

        //

        landMarkPositions.Add(new Vector3(-37f, 0.0f, 9f)); //7
        landMarkPositions.Add(new Vector3(-17f, 0.0f, 9f)); //8
        landMarkPositions.Add(new Vector3(3f, 0.0f, 9f)); //9
        //indoorPositions.Add(new Vector3(29f, 0.0f, 7f));

        //

        //landMarkPositions.Add(new Vector3(-37f, 0.0f, 29f)); //10
        //landMarkPositions.Add(new Vector3(-17f, 0.0f, 29f)); //11
        //landMarkPositions.Add(new Vector3(3f, 0.0f, 29f)); //12
        //indoorPositions.Add(new Vector3(29f, 0.0f, 27f));

        startPositions.Add(new Vector3(-37f, 0.0f, -33f));
        startPositions.Add(new Vector3(-17f, 0.0f, -33f));
        startPositions.Add(new Vector3(3f, 0.0f, -33f));


        //Starts in one of the three first land marks

        int index = Random.Range(0, startPositions.Count);
        startIndex = Random.Range(0, landMarkPositions.Count);

        currentLandMark = index;

        //Posible Positions

        adyacentList.Add(new int[] { 1, 3 });
        adyacentList.Add(new int[] { 0, 2, 4 });
        adyacentList.Add(new int[] { 1, 5 });

        adyacentList.Add(new int[] { 0, 4, 6 });
        adyacentList.Add(new int[] { 1, 3, 5, 7 });
        adyacentList.Add(new int[] { 2, 4, 8 });

        adyacentList.Add(new int[] { 3, 7 });
        adyacentList.Add(new int[] { 4, 6, 8 });
        adyacentList.Add(new int[] { 7, 5 });




        coroutine = Wandering();

        MODE = WANDERING_MODE;


        //IF THE MIDDLEWARE IS GOING TO MAKE THE MOVEMENT PLAN

        if (planningMode)
        {
            Vector3 startPosition = (Vector3)landMarkPositions[0];
            Debug.Log(startPosition + "," + startIndex);
            //this.transform.position = startPosition;
            agent.Warp(startPosition);

            StartCoroutine(ViewStreaming());
        }
        else
        {
            Vector3 startPosition = (Vector3)startPositions[currentLandMark];
            agent.Warp(startPosition);

            imageSender.sendImage(getFPVMapBytes());

        }


    }


    Vector3 NextInDoorPosition()
    {
        int[] possibleLandMarks = (int[])adyacentList[currentLandMark];

        int selectedDoor = Random.Range(0, possibleLandMarks.Length);

        int landMarkIndex = possibleLandMarks[selectedDoor];

        print("current: " + currentLandMark + ", next: " + landMarkIndex);

        nextLandMark = landMarkIndex;

        return (Vector3)landMarkPositions[nextLandMark];
    }

    void ChangeDestination()
    {
        if (MODE == WANDERING_MODE)
        {
            destination = NextInDoorPosition();

        }
        else if (MODE == ROUTE_MODE)
        {
            if (currentStep < steps.Length)
            {
                int stepLocation = steps[currentStep];
                destination = (Vector3)landMarkPositions[stepLocation];
            }
            else
            {
                MODE = 0;
                currentStep = 0;
                Debug.Log("Goal reached");
            }

        }



        agent.SetDestination(destination);


    }


    private IEnumerator Wandering()
    {
        bool stopped = false;

        while (true)
        {
            if (!agent.pathPending)
            {
                if (agent.remainingDistance <= agent.stoppingDistance)
                {
                    if (!agent.hasPath || agent.velocity.sqrMagnitude == 0f)
                    {
                        character.Move(Vector3.zero, false, false);

                        if (!stopped)
                        {
                            stopped = true;

                            currentLandMark = nextLandMark;

                            transform.rotation = Quaternion.Euler(new Vector3(0f, 0f, 0f));

                            if (MODE == ROUTE_MODE)
                            {
                                nextChange = Time.time + intervalRoute;

                                currentStep++;
                            }
                            else
                            {
                                nextChange = Time.time + interval;
                            }




                        }
                        else
                        {
                            transform.rotation = Quaternion.Euler(new Vector3(0f, 0f, 0f));

                            
                            


                            if (Time.time > nextChange)
                            {

                                if (captureBody)
                                {
                                    if (Time.frameCount % 100 == 0)
                                    {
                                        sendObjectsAround();
                                    }
                                    
                                }

                                if (capturedSent)
                                {
                                    if (Time.frameCount % 100 == 0)
                                    {
                                        imageSender.sendImage(getFPVMapBytes());
                                    }

                                }


                                if (Time.time > nextChange + 3.0f)
                                {
                                    capturedSent = true;
                                    captureBody = false;

                                }

                                if (Time.time > nextChange + 6.0f)
                                {

                                    ChangeDestination();

                                    stopped = false;
                                    capturedSent = false;
                                    captureBody = true;
                                }


                            }

                        }


                    }
                    else
                    {
                        character.Move(agent.desiredVelocity, false, false);
                    }
                }
                else
                {
                    character.Move(agent.desiredVelocity, false, false);
                }
            }
            else
            {
                character.Move(agent.desiredVelocity, false, false);
            }

            yield return null;
        }
    }


    //SEND THE IMAGE WHEN PLAYER STOPS

    byte[] getFPVMapBytes()
    {
        RenderTexture renderTexture = (RenderTexture)rawImage.mainTexture;

        Texture2D tex2d = new Texture2D(renderTexture.width, renderTexture.height, TextureFormat.RGB24, false);

        RenderTexture.active = renderTexture;
        tex2d.ReadPixels(new Rect(0, 0, renderTexture.width, renderTexture.height), 0, 0);
        tex2d.Apply();

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


    private IEnumerator ViewStreaming()
    {
        while (true)
        {
            yield return new WaitForSeconds(1);
            imageSender.sendImage(getFPVMapBytes());
        }

    }


    // Update is called once per frame
    void Update()
    {



        if (Input.GetKey(KeyCode.I))
        {
            isWandering = true;

        }

        if (triggerNetworkReception)
        {

            triggerNetworkReception = false;

            MODE = ROUTE_MODE;

            ChangeDestination();

            StartCoroutine(coroutine);

        }


        if (!isWandering)
        {
            float h = Input.GetAxis("Horizontal");
            float v = Input.GetAxis("Vertical");

            // calculate camera relative direction to move:
            Vector3 cameraForward = Vector3.Scale(Camera.main.transform.forward, new Vector3(1, 0, 1)).normalized;
            Vector3 movement = v * cameraForward + h * Camera.main.transform.right;

            character.Move(movement, false, false);

        }
        else
        {
            if (Input.GetMouseButtonDown(0))
            {

                ChangeDestination();
                start = true;

                STATE = MOVING;

                StartCoroutine(coroutine);
            }

        }

    }

    //

    void sendObjectsAround()
    {

        List<int> objectsNearby = new List<int>();

        Collider[] hitColliders = Physics.OverlapSphere(this.transform.position, 8.0f);
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
            catch (System.Exception e)
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
        spike.duration = 1;


        stateSender.sendState(spike);


    }

    //NETWORK INSTRUCTIONS

    public override void Execute(string json)
    {
        CommandObject items = JsonUtility.FromJson<CommandObject>(json);
        steps = items.plan;

        triggerNetworkReception = true;

    }

}
