# episodic-memory

Episodic memory developed using CST based on Cuayollotl-Memory Bio-Inspired Implementation of Declarative Working Memory

Requirements:
- Java
- Unity 3D
- Yolo

In the folder "episodicv2/app/ create a folder "yolo/files" and insert the yolov3.weights and yolov3.cfg that can be downloaded with this steps:
- Config file:

```
git clone https://github.com/pjreddie/darknet
cd darknet
make
```

- Pre-trained weight file:
```
wget https://pjreddie.com/media/files/yolov3.weights
```

More can be found at: https://pjreddie.com/darknet/yolo/

 

Colocar parte do script do OpenCV

OpenCV for Mac:

https://developers.ascendcorp.com/setting-up-opencv-java-for-macs-with-apple-silicon-m1-18e3fc85895f
https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html

xcode-select --install
sudo xcodebuild -license
//install jdk
Download the latest Java JDK from the Oracle website. Now you should be able to install the last Java JDK by opening the file just downloaded.
brew install adoptopenjdk


brew install cmake
brew install ant

brew install wget
wget -O opencv.zip https://github.com/opencv/opencv/archive/4.8.0.zip
unzip opencv.zip

cd opencv-4.8.0
mkdir build && cd build

cmake -DWITH_OPENJPEG=OFF \
-DWITH_IPP=OFF \
-D CMAKE_BUILD_TYPE=RELEASE \
-D CMAKE_INSTALL_PREFIX=/usr/local/opencv \
-D JAVA_INCLUDE_PATH=$JAVA_HOME/include \
-D JAVA_AWT_LIBRARY=$JAVA_HOME/lib/jvm/java-1.8.0/include/jawt.h \
-D JAVA_JVM_LIBRARY=$JAVA_HOME/jvm/java-1.8.0/include/jni.ho \
-D BUILD_opencv_python2=OFF \
-D BUILD_opencv_java=ON \
-D INSTALL_PYTHON_EXAMPLES=OFF \
-D INSTALL_C_EXAMPLES=OFF \
-D OPENCV_ENABLE_NONFREE=OFF \
-D BUILD_EXAMPLES=ON ..

make -j8

sudo make install

insert the Java AWT include path (e.g., /usr/lib/jvm/java-1.8.0/include/)
insert the Java AWT library path (e.g., /usr/lib/jvm/java-1.8.0/include/jawt.h)
insert the Java include path (e.g., /usr/lib/jvm/java-1.8.0/include/)
insert the alternative Java include path (e.g., /usr/lib/jvm/java-1.8.0/include/linux)
insert the JVM library path (e.g., /usr/lib/jvm/java-1.8.0/include/jni.h)