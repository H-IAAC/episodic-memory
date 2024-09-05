using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;
using UnityStandardAssets.Characters.ThirdPerson;

public class PlayerWanderingController : MonoBehaviour
{
    public NavMeshAgent agent;
    public ThirdPersonCharacter character;
    private Vector3 destination;

    private float nextChange = 0.0f;
    private float interval = 5.0f;
    private bool isWaiting = false;
    private ArrayList indoorPositions;
    private bool start = false;
    private bool isWandering = false;

    // Start is called before the first frame update
    void Start()
    {
        agent.updateRotation = false;

        indoorPositions = new ArrayList();


        indoorPositions.Add(new Vector3(-62.4f, 0.0f, -10.0f));
        indoorPositions.Add(new Vector3(-63.0f, 0.0f, -12.0f));

        indoorPositions.Add(new Vector3(-59.5f, 0.0f, -6.4f));

        indoorPositions.Add(new Vector3(-56.6f, 0.0f, -12.6f));
        indoorPositions.Add(new Vector3(-57.5f, 0.0f, -10.6f));

        indoorPositions.Add(new Vector3(-62.8f, 0.0f, -2.17f));
        indoorPositions.Add(new Vector3(-62.8f, 0.0f, 0.37f));

        //


        indoorPositions.Add(new Vector3(4.0f, 0.0f, -59.0f));
        indoorPositions.Add(new Vector3(4.7f, 0.0f, -60.92f));
        indoorPositions.Add(new Vector3(3.2f, 0.0f, -61.4f));

        indoorPositions.Add(new Vector3(2.0f, 0.0f, -68.4f));
        indoorPositions.Add(new Vector3(1.67f, 0.0f, -66.5f));

        indoorPositions.Add(new Vector3(-6.63f, 0.0f, -66.9f));
        indoorPositions.Add(new Vector3(-7.38f, 0.0f, -64.9f));
        indoorPositions.Add(new Vector3(-7.38f, 0.0f, -61.42f));

        //

        indoorPositions.Add(new Vector3(58.9f, 0.0f, -3.82f));
        indoorPositions.Add(new Vector3(62.5f, 0.0f, -4.5f));

        indoorPositions.Add(new Vector3(69.9f, 0.0f, -2.46f));
        indoorPositions.Add(new Vector3(67.42f, 0.0f, -2.46f));
        indoorPositions.Add(new Vector3(68.5f, 0.0f, -3.52f));
        indoorPositions.Add(new Vector3(64.59f, 0.0f, -8.61f));

        indoorPositions.Add(new Vector3(67.0f, 0.0f, -13.3f));
        indoorPositions.Add(new Vector3(67.0f, 0.0f, -15.3f));
        indoorPositions.Add(new Vector3(61.0f, 0.0f, -13.3f));

        //

        indoorPositions.Add(new Vector3(-3.47f, 0.0f, 66.8f));
        indoorPositions.Add(new Vector3(-3.47f, 0.0f, 64.7f));
        indoorPositions.Add(new Vector3(-2.13f, 0.0f, 62.4f));

        indoorPositions.Add(new Vector3(-3.28f, 0.0f, 58.96f));
        indoorPositions.Add(new Vector3(-3.1f, 0.0f, 56.23f));
        indoorPositions.Add(new Vector3(-1.63f, 0.0f, 57.5f));

        indoorPositions.Add(new Vector3(2.69f, 0.0f, 60.3f));

        indoorPositions.Add(new Vector3(7.48f, 0.0f, 62.8f));
        indoorPositions.Add(new Vector3(7.48f, 0.0f, 60.0f));
        indoorPositions.Add(new Vector3(7.48f, 0.0f, 57.56f));



        //this.transform.position = new Vector3(-31f, 0, -32f);

        agent.Warp(new Vector3(-31f, 0, -32f));

    }


    Vector3 NextInDoorPosition()
    {
        int index = Random.Range(0, indoorPositions.Count);

        return (Vector3)indoorPositions[index];
    }

    void ChangeDestination()
    {
        if (Random.Range(0.0f, 1.0f) > 0.4f)
        {
            destination = NextInDoorPosition();
        }
        else
        {
            destination = new Vector3(Random.Range(-46.0f, 46.0f), 0, Random.Range(-63.0f, 63.0f));
        }


        agent.SetDestination(destination);

    }

    // Update is called once per frame
    void Update()
    {
        

        if (Input.GetKey(KeyCode.I))
        {
            isWandering = true;
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

            }

            if (start)
            {
                if (agent.remainingDistance > agent.stoppingDistance)
                {
                    character.Move(agent.desiredVelocity, false, false);
                }
                else
                {
                    character.Move(Vector3.zero, false, false);

                    if (!isWaiting)
                    {
                        nextChange = Time.time + interval;
                        isWaiting = true;
                    }
                    else
                    {
                        if (Time.time > nextChange)
                        {
                            ChangeDestination();
                            isWaiting = false;
                        }
                    }

                }
            }
        }

    }
}
