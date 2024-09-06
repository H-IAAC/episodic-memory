#!/bin/bash

# Verificar o argumento de entrada
if [ "$1" == "install" ]; then
    echo "Iniciando a instalação..."

    # Verificar se a pasta yolo/files já existe, se não, criar
    if [ ! -d yolo/files ]; then
        mkdir -p yolo/files
        echo "Pasta yolo/files criada."
    else
        echo "A pasta yolo/files já existe."
    fi

    # Verificar se coco.names ou yolov3.cfg já existem, se não, realizar os comandos
    if [ ! -f yolo/files/coco.names ] || [ ! -f yolo/files/yolov3.cfg ]; then
        # Clonar o repositório do darknet e compilar
        git clone https://github.com/pjreddie/darknet
        cd darknet
        make

        # Verificar se coco.names não existe e copiar
        if [ ! -f ../yolo/files/coco.names ]; then
            cp data/coco.names ../yolo/files/
            echo "Arquivo coco.names copiado."
        else
            echo "O arquivo coco.names já existe."
        fi

        # Verificar se yolov3.cfg não existe e copiar
        if [ ! -f ../yolo/files/yolov3.cfg ]; then
            cp cfg/yolov3.cfg ../yolo/files/
            echo "Arquivo yolov3.cfg copiado."
        else
            echo "O arquivo yolov3.cfg já existe."
        fi

        # Apagar a pasta darknet
        cd ..
        rm -rf darknet
        echo "Pasta darknet removida."
    else
        echo "Os arquivos coco.names e yolov3.cfg já existem."
    fi

    # Verificar se yolov3.weights já existe, se não, fazer o download
    if [ ! -f yolo/files/yolov3.weights ]; then
        wget https://pjreddie.com/media/files/yolov3.weights -P yolo/files
        echo "Arquivo yolov3.weights baixado."
    else
        echo "O arquivo yolov3.weights já existe."
    fi

    if [ -f "libs/opencv-480.jar" ] && [ -f "libs/libopencv_java480.so" ]; then
        echo "Os arquivos .jar e .so já existem na pasta libs. Nenhuma ação necessária."
    else
        echo "Arquivos .jar e .so não encontrados na pasta libs. Continuando com a instalação e compilação..."

        if [ -f "opencv-4.8.0/build/bin/opencv-480.jar" ] && [ -f "opencv-4.8.0/build/lib/libopencv_java480.so" ]; then
            echo "O build do OpenCV já foi concluído."
        else
            sudo apt-get -y install cmake
            sudo snap install ant --classic  # version 1.10.14
            sudo apt install g++
            wget https://github.com/opencv/opencv/archive/4.8.0.zip
            sudo apt-get install unzip
            unzip 4.8.0.zip
            cd opencv-4.8.0/
            mkdir build
            cd build
            cmake -D WITH_OPENJPEG=OFF \
                -D WITH_IPP=OFF \
                -D CMAKE_BUILD_TYPE=RELEASE \
                -D CMAKE_INSTALL_PREFIX=/usr/local/opencv \
                -D JAVA_INCLUDE_PATH=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include \
                -D JAVA_AWT_LIBRARY=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include/jawt.h \
                -D JAVA_INCLUDE_PATH_2=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include/linux \
                -D JAVA_JVM_LIBRARY=/usr/lib/jvm/java-1.11.0-openjdk-amd64/include/jni.h \
                -D BUILD_opencv_python2=OFF \
                -D BUILD_opencv_python2=OFF \
                -D BUILD_opencv_java=ON \
                -D INSTALL_PYTHON_EXAMPLES=OFF \
                -D INSTALL_C_EXAMPLES=OFF \
                -D OPENCV_ENABLE_NONFREE=OFF \
                -D BUILD_EXAMPLES=ON \
                -D BUILD_PERF_TESTS=OFF \
                -D BUILD_SHARED_LIBRARY=OFF \
                -D BUILD_TESTS=OFF \
                -D BUILD_opencv_python=OFF ..
            make -j4
            cd ..
        fi

        mkdir -p libs

        cp ./opencv-4.8.0/build/bin/opencv-480.jar ./libs
        cp ./opencv-4.8.0/build/lib/libopencv_java480.so ./libs

        rm 4.8.0.zip

        wget -qO - https://hub.unity3d.com/linux/keys/public | gpg --dearmor | sudo tee /usr/share/keyrings/Unity_Technologies_ApS.gpg > /dev/null

        sudo sh -c 'echo "deb [signed-by=/usr/share/keyrings/Unity_Technologies_ApS.gpg] https://hub.unity3d.com/linux/repos/deb stable main" > /etc/apt/sources.list.d/unityhub.list'

        sudo apt update
        sudo apt-get install unityhub
    fi
fi

# Entrar na pasta consolidation_server
cd consolidation_server/

python3.7 -m venv venv

source venv/bin/activate

pip install --upgrade pip

# Instalar os pacotes necessários (caso o ambiente já não tenha sido configurado)
python -m pip install -r requirements.txt
echo "Pacotes instalados."

# Iniciar o servidor
python3.7 start.py
