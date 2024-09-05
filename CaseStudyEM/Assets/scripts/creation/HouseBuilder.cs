using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HouseBuilder : MonoBehaviour
{
    // Start is called before the first frame update

    private int startY = 0;
    public float originX = -50;
    public float originZ = -100;
    private int roomsMaxX = 3;
    private int roomsMaxZ = 3;

    private int cityX = 4;
    private int cityZ = 4;

    private int[,] houseDimensions;
    private int[,] cityDimensions;

    public GameObject floor;
    public GameObject outdoorFloor;
    public GameObject wall;
    public GameObject wallDoor;
    public GameObject[] bedroomObjects;
    public GameObject[] bathroomObjects;
    public GameObject[] kitchenObjects;
    public GameObject[] hallObjects;
    private GameObject[][] typeOfRooms;

    private int[] inRoomRotation = new int[] { 0, 0, 0,     -90, -90, -90,                  180, 180, 180,                        90, 90, 90 };
    private float[] inRoomX = new float[] { 0.0f, 0.0f, 0.0f,    0.5f, 0.5f, 0.5f,                   0, 0, 0,                            -0.5f, -0.5f, -0.5f };
    private float[] inRoomZ = new float[] { -0.5f, -0.5f, -0.5f,   0.0f, 0.0f, 0.0f,      0.5f, 0.5f, 0.5f,                    -0.0f, -0.0f, -0.0f };



    void Start()
    {
        int roomsX = Random.Range(2, roomsMaxX);
        int roomsZ = Random.Range(1, roomsMaxZ);

        float offsetX = 0f;
        float offsetZ = 0f;

        float sepX = 0.0f;
        float sepZ = 0.0f;
        
        for (int i = 0; i < cityX; i++)
        {
            for (int j = 0; j < cityZ; j++)
            {
                roomsX = Random.Range(1, roomsMaxX);
                roomsZ = Random.Range(1, roomsMaxZ);

                offsetX = (i * roomsMaxX * (floor.transform.localScale.x + sepX));
                offsetZ = (j * roomsMaxZ * (floor.transform.localScale.z + sepZ));

                createHouse(roomsX, roomsZ, originX + offsetX, originZ + offsetZ);

            }

        }

        
    }

    void createHouse(int roomsX, int roomsZ, float startX, float startZ)
    {


        houseDimensions = new int[roomsMaxX, roomsMaxZ];

        typeOfRooms = new GameObject[][] { bedroomObjects, bathroomObjects, kitchenObjects, hallObjects };

        
        for (int h = 0; h < roomsX; h++)
        {
            for (int w = 0; w < roomsZ; w++)
            {
                houseDimensions[h, w] = 1;

            }
        }
        
        for (int h = 0; h < roomsMaxX; h++)
        {
            for (int w = 0; w < roomsMaxZ; w++)
            {
                Vector3 pos = new Vector3(startX + (h * floor.transform.localScale.x), startY, startZ + (w * floor.transform.localScale.z));


                if( w <= roomsZ && h < roomsX)
                {
                    Quaternion wallRotation = Quaternion.Euler(wall.transform.rotation.x, -90, 0);
                    Vector3 pos2 = new Vector3(startX + (h * floor.transform.localScale.x), startY, startZ + (w * floor.transform.localScale.z));
                    

                    if (w > 0 && w < roomsZ)
                    {
                        Instantiate(wallDoor, pos2, wallRotation);
                    }
                    else
                    {
                        Instantiate(wall, pos2, wallRotation);
                    }

                    if (w == roomsZ -1 && roomsZ == roomsMaxZ)
                    {
                        pos2 = new Vector3(startX + (h * floor.transform.localScale.x), startY, startZ + (roomsMaxZ * floor.transform.localScale.z));
                        Instantiate(wall, pos2, wallRotation);
                    }

                }

                
                if (w < roomsZ && h <= roomsX)
                {
                    Vector3 pos2 = new Vector3(startX + (h * floor.transform.localScale.x), startY, startZ + (w * floor.transform.localScale.z));

                    if (h > 0)
                    {
                        Instantiate(wallDoor, pos2, Quaternion.identity);
                    }
                    else
                    {
                        Instantiate(wall, pos2, Quaternion.identity);
                    }

                    if (h == roomsX - 1 && roomsX == roomsMaxX)
                    {
                        pos2 = new Vector3(startX + (roomsMaxX * floor.transform.localScale.x), startY, startZ + (w * floor.transform.localScale.z));
                        Instantiate(wallDoor, pos2, Quaternion.identity);
                    }
                }




                if (houseDimensions[h, w] == 1)
                {

                    GameObject instantiatedObject = Instantiate(floor, pos, Quaternion.identity);
                    instantiatedObject.layer = 8;

                    CreateRandomRoom(instantiatedObject);

                }
                else if (houseDimensions[h, w] == 0)
                {
                    GameObject instantiatedObject = Instantiate(outdoorFloor, pos, Quaternion.identity);
                    instantiatedObject.layer = 8;
                }
            }
        }
    }

    void CreateRandomRoom(GameObject floor)
    {
        int type = Random.Range(0, typeOfRooms.Length);
        GameObject[] selectedObjects = typeOfRooms[type];

        CreateBedRoom(floor, selectedObjects);
    }

    void CreateBedRoom(GameObject floor, GameObject[] selectedObjects)
    {
        float orX = floor.transform.position.x - floor.transform.localScale.x/2.0f;
        float orZ = floor.transform.position.z - floor.transform.localScale.z/2.0f;


        Vector3 floorSize = floor.GetComponent<Renderer>().bounds.size;
     //   Vector3 itemSize = bedroomObjects[0].GetComponent<Renderer>().bounds.size;

        int itemsInZ = 6;
        int itemsInX = 6;
        float itemSpaceZ = floorSize.z / itemsInZ;
        float itemSpaceX = floorSize.x / itemsInX;

        float itemSizeZ = 0;
        float itemSizeX = 0;
        float itemSizeY = 0;

        for (int i = 0; i < itemsInX; i++)
        {
            for (int j = 0; j < itemsInZ; j++)
            {
                int itemIndex = Random.Range(0, selectedObjects.Length);
                //itemIndex = 1;
                GameObject item = selectedObjects[itemIndex];

                Vector3 itemSize = item.GetComponent<Renderer>().bounds.size;

                itemSizeZ = itemSize.z / 2;
                itemSizeX = itemSize.x / 2;
                itemSizeY = itemSize.y / 2;

                float itemPositionX = orX + (i * itemSpaceX);
                float itemPositionZ = orZ + (j* itemSpaceZ);

                int rotation = 0;

                if(j == 0 && i == 0)
                {
                    rotation = 90;
                    itemPositionX += itemSizeZ;
                    itemPositionZ += itemSizeX;

                    //itemPositionZ += itemSpaceZ / 2;

                }
                else if(j == itemsInZ -1 && i == 0)
                {
                    rotation = 180;
                    itemPositionX += itemSizeX;
                    itemPositionZ += (itemSpaceX - itemSizeZ);

                    //itemPositionX += itemSpaceX / 2;
                }
                else if(j==0 && i == itemsInX - 1)
                {
                    rotation = -90;
                    itemPositionX += (itemSpaceX - itemSizeZ);
                    itemPositionZ += itemSizeX;

                    //itemPositionZ += itemSpaceZ / 2;
                }
                else if(j==itemsInZ-1 && i == itemsInX - 1)
                {
                    rotation = -90;
                    itemPositionX += (itemSpaceX - itemSizeZ);
                    itemPositionZ += itemSizeX;

                    //itemPositionZ += itemSpaceZ / 2;
                    continue;
                }else if (j > 0 && j < itemsInZ - 1 && i > 0 && i < itemsInX - 1)
                {
                    itemPositionX += itemSpaceX / 2;
                    itemPositionZ += itemSpaceZ / 2;
                    continue;
                }
                else
                {

                    if (j == 0)
                    {
                        rotation = 0;
                        itemPositionX += itemSizeX;
                        itemPositionZ += itemSizeZ;


                        //itemPositionX += itemSpaceX / 2;
                    }
                    else if (j == itemsInZ - 1)
                    {
                        rotation = 180;
                        itemPositionX += itemSizeX;
                        itemPositionZ += (itemSpaceX - itemSizeZ);


                       // itemPositionX += itemSpaceX / 2;
                    }

                    if (i == 0)
                    {
                        rotation = 90;
                        itemPositionX += itemSizeZ;
                        itemPositionZ += itemSizeX;

                        //itemPositionZ += itemSpaceZ / 2;
                    }
                    else if (i == itemsInX - 1)
                    {
                        rotation = -90;
                        itemPositionX += (itemSpaceX - itemSizeZ);
                        itemPositionZ += itemSizeX;
                        //itemPositionZ += itemSpaceZ / 2;
                    }
                }



                Vector3 itemPosition = new Vector3(itemPositionX, 0, itemPositionZ);
                Quaternion itemRotation = Quaternion.Euler(item.transform.rotation.x, rotation, 0);

                float scaleIt = 0.9f;

                if(!Physics.CheckBox(new Vector3(itemPositionX, 0.5f, itemPositionZ), new Vector3(itemSizeX* scaleIt, itemSizeY* scaleIt, itemSizeZ* scaleIt)))
                {
                    GameObject instantiatedObject = Instantiate(item, itemPosition, itemRotation);
                }

                

            }
        }

    }



}
