import numpy as np
import pandas as pd 
import os
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from sklearn.neighbors import NearestCentroid
from scipy.stats import zscore
import sys

import pyperclip

np.set_printoptions(suppress=True) #make numpy arrays not be printed in scientific notation 
#region constants
NANOSEC_TO_MINUTE_FACTOR = 60000000000
NUMBER_OF_LABELS = 4
NUMBER_OF_INPUT_PARAMETERS = 2 #number of columns (heartrate,stepcounter)
#endregion

#region imported data 
data = pd.read_csv(os.path.join("data","combined.csv"))
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

#returns a number representing how many rows there should be in the budget time series dataframe
def get_budget_time_series_row_count(data_frame):
    result = 0
    previous_result = 0
    for i in range(NUMBER_OF_LABELS): #number of labels = 4, and they are indexed from 0, so we loop from 0 through 3
        data_frame_with_label = data_frame[(data_frame[label_as_string]==i)]
        if np.isnan(data_frame_with_label.loc[:,minute_timestamp_as_string].max()): #if there are no rows with that label
            continue
        unique_session_ids = get_unique_session_ids(data_frame_with_label)
        for session_id in unique_session_ids:
            data_frame_with_session_id = data_frame_with_label[(data_frame_with_label[session_id_as_string]==session_id)]
            result += data_frame_with_session_id.loc[:,minute_timestamp_as_string].nunique()
        centroid_sizes.append(result-previous_result)
        print(result-previous_result) #shows how many minutes of each activity
        previous_result = result
    return result 

def get_data_frame_with_label(data_frame,label):
    return data_frame[(data_frame[label_as_string]==label)]

def get_mean_heart_rate_data_for_budget_time_series_row(data_frame):
    return data_frame.loc[:,heartrate_as_string].mean()

def get_step_count_at_minute_interval_start(data_frame):
    return data_frame.iloc[0][step_count_as_string]

def get_step_count_difference_for_budget_time_series_row(data_frame, initial_step_count):
    return data_frame.iloc[-1, data_frame.columns.get_loc(step_count_as_string)] - initial_step_count

def clean_data_frames_based_on_z_score(data_frame):
    #since the step data is continually getting bigger we do not clean outliers from it, since that would consider all points to be outliers
    data_frame_at_minute_heart_rate = data_frame[[heartrate_as_string]]
    data_frame_at_minute_step = data_frame[[step_count_as_string]]
    z_values = data_frame_at_minute_heart_rate.apply(zscore)
    #according to the z-score a value of higher than 3 or below -3 is considered unusual for a data point and is thus removed
    data_frame_clean_heart_rate = data_frame_at_minute_heart_rate[(z_values < 3).all(axis = 1) & (z_values > -3).all(axis = 1)]
    combined = pd.concat([data_frame_clean_heart_rate,data_frame_at_minute_step],axis=1)
    combined.dropna(inplace = True)
    return combined
    
#its return value indicates whether we should increase the index when calling the function the next time (true) or not (false)
#returns true if row was added to time series, if not it is because no data for the time series with that specific label and minute exists
def add_budget_time_series_row(row_index,data_frame_with_label,X,y,label,minute,session_id):
    data_frame_with_session_id = data_frame_with_label[(data_frame_with_label[session_id_as_string]==session_id)]
    data_frame_at_minute = data_frame_with_session_id[(data_frame_with_session_id[minute_timestamp_as_string]==minute)]
    if data_frame_at_minute.empty: #if there is no data for that minute
        return False   
    
    cleaned_data_frame = clean_data_frames_based_on_z_score(data_frame_at_minute)
    if cleaned_data_frame.empty: #if all data for that minute are outliers
        return False
    
    heart_rate_mean = get_mean_heart_rate_data_for_budget_time_series_row(cleaned_data_frame)
    first_step_count_at_minute_interval = get_step_count_at_minute_interval_start(cleaned_data_frame)
    step_count_difference = get_step_count_difference_for_budget_time_series_row(cleaned_data_frame,first_step_count_at_minute_interval)
    X[row_index] = np.array([heart_rate_mean, step_count_difference])
    y[row_index] = np.array([label])
    return True

def make_budget_time_series_from_data_frame(data_frame):
    row_count = int(get_budget_time_series_row_count(data_frame))
    X = np.empty((row_count,NUMBER_OF_INPUT_PARAMETERS))  #row,column
    y = np.empty((row_count,1)) #row,column. 1 is number of columns (label)
    max_minute = int(data_frame.loc[:,minute_timestamp_as_string].nunique())
    budget_time_series_index = 0
    for i in range(NUMBER_OF_LABELS): #for every label (#number of labels = 4, and they are indexed from 0, so we loop from 0 through 3)
        data_frame_with_label = get_data_frame_with_label(data_frame,i)
        print(f"label {i}:")
        if data_frame_with_label.empty:
            continue
        unique_session_ids = get_unique_session_ids(data_frame_with_label)
        for session_id in unique_session_ids:
            for j in range(max_minute): #for every minute
                #if row was added to time_series 
                if add_budget_time_series_row(budget_time_series_index,data_frame_with_label,X,y,i,j,session_id): 
                    budget_time_series_index += 1
    return X,y

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

#for some reason after running the make_budget_time_series_from_data_frame we may end up with some very low y values / very high X values
#it is usually the final 2 values (i think) - or not any values (but this function does not take this into account)
#should probably do some debugging on make_budget_time_series_from_data_frame instead - but we do this for now
def remove_outliers_from_time_series(X,y):
    i = 0
    while i < len(X): #X and y have the same length, so it does not matter which one to pick
        heart_rate = X[i][0]
        step_count = X[i][1]
        label = y[i]
        #value should be removed from both lists - since they should keep the same size
        #HR nor step rate should never get this high/low and label should never be between 0 and 1 or higher than 3(should be enough to seperate outliers)
        if heart_rate > 10000 or heart_rate < 0.0001 or step_count > 10000 or (step_count < 0.0001 and step_count != 0) or (label < 1 and label != 0) or label > 3:
            X = np.delete(X,i,axis=0)
            y = np.delete(y,i,axis=0)
        else:
            i += 1
            
def convertScikitCentroidsToOurCentroids(centroids):
    if len(centroids) == 5: #hacky solution, but there is a chance that there is an extra centroid which is very high value (as the first centroid)
        centroids = centroids[1:]
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
    X,y = make_budget_time_series_from_data_frame(data)
    
    robust_scaler = preprocessing.RobustScaler()
    X_robust_scaled = robust_scaler.fit_transform(X)
    
    min_max_scaler = preprocessing.MinMaxScaler()
    X_minmax_scaled = min_max_scaler.fit_transform(X)
    
    scaler = preprocessing.StandardScaler()
    X_standard_scaled = scaler.fit_transform(X)
    
    remove_outliers_from_time_series(X,y)
    y = y.astype(int) #converts labels to ints - this is done after removing the outliers
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=42)
    nearest_centroid = NearestCentroid() 
    nearest_centroid.fit(X_train, np.ravel(y_train))
    centroids = convertScikitCentroidsToOurCentroids(nearest_centroid.centroids_)
    
        

    pyperclip.copy(format_final_centroid_to_java(centroids))
        
   
    print("accuracy:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))
    