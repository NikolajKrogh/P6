import numpy as np
import pandas as pd 
import matplotlib.pyplot as plt

from sklearn.cluster import KMeans



# import some data to play with
data = pd.read_csv('mhealth_raw_data.csv')
X = np.array([data.grx.values, data.gry.values]) 
newX = []

i=0
while(i < len(X[0])):
    newX.append([X[0][i], X[1][i]])
    i += 1
print(newX)
newX = np.array(newX)

kmeans = KMeans(n_clusters=2, random_state=42, n_init='auto')
kmeans.fit_transform(newX)
y_kmeans = kmeans.predict(newX)
print(y_kmeans)
plt.scatter(newX[:, 0], newX[:, 1], c=y_kmeans, cmap='viridis')

centers = kmeans.cluster_centers_
plt.scatter(centers[:, 0], centers[:, 1], c='black', s=200, alpha=0.5);
plt.show()