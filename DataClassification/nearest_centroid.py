import numpy as np
import pandas as pd 
import os
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.neighbors import NearestCentroid
import sys

import pyperclip

np.set_printoptions(suppress=True) #make numpy arrays not be printed in scientific notation
np.set_printoptions(threshold=sys.maxsize) #make numpy arrays be fully printed instead of with "..."
 
#region constants
NANOSEC_TO_MINUTE_FACTOR = 60000000000
NUMBER_OF_LABELS = 4
NUMBER_OF_INPUT_PARAMETERS = 2 #number of columns (heartrate,stepcounter)
TIMESERIES_LENGTH = 3
#endregion

#region column names
timestamp_as_string = "timestamp"
minute_timestamp_as_string = "minutes"
heartrate_as_string = "heart_rate"
step_count_as_string = 'step_count'
session_id_as_string = 'session_id'
label_as_string = "label"
#endregion

centroid_sizes = []


class Centroid:
    def __init__(self,stepCount,heartRate,label,size):
        self.stepCount = stepCount
        self.heartRate = heartRate
        self.label = label
        self.size = size
        self.maxStepCount = 0
        self.maxHeartRate = 0
        self.minStepCount = 0
        self.minHeartRate = 0
    
    
def get_unique_session_ids(data_frame):
    return data_frame.loc[:,session_id_as_string].unique()

def make_aggregated_time_series(data):
    X = []
    y = []
    for label in range(NUMBER_OF_LABELS):
        centroid_size = 0
        data_frame_with_label = get_data_frame_with_label(data,label)
        if data_frame_with_label.empty:
            continue
        unique_session_ids = get_unique_session_ids(data_frame_with_label)
        for session_id in unique_session_ids:
            dataframe_with_session_id = data_frame_with_label.loc[data_frame_with_label[session_id_as_string] == session_id]
            minutes = dataframe_with_session_id.loc[:,minute_timestamp_as_string].nunique()
            centroid_size += minutes
            excessMinutes = minutes % TIMESERIES_LENGTH
            #remove excess minutes if they are not divisible by the timeseries length
            dataframe_with_session_id = dataframe_with_session_id.loc[dataframe_with_session_id[minute_timestamp_as_string]<minutes-excessMinutes]
            minutes = dataframe_with_session_id.loc[:,minute_timestamp_as_string].nunique() #update minutes value
            i = 0
            while i < minutes:
                add_aggregated_time_window(X,y,dataframe_with_session_id,i,label)
                i+=TIMESERIES_LENGTH
        centroid_sizes.append(centroid_size)
    return np.asarray(X),np.asarray(y)
                          
def add_aggregated_time_window(X,y,data_frame,startMinute,label):
    data_frame_at_minutes = data_frame.loc[(data_frame[minute_timestamp_as_string] >= startMinute) & 
                                             (data_frame[minute_timestamp_as_string] < startMinute+TIMESERIES_LENGTH )]
    heart_rate_mean = data_frame_at_minutes.loc[:, heartrate_as_string].mean()
    initial_step_count = data_frame_at_minutes[step_count_as_string].min()
    step_count_difference = data_frame_at_minutes[step_count_as_string].max() - initial_step_count
    X.append([heart_rate_mean,step_count_difference])
    y.append([label])

def get_data_frame_with_label(data_frame,label):
    return data_frame[(data_frame[label_as_string]==label)]

def format_final_centroid_to_java(centroids):
    result = "{"
    for centroid in centroids:
        result += "new Centroid("
        result += str(centroid.heartRate)
        result += ","
        result += str(centroid.minHeartRate)
        result += ","
        result += str(centroid.maxHeartRate)
        result += ","
        result += str(centroid.stepCount)
        result += ","
        result += str(centroid.minStepCount)
        result += ","
        result += str(centroid.maxStepCount)
        result += ","
        result += "(byte) "
        result += str(centroid.label)
        result += ","
        result += str(centroid.size)
        result += "),"    
    result = result.rstrip(", ") #removes trailing comma
    result += "};"
    return result     
       
def convertScikitCentroidsToOurCentroids(centroids):
    all_centroids = []
    for i in range(len(centroids)):
        centroid = Centroid(centroids[i][0],centroids[i][1],i,centroid_sizes[i])
        all_centroids.append(centroid)
    return all_centroids

def addEllipsesDataToCentroids(centroids, X, y):
    offset = 0
    for i in range(NUMBER_OF_LABELS):

        labels = y[np.where(y==i)]
        dataPoints = X[offset:offset + len(labels)]
        offset += len(labels)
        centroids[i].maxHeartRate, centroids[i].maxStepCount = dataPoints.max(axis=0)
        centroids[i].minHeartRate, centroids[i].minStepCount = dataPoints.min(axis=0)
    
        
if __name__ == '__main__':
    data = pd.read_csv(os.path.join("data","combined.csv"))
    X,y = make_aggregated_time_series(data)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=42)
    nearest_centroid = NearestCentroid() 
    nearest_centroid.fit(X_train, np.ravel(y_train))
    centroids = convertScikitCentroidsToOurCentroids(nearest_centroid.centroids_)
    addEllipsesDataToCentroids(centroids,X,y)

    
    pyperclip.copy(format_final_centroid_to_java(centroids))
   
    print("accuracy:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))
    