import matplotlib.pyplot as plt
import numpy as np
import math

from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
from joblib import dump, load
from pathlib import Path


class SceneClustering(object):

    def __init__(self, model_filename, vocabulary_file, filename, clusters_path):
        self._model_filename = model_filename
        self._vocabulary_file = vocabulary_file
        self._filename = filename
        self._model = None
        self._vectorizer = None
        self._clusters_path = clusters_path

    def load_scenes(self, scenes_file):

        file = open(scenes_file, "r")
        scenes = []

        for line in file:
            scenes.append(line)

        return scenes

    def load_corpus(self, scenes_file):

        file = open(scenes_file, "r")
        corpus = []

        for line in file:
            element = line.split(",")[1]
            corpus.append(element)

        return corpus

    def save_matrix(self, matrix):
        with open('outfile.txt', 'w') as f:
            for line in matrix:
                np.savetxt(f, line, fmt='%i')

    def save_cluster(self, name, c_scenes):

        Path(self._clusters_path).mkdir(parents=True, exist_ok=True)

        with open(self._clusters_path+"\cluster" + str(name) + ".txt", 'w') as f:
            for line in c_scenes:
                f.write(line)

    def rule_of_thumb(self, scene_data):
        num_objects = scene_data.shape[0]
        clusters = math.sqrt((num_objects / 2))
        return int(clusters)

    def silhouette_method(self, scene_data, max_iterations):
        clusters = range(2, max_iterations)
        best_k = 0
        max_sc = 0
        for k in clusters:
            km = KMeans(n_clusters=k)
            km = km.fit(scene_data)
            label = km.labels_
            sil_coeff = silhouette_score(scene_data, label, metric='euclidean')

            if sil_coeff > max_sc:
                max_sc = sil_coeff
                best_k = k

            # print("For n_clusters={}, The Silhouette Coefficient is {}".format(k, sil_coeff))

        return best_k

    def load_or_train(self):

        loaded = self.load_kmeans_model()

        if not loaded:
            self.train_kmeans()

    def load_kmeans_model(self):

        try:
            self._model = load(self._model_filename)
            self._vectorizer = load(self._vocabulary_file)
            return True
        except Exception as e:
            print(e)
            return False

    def train_kmeans(self):

        scenes = self.load_scenes(self._filename)
        corpus = self.load_corpus(self._filename)

        scenes_data = []
        i = 0
        for scene in scenes:
            scene_data = [corpus[i], scene]
            scenes_data.append(scene_data)
            i += 1

        vectorizer = CountVectorizer(token_pattern=r'\d+', min_df=1)
        scenes_matrix = vectorizer.fit_transform(corpus).todense()
        features = vectorizer.get_feature_names()

        clusters = self.rule_of_thumb(scenes_matrix)

        model = KMeans(n_clusters=clusters, init='k-means++', max_iter=200, n_init=1)
        model.fit(scenes_matrix)

        cluster_scenes = []

        for i in range(clusters):
            list_k = []
            cluster_scenes.append(list_k)

        for scene_d in scenes_data:
            p_matrix = vectorizer.transform([scene_d[0]]).todense()
            Y = model.predict(p_matrix)[0]
            cluster_scenes[Y].append(scene_d[1])

        c = 0
        for cs in cluster_scenes:
            self.save_cluster(c, cs)
            c += 1

        self._vectorizer = vectorizer
        self._model = model

        dump(model, self._model_filename)
        dump(vectorizer, self._vocabulary_file)

    def scene_prediction(self, scene_pattern):
        try:
            p_matrix = self._vectorizer.transform([scene_pattern]).todense()
            kc = self._model.predict(p_matrix)[0]
            return kc
        except Exception as e:
            print(e)
            return -1



# Main

if __name__ == "__main__":
    model_filename = "scenes_clusters.pkl"
    vocabulary_file = 'vocabulary.pkl'
    filename = "../ca3_scenes.txt"
    scene_pattern = "(61)<(57)<(70)<<<<<<"

    scene_clustering = SceneClustering(model_filename, vocabulary_file, filename)
    scene_clustering.load_or_train()

    #cluster = scene_clustering.scene_prediction(scene_pattern)

    #print(cluster)
