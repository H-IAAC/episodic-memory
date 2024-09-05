using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CityBuilder : MonoBehaviour
{

    public GameObject[] buildings;
    public GameObject[] cars;
    public GameObject xstreets;
    public GameObject zstreets;
    public GameObject crossroad;
    public int mapWidth = 20;
    public int mapHeight = 10;
    public int buildingFootprint = 5000;
    int[,] mapGrid;
    public int startX = -250;
    public int startZ = -15;

    // Start is called before the first frame update
    void Start()
    {

        //float seed = Random.Range(0, 100);
        float seed = 72;

        mapGrid = new int[mapWidth, mapHeight];


        //Generar datos del mapa
        for (int h = 0; h < mapHeight; h++)
        {
            for (int w = 0; w < mapWidth; w++)
            {
                mapGrid[w, h] = (int)(Mathf.PerlinNoise(w / 10.0f + seed, h / 10.0f + seed) * 10);
            }
        }



        //Construir las calles

        int x = 0;

        for(int n = 0; n < 50; n++)
        {
            for(int h = 0; h < mapHeight; h++)
            {
                mapGrid[x, h] = -1;
            }

            x += Random.Range(3, 4);
            if (x >= mapWidth) break;

        }


        int z = 0;

        for (int n = 0; n < 10; n++)
        {
            for (int w = 0; w < mapWidth; w++)
            {
                if(mapGrid[w, z] == -1)
                {
                    mapGrid[w, z] = -3;

                }
                else
                {
                    mapGrid[w, z] = -2;
                }
            }

            z += Random.Range(3, 5);
            if (z >= mapHeight) break;

        }



        //Generar ciudad
        for (int h = 0; h < mapHeight; h++)
        {
            for(int w = 0; w < mapWidth; w++)
            {
                int result = mapGrid[w, h];

                Vector3 pos = new Vector3(startX + (w * buildingFootprint), 0, startZ + (h * buildingFootprint));

                if (result < -2)
                {
                    GameObject cr = (GameObject)Instantiate(crossroad, pos, crossroad.transform.rotation);
                    cr.layer = crossroad.layer;
                }
                else if (result < -1)
                {
                    GameObject cr = Instantiate(xstreets, pos, xstreets.transform.rotation);
                    cr.layer = xstreets.layer;
                    int prob = Random.Range(0, 100);

                    if (prob < 25)
                    {

                        Quaternion carRotation = Quaternion.Euler(xstreets.transform.rotation.x, -90, 0);
                        GameObject car = cars[Random.Range(0, cars.Length)];

                        Instantiate(car, new Vector3(pos.x, 1, pos.z), carRotation);
                    }
                }
                else if (result < 0)
                {
                    GameObject cr = Instantiate(zstreets, pos, zstreets.transform.rotation);
                    cr.layer = zstreets.layer;

                    int prob = Random.Range(0, 100);

                    if (prob < 25)
                    {
                        Quaternion carRotation = Quaternion.Euler(xstreets.transform.rotation.x, 0, 0);
                        GameObject car = cars[Random.Range(0, cars.Length)];

                        Instantiate(car, new Vector3(pos.x, 1, pos.z), carRotation);
                    }
                }
                else if (result < 1)
                    Instantiate(buildings[0], pos, Quaternion.identity);
                else if (result < 2)
                    Instantiate(buildings[1], pos, Quaternion.identity);
                else if (result < 4)
                    Instantiate(buildings[2], pos, Quaternion.identity);
                else if (result < 6)
                    Instantiate(buildings[3], pos, Quaternion.identity);
                else if (result < 7)
                    Instantiate(buildings[4], pos, Quaternion.identity);
                else if (result < 8)
                    Instantiate(buildings[5], pos, Quaternion.identity);
                else if (result < 10)
                    Instantiate(buildings[6], pos, Quaternion.identity);

            }
        }

        
    }

}
