import socket
import threading
import json

from consolidation.scenes_clustering import SceneClustering


class ConsolidationServer(object):

    TRAIN_MODEL = '1'
    PREDICT_PATTERN = '2'

    def __init__(self, host, port, model_filename, vocabulary_file, filename, clusters_path):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((self.host, self.port))
        self.scene_clustering = SceneClustering(model_filename, vocabulary_file, filename, clusters_path)

        if self.scene_clustering.load_kmeans_model():
            print("[Model loaded]")
        else:
            print("[Model does not exist]")

    def listen(self):
        self.sock.listen(5)
        while True:
            print("[Consolidation server ready]")
            print("[Waiting for connections...]")
            client, address = self.sock.accept()
            threading.Thread(target=self.listen_to_single_client, args=(client, address)).start()
            print("[Connected client: ", address, "]")

    def listen_to_single_client(self, client, address):

        size = 1024
        total = 0

        image_bytes_tmp = bytearray()

        while True:
            try:

                data = client.recv(size)
                total += len(data)
                image_bytes_tmp.extend(data)

                if len(data) == 0:
                    print("[0 bytes received, stopping...]")
                    break

                if len(data) < size:
                    image_bytes = bytes(bytearray(image_bytes_tmp))

                    self.handle_command(client, image_bytes.decode("utf-8"))

                    total = 0

                    image_bytes_tmp = bytearray()

            except Exception as e:
                print(e)
                client.close()
                break

        print("[Connection closed]")

        client.close()

    def handle_command(self, client, data):

        json_data = json.loads(data)

        print("Request: ", json_data)

        command = json_data['command']

        if command == self.TRAIN_MODEL:

            print("[Training model...]")

            self.scene_clustering.train_kmeans()

            print("[The model has been trained]")

        elif command == self.PREDICT_PATTERN:

            print("[Making prediction...]")

            pattern = json_data['data']
            cluster = self.scene_clustering.scene_prediction(pattern)
            json_response = json.dumps({"cluster": int(cluster)}) + "\n"
            client.send(json_response.encode())

            print("Response: ", json_response)
