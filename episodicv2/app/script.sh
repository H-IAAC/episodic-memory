#!/bin/bash

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
    if [ ! -f ../yolo/files/coco.names ]; then
        cp data/coco.names ../yolo/files/
        echo "Arquivo coco.names copiado."
    else
        echo "O arquivo coco.names já existe."
    fi

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

# Entrar na pasta consolidation_server
cd consolidation_server

# Criar o ambiente virtual
python3.7 -m venv venv

# Ativar o ambiente virtual
source venv/bin/activate

# Instalar os pacotes necessários
python -m pip install -r requirements.txt

# Iniciar o servidor
python3.7 start.py
