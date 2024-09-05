import socket
import json

from server.consolidation_server import ConsolidationServer

if __name__ == "__main__":

    with open("config.json") as json_data_file:
        data = json.load(json_data_file)

    hostname = socket.gethostbyname("127.0.0.1")
    model_filename = data['clusters_file']
    clusters_path = data['clusters_path']
    vocabulary_file = data['vocabulary_file']
    filename = data['scenes_path']
    port = data['port']

    print("Starting consolidation server...")

    server = ConsolidationServer(hostname, port, model_filename, vocabulary_file, filename, clusters_path)
    server.listen()
