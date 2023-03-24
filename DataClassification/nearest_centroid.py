import numpy as np
import pandas as pd 
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from sklearn.neighbors import NearestCentroid

#region constants
NANOSEC_TO_MINUTE_FACTOR = 60000000000
NUMBER_OF_LABELS = 4
NUMBER_OF_INPUT_PARAMETER = 5 #number of columns (acc_x,acc_y,acc_z,heartrate,stepcounter)
#endregion

#region imported data 
data = pd.read_csv(r"data\combined.csv")
#idle_data = pd.read_csv(r"data\idle_2023-03-17_08_31_24.csv")
#walking_data = pd.read_csv(r"data\walking_2023-03-10_17_17_44.csv")
#cycling_data = pd.read_csv(r"data\cycling_2023-03-10_12_05_37.csv")
#endregion

#region column names
timestamp_as_string = "timestamp"
minute_timestamp_as_string = "minutes"
heartrate_as_string = "heart_rate"
x_accelerometer_as_string = "acc_x"
y_accelerometer_as_string = "acc_y"
z_accelerometer_as_string = "acc_z"
step_count_as_string = 'step_count'
session_id_as_string = 'session_id'
label_as_string = "label"
#endregion


def get_unique_session_ids(data_frame):
    return data_frame.loc[:,session_id_as_string].unique()

#returns a number representing how many rows there should be in the budget time series dataframe
def get_budget_time_series_row_count(data_frame):
    result = 0
    for i in range(NUMBER_OF_LABELS): #number of labels = 4, and they are indexed from 0, so we loop from 0 through 3
        data_frame_with_label = data_frame[(data_frame[label_as_string]==i)]
        if np.isnan(data_frame_with_label.loc[:,minute_timestamp_as_string].max()): #if there are no rows with that label
            continue
        unique_session_ids = get_unique_session_ids(data_frame_with_label)
        for session_id in unique_session_ids:
            data_frame_with_session_id = data_frame_with_label[(data_frame_with_label[session_id_as_string]==session_id)]
            result += data_frame_with_session_id.loc[:,minute_timestamp_as_string].nunique()
    return result 


def get_data_frame_with_label(data_frame,label):
    return data_frame[(data_frame[label_as_string]==label)]

#gets the first step count value at a specific minute interval
def get_step_count_at_minute_interval_start(data_frame, minute):
    return data_frame.loc[data_frame[minute_timestamp_as_string] == minute].iloc[0][step_count_as_string]

def get_mean_accelerometer_data_for_budget_time_series_row(data_frame, accelerometer_type_as_string):
    return data_frame.loc[:,accelerometer_type_as_string].abs().mean()

def get_mean_heart_rate_data_for_budget_time_series_row(data_frame):
    return data_frame.loc[:,heartrate_as_string].mean()

def get_step_count_difference_for_budget_time_series_row(data_frame, initial_step_count):
    return data_frame.iloc[-1, data_frame.columns.get_loc(step_count_as_string)] - initial_step_count

#its return value indicates whether we should increase the index when calling the function the next time (true) or not (false)
#returns true if row was added to time series, if not it is because no data for the time series with that specific label and minute exists
def add_budget_time_series_row(row_index,data_frame_with_label,X,y,label,minute,session_id):
    data_frame_with_session_id = data_frame_with_label[(data_frame_with_label[session_id_as_string]==session_id)]
    data_frame_with_minute = data_frame_with_session_id[(data_frame_with_session_id[minute_timestamp_as_string]==minute)]
    if data_frame_with_minute.empty: #if there is no data for that minute
        return False   
    acc_x_mean = get_mean_accelerometer_data_for_budget_time_series_row(data_frame_with_minute,x_accelerometer_as_string)
    acc_y_mean = get_mean_accelerometer_data_for_budget_time_series_row(data_frame_with_minute,y_accelerometer_as_string)
    acc_z_mean = get_mean_accelerometer_data_for_budget_time_series_row(data_frame_with_minute,z_accelerometer_as_string)
    heart_rate_mean = get_mean_heart_rate_data_for_budget_time_series_row(data_frame_with_minute)
    previous_step_count = get_step_count_at_minute_interval_start(data_frame_with_label,minute)
    step_count_difference = get_step_count_difference_for_budget_time_series_row(data_frame_with_minute,previous_step_count)
    X[row_index] = np.array([acc_x_mean,acc_y_mean,acc_z_mean,heart_rate_mean, step_count_difference])
    y[row_index] = np.array([label])
    return True

def make_budget_time_series_from_data_frame(data_frame):
    row_count = int(get_budget_time_series_row_count(data_frame))
    X = np.empty((row_count,NUMBER_OF_INPUT_PARAMETER))  #row,column
    y = np.empty((row_count,1)) #row,column. 1 is number of columns (label)
    max_minute = int(data_frame.loc[:,minute_timestamp_as_string].nunique())
    budget_time_series_index = 0
    for i in range(NUMBER_OF_LABELS): #for every label (#number of labels = 4, and they are indexed from 0, so we loop from 0 through 3)
        data_frame_with_label = get_data_frame_with_label(data_frame,i)
        print(f"--------------------------label {i}: ------------------------------------------")
        if data_frame_with_label.empty:
            continue
        unique_session_ids = get_unique_session_ids(data_frame_with_label)
        for session_id in unique_session_ids:
            for j in range(max_minute): #for every minute
                #if row was added to time_series 
                if add_budget_time_series_row(budget_time_series_index,data_frame_with_label,X,y,i,j,session_id): 
                    budget_time_series_index += 1
    return X,y

if __name__ == '__main__':
    X,y = make_budget_time_series_from_data_frame(data)
    #X_test_idle, y_test_idle =  make_budget_time_series_from_data_frame(idle_data)
    #X_test_walk, y_test_walk = make_budget_time_series_from_data_frame(walking_data)
    #X_test_cycling, y_test_cycling = make_budget_time_series_from_data_frame(cycling_data)
    
    robust_scaler = preprocessing.RobustScaler()
    X_robust_scaled = robust_scaler.fit_transform(X)
    #X_idle_robust_scaled = robust_scaler.fit_transform(X_test_idle)
    #X_walk_robust_scaled = robust_scaler.fit_transform(X_test_walk)
    #X_cycling_robust_scaled = robust_scaler.fit_transform(X_test_cycling)
    
    min_max_scaler = preprocessing.MinMaxScaler()
    X_minmax_scaled = min_max_scaler.fit_transform(X)
    #X_idle_min_max_scaled = min_max_scaler.fit_transform(X_test_idle)
    #X_walk_min_max_scaled = min_max_scaler.fit_transform(X_test_walk)
    #X_cycling_min_max_scaled = min_max_scaler.fit_transform(X_test_cycling)
    
    scaler = preprocessing.StandardScaler()
    X_standard_scaled = scaler.fit_transform(X)
    
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state=42)
  
    nearest_centroid = NearestCentroid() 
    
    
    nearest_centroid.fit(X_train, np.ravel(y_train))
    #print("accuracy idle:", accuracy_score(nearest_centroid.predict(X_idle_min_max_scaled),np.ravel(y_test_idle)))
    #print("accuracy walking:", accuracy_score(nearest_centroid.predict(X_walk_min_max_scaled),np.ravel(y_test_walk)))
    #print("accuracy cycling:", accuracy_score(nearest_centroid.predict(X_cycling_min_max_scaled),np.ravel(y_test_cycling)))
    
    

    print("accuracy:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))

