# Import necessary modules
import numpy as np
import pandas as pd 
import matplotlib.pyplot as plt
from sklearn.model_selection import cross_val_score
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
import random

# Create feature and target arrays
data = pd.read_csv(r'data\WISDM_Jogging_and_Walking_0_1.csv')
runTestData = pd.read_csv(r"data\supervised_small_running.csv")
walkTestData = pd.read_csv(r"data\supervised_small_walking.csv")
smallData = pd.read_csv(r"data\supervised_logistic_regression.csv")
pamapData = pd.read_csv(r"data\pamapdataWithHR.csv")
pamapx = "3d_accel_x_2"
pamapy = "3d_accel_y_2"
pamapz = "3d_accel_z_2"
pamapheart = "heartrate"
pamaplabel = "activityType"

phone_x_accelerometer = "raw_acc:3d:std_x"
phone_y_accelerometer = "raw_acc:3d:std_y"
phone_z_accelerometer = "raw_acc:3d:std_z"
running = "label:FIX_running"
walking = "label:FIX_walking"
label = "label:is_running"
acc_x = "acc_x"
acc_y = "acc_y"
acc_z = "acc_z"

X_pamap_no_heart = pamapData[[pamapx,pamapy,pamapz]]

X_pamap = pamapData[[pamapx,pamapy,pamapz,pamapheart]]
Y_pamap = pamapData[[pamaplabel]]

X_train, X_test, y_train, y_test = train_test_split(X_pamap_no_heart, Y_pamap, test_size = 0.2, random_state=42)

X = data[[acc_x,acc_y,acc_z]]
Y = data[[label]]
# Split into training and test set
#X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size = 0.2, random_state=42)

#pipe = make_pipeline(StandardScaler(), KNeighborsClassifier())
#pipe.fit(X_train,y_train)
#print(pipe.score(X_test,y_test))

min_max_scaler = preprocessing.MinMaxScaler()

scaler = preprocessing.StandardScaler().fit(X_train)
X_train_scaled = scaler.transform(X_train)
X_train_minmax = min_max_scaler.fit_transform(X_train)
scaler = preprocessing.StandardScaler().fit(X_test)
X_test_scaled = scaler.transform(X_test)
X_test_minmax = min_max_scaler.fit_transform(X_test)

X_small_train = smallData[[phone_x_accelerometer,phone_y_accelerometer,phone_x_accelerometer]]
scaler = preprocessing.StandardScaler().fit(X_small_train)
X_small_train_scaled = scaler.transform(X_small_train)
X_small_train_minmax = min_max_scaler.fit_transform(X_small_train)
y_small_train = smallData[["label:is_running"]]

#scaler = preprocessing.StandardScaler().fit(y_train)
#y_train_scaled = scaler.transform(y_train)
#print(X_train_scaled)


#X_test = smallData[[phone_x_accelerometer,phone_y_accelerometer,phone_x_accelerometer]]
#y_test = smallData[["label:is_running"]]
X_test_run = runTestData[[acc_x,acc_y,acc_z]]
y_test_run = runTestData[[label]]
X_test_walk = walkTestData[[acc_x,acc_y,acc_z]]
y_test_walk = walkTestData[[label]]

knn = KNeighborsClassifier(n_neighbors=400)
knn.fit(X_train_minmax, y_train.values.ravel())


def calcBestN():
  neighbors = np.arange(1, 9)
  scores = []
  for k in range(1,9):
    knn = KNeighborsClassifier(n_neighbors = k)
    print(k)
    #Fit the classifier to the training data
    knn.fit(X_train.values, y_train.values.ravel())
    #Compute accuracy on the training set
    #Compute accuracy on the testing set
    scores.append(cross_val_score(knn,X_train.values, y_train.values.ravel(),cv=5))

  plt.title('k-NN: Varying Number of Neighbors')
  plt.plot(neighbors, scores, label = 'Testing Accuracy')
  plt.legend()
  plt.xlabel('# of Neighbors')
  plt.ylabel('Accuracy')
  plt.show()



print("prediction :",accuracy_score(knn.predict(X_test_minmax),y_test))
print("small data:",accuracy_score(knn.predict(X_small_train_minmax),y_small_train))
print("run accuracy: ",accuracy_score(knn.predict(X_test_run),y_test_run))
print("walk accuracy: ",accuracy_score(knn.predict(X_test_walk),y_test_walk))


newPredictions = []
walking = 0
jogging = 0


#for i in range(1000):
#[[random.uniform(-1,1),random.uniform(-1,1),random.uniform(-1,1)]]


for x in X_test_minmax:
  prediction = knn.predict([x])
  if prediction == 1:
      jogging += 1
  else:
      walking += 1
    
print("walking: ",walking)
print("jogging:",jogging)
#for pred in newPredictions
#print(knn.predict(X_test))


