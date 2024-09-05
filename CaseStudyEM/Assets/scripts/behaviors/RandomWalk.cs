using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class RandomWalk : MonoBehaviour
{
    private float timeToChangeDirection;
    private Rigidbody rb;
    public float speed = 1.0f;

    void Start()
    {
        rb = GetComponent<Rigidbody>();
        ChangeDirection();
    }

    // Update is called once per frame
    void Update()
    {
        timeToChangeDirection -= Time.deltaTime;

        if (timeToChangeDirection <= 0)
        {
            ChangeDirection();
        }

        float moveHorizontal = Input.GetAxis("Horizontal");
        float moveVertical = Input.GetAxis("Vertical");

        Vector3 movement = new Vector3(moveHorizontal, 0.0f, moveVertical);

        rb.position += transform.forward * Time.deltaTime * speed;


    }

    private void ChangeDirection()
    {

        var tempRotation = Quaternion.identity;
        var tempVector = Vector3.zero;
        tempVector = tempRotation.eulerAngles;
        tempVector.y = Random.Range(0, 359);
        tempRotation.eulerAngles = tempVector;
        transform.rotation = tempRotation;

        timeToChangeDirection = 10f;
    }
}
