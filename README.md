# Episodic-memory

Episodic memory developed using CST based on Cuayollotl-Memory Bio-Inspired Implementation of Declarative Working Memory


### Software Requirements

- [Unity 2019.1.10f1](https://unity3d.com/get-unity/download/archive)
- [Python 3.7](https://www.python.org/downloads/release/python-370/)
- Netbeans (using version 21)
- Java JDK version "1.11.0"

### Script for linux:

Update the script with your java version:
```sh
-D JAVA_INCLUDE_PATH=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include \
-D JAVA_AWT_LIBRARY=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include/jawt.h \
-D JAVA_INCLUDE_PATH_2=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include/linux \
-D JAVA_JVM_LIBRARY=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include/jni.h \
```

Run the script:
```sh
chmod +x script.sh

./script.sh [install]
```
Use the install flag on first execution or if you are not sure if you have all the requirements.


After the script has run and you have on terminal:
```sh
[Model loaded]
[Consolidation server ready]
[Waiting for connections...]
```
Run the App.java file.

Open the unity project CMStudyEM on Unity Hub and click on run button (if the lightning is dark click on Generate Lightning on Unity Editor).
