import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.datasets import load_iris
from sklearn.linear_model import LogisticRegression
import datetime
from sklearn.model_selection import train_test_split 


def alterDataForLogisticRegression(running_label,walking_label):
    data = pd.read_csv(r"data\supervised_small.csv")
    labels = data[[running_label,walking_label]]
    isRunningLabels = []
    for index, row in labels.iterrows():
        if row[running_label] == 1.0:
            isRunningLabels.append(1)
        else:
            isRunningLabels.append(0)
    data["label:is_running"] = isRunningLabels
    data.to_csv(r"data\supervised_logistic_regression.csv")

data = pd.read_csv("data\supervised_logistic_regression.csv")

phone_x_accel = "raw_acc:3d:std_x"
phone_y_accel = "raw_acc:3d:std_y"
phone_z_accel = "raw_acc:3d:std_z"
watch_x_accel = "watch_acceleration:3d:std_x"
watch_y_accel = "watch_acceleration:3d:std_y"
watch_z_accel = "watch_acceleration:3d:std_z"
isRunningLabel = "label:is_running"
phone_and_watch_xyz_with_timestamp = data[["timestamp",phone_x_accel,phone_y_accel,phone_z_accel,watch_x_accel,watch_y_accel,watch_z_accel]]
phone_xyz_with_timestamp = data[["timestamp",phone_x_accel,phone_y_accel,phone_z_accel]]
label = data[[isRunningLabel]]

X_train,X_test,y_train,y_test=train_test_split(phone_xyz_with_timestamp,label,test_size=0.95,random_state=0) 



prediction = [[12312312,100.001,100.001,100.0001]]
#prediction.
logReg = LogisticRegression(solver='liblinear') 
logReg.fit(X_train,y_train)

#LogisticRegression(random_state=10, max_iter=30000)
#logReg.fit(phone_xyz_with_timestamp, label.values.ravel())
#print(logReg.score(phone_xyz_with_timestamp, label.values.ravel()))



print(logReg.predict(X_test))
