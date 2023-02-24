# Import necessary modules
import numpy as np
import pandas as pd 
import matplotlib.pyplot as plt
import seaborn as sns

from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.model_selection import cross_val_score


# Create feature and target arrays
data = pd.read_csv('WISDM_Jogging_and_Walking.csv')
phone_x_accelerometer = "acc_x"
phone_y_accelerometer = "acc_y"
phone_z_accelerometer = "acc_z"
label = "label"

X = data[[phone_x_accelerometer,phone_y_accelerometer]]
y = data[[label]]
# Split into training and test set
X_train, X_test, y_train, y_test = train_test_split(
			X, y, test_size = 0.2, random_state=42)

"""
k_values = [i for i in range (70,80)]
scores = []

for k in k_values:
	print(k)
	knn = KNeighborsClassifier(n_neighbors=k)
	knn.fit(X_train.values, y_train.values.ravel())
	score = cross_val_score(knn, X_train.values, y_train.values.ravel(), cv=5)
	scores.append(np.mean(score))
print("accuracy in %:", max(scores), "cluster:",scores.index(max(scores))+71)
sns.lineplot(x = k_values, y = scores, marker = 'o')
plt.xlabel("K Values")
plt.ylabel("Accuracy Score")
plt.show()
"""

knn = KNeighborsClassifier(n_neighbors=40)
knn.fit(X_train.values, y_train.values.ravel())
# Predict on dataset which model has not seen before
print(accuracy_score(knn.predict(X_test), y_test))
#print(knn.predict(X_test))


