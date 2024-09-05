using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class NetworkMonoBehaviour : MonoBehaviour
{

   abstract public void Execute(string json);

}
