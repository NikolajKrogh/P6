import numpy as np
import pandas as pd 
import os
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.neighbors import NearestCentroid
import sys

import pyperclip

np.set_printoptions(suppress=True) #makes numpy arrays not be printed in scientific notation
np.set_printoptions(threshold=sys.maxsize) #makes numpy arrays be fully printed instead of with "..."
 
#region constants
NANOSEC_TO_MINUTE_FACTOR = 60000000000
NUMBER_OF_LABELS = 4
NUMBER_OF_INPUT_PARAMETERS = 2 #number of columns (heartrate,stepcounter)
TIME_WINDOW_LENGTH = 1
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
    def __init__(self,heart_rate,step_count,label,size):
        self.heart_rate = heart_rate
        self.step_count = step_count
        self.label = label
        self.size = size
        self.max_step_count = 0
        self.max_heart_rate = 0
        self.min_step_count = 0
        self.min_heart_rate = 0
    
    
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
            excessMinutes = minutes % TIME_WINDOW_LENGTH
            #if the total minutes is divisible by the window length, we still want to remove the last minute since that is not a full minute
            if excessMinutes == 0:
                dataframe_with_session_id = dataframe_with_session_id.loc[dataframe_with_session_id[minute_timestamp_as_string]<minutes-1]
            #remove excess minutes if they are not divisible by the timeseries length
            dataframe_with_session_id = dataframe_with_session_id.loc[dataframe_with_session_id[minute_timestamp_as_string]<minutes-excessMinutes]
            minutes = dataframe_with_session_id.loc[:,minute_timestamp_as_string].nunique() #update minutes value
            i = 0
            while i < minutes:
                add_aggregated_time_window(X,y,dataframe_with_session_id,i,label)
                i+=TIME_WINDOW_LENGTH
        centroid_sizes.append(centroid_size)
    return np.asarray(X),np.asarray(y)
                          
def add_aggregated_time_window(X,y,data_frame,startMinute,label):
    data_frame_at_minutes = data_frame.loc[(data_frame[minute_timestamp_as_string] >= startMinute) & 
                                             (data_frame[minute_timestamp_as_string] < startMinute+TIME_WINDOW_LENGTH )]
    heart_rate_mean = data_frame_at_minutes.loc[:, heartrate_as_string].mean()
    initial_step_count = data_frame_at_minutes[step_count_as_string].min()
    step_count_difference = data_frame_at_minutes[step_count_as_string].max() - initial_step_count
    X.append([heart_rate_mean,step_count_difference])
    y.append([label])

def get_data_frame_with_label(data_frame,label):
    return data_frame[(data_frame[label_as_string]==label)]     
       
def convert_scikit_centroids_to_our_centroids(centroids):
    all_centroids = []
    for i in range(len(centroids)):
        centroid = Centroid(centroids[i][0],centroids[i][1],i,centroid_sizes[i])
        all_centroids.append(centroid)
    return all_centroids

#Here the min and max value is added from the aggregated datapoints (X and y)
def add_min_max_step_count_data_to_centroids(centroids, X, y):
    offset = 0
    for i in range(NUMBER_OF_LABELS):
        labels = y[np.where(y==i)]
        data_points = X[offset:offset + len(labels)]
        offset += len(labels)
        #axis=0 means we compare all values in a column
        centroids[i].max_step_count = data_points.max(axis=0)[1]
        centroids[i].min_step_count = data_points.min(axis=0)[1]
        
#Here the min and max value is added from the aggregated datapoints (data)
def add_min_max_heart_rate_data_to_centroids(centroids,X,y):
    offset = 0
    for i in range(NUMBER_OF_LABELS):
        labels = y[np.where(y==i)]
        data_points = X[offset:offset + len(labels)]
        offset += len(labels)
        #axis=0 means we compare all values in a column
        centroids[i].max_heart_rate = data_points.max(axis=0)[0]
        centroids[i].min_heart_rate = data_points.min(axis=0)[0]
        
def format_final_centroid_to_java(centroids):
    result = "{"
    for centroid in centroids:
        result += "new Centroid("
        result += f"{centroid.heart_rate},"
        result += f"{centroid.min_heart_rate},"
        result += f"{centroid.max_heart_rate},"
        result += f"{centroid.step_count},"
        result += f"{centroid.min_step_count},"
        result += f"{centroid.max_step_count},"
        result += "(byte) "
        result += f"{centroid.label},"
        result += f"{centroid.size}"
        result += "),"    
    result = result.rstrip(", ") #removes trailing comma
    result += "};"
    return result    

def makeOverleafPoints(X,y):
    #red = sit, green = walk, violet = run, lime = bike
    colours = ["red", "green", "violet", "lime"]
    result = ""
    for i, value in enumerate(X):
        result += f"\\filldraw[{colours[y[i][0]]}] ({value[0]},{value[1]}) circle (0.2pt) node[anchor=north] {{}}; \n"          
    return result

if __name__ == '__main__':
    data = pd.read_csv(os.path.join("data","combined.csv"))
    X,y = make_aggregated_time_series(data)
    #pyperclip.copy(makeOverleafPoints(X,y))
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=42)
    nearest_centroid = NearestCentroid() 
    nearest_centroid.fit(X, np.ravel(y))
    centroids = convert_scikit_centroids_to_our_centroids(nearest_centroid.centroids_)
    add_min_max_step_count_data_to_centroids(centroids,X,y)
    add_min_max_heart_rate_data_to_centroids(centroids, X,y)

    pyperclip.copy(format_final_centroid_to_java(centroids))
   
    #print("accuracy:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))
    