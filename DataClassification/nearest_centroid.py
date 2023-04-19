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
TIMESERIES_LENGTH = 1
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
        for i in range(len(centroid)):
            if i == 2:
                result += "(byte) "
            result += str(centroid[i])
            result += ", "
        result = result.rstrip(", ")#removes trailing comma
        result += "),"
     
    result = result.rstrip(", ") #removes trailing comma
    result += "};"
    return result     
       
def convertScikitCentroidsToOurCentroids(centroids):
    final_centroids = []
    for i in range(len(centroids)):
        centroid = []
        centroid.append(centroids[i][0]) #adds label
        centroid.append(centroids[i][1]) #adds centroid size (number of datapoints(minutes) for that centroid) 
        centroid.append(i)
        centroid.append(centroid_sizes[i])
        final_centroids.append(centroid)
    return final_centroids
        
if __name__ == '__main__':
    data = pd.read_csv(os.path.join("data","combined.csv"))
    for i in range(1,6):
        TIMESERIES_LENGTH = i
        X,y = make_aggregated_time_series(data)
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=42)
        nearest_centroid = NearestCentroid() 
        nearest_centroid.fit(X_train, np.ravel(y_train))
        centroids = convertScikitCentroidsToOurCentroids(nearest_centroid.centroids_)

        pyperclip.copy(format_final_centroid_to_java(centroids))

        print(f"accuracy for timeseries length at size {TIMESERIES_LENGTH}:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))
    