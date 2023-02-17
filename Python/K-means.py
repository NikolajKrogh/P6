import numpy as np
import pandas as pd 
import matplotlib.pyplot as plt
from matplotlib.colors import ListedColormap

from sklearn.cluster import KMeans
from sklearn import datasets
from sklearn.utils import shuffle
from sklearn.datasets import make_blobs


# import some data to play with
data = pd.read_csv('mhealth_raw_data.csv')
X = np.array([data.alx.values, data.alz.values]) 
y = data.aly.values
newX = []

i=0
while(i < len(X[0])):
    newX.append([X[0][i], X[1][i]])
    i += 1
print(newX)
newX = np.array(newX)
plt.scatter(X[:, 0], X[:, 1], s=50);

kmeans = KMeans(n_clusters=2)
kmeans.fit(newX)
y_kmeans = kmeans.predict(newX)

plt.scatter(newX[:, 0], newX[:, 1], c=y_kmeans, s=50, cmap='viridis')

centers = kmeans.cluster_centers_
plt.scatter(centers[:, 0], centers[:, 1], c='black', s=200, alpha=0.5);
plt.show()