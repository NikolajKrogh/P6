import numpy as np
import pandas as pd 
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from tslearn.metrics import dtw
from sklearn.neighbors import NearestCentroid

#region constants
NANOSEC_TO_MINUTE_FACTOR = 60000000000
NUMBER_OF_LABELS = 4
#endregion

#region imported data 
data = pd.read_csv(r"data\test.csv")
#endregion

#region column names
timestamp_as_string = "timestamp"
minute_timestamp_as_string = "minutes"
heartrate_as_string = "heart_rate"
x_accelerometer_as_string = "acc_x"
y_accelerometer_as_string = "acc_y"
z_accelerometer_as_string = "acc_z"
step_count_as_string = 'step_count'
label_as_string = "label"
#endregion

#returns a number representing how many rows there should be in the budget time series dataframe
def get_budget_time_series_row_count(data_frame):
    result = 0
    for i in range(NUMBER_OF_LABELS): #number of labels = 4, and they are indexed from 0, so we loop from 0 through 3
        data_frame_with_label = data_frame[(data_frame[label_as_string]==i)]
        if np.isnan(data_frame_with_label.loc[:,minute_timestamp_as_string].max()): #if there are not rows with that label
            continue
        result += data_frame_with_label.loc[:,minute_timestamp_as_string].nunique()
    return result 

def get_data_frame_with_label(data_frame,label):
    return data_frame[(data_frame[label_as_string]==label)]

def get_initial_step_count(data_frame):
    return data_frame.iloc[0, data_frame.columns.get_loc(step_count_as_string)]

def get_mean_accelerometer_data_for_budget_time_series_row(data_frame, accelerometer_type_as_string):
    return data_frame.loc[:,accelerometer_type_as_string].abs().mean()

def get_mean_heart_rate_data_for_budget_time_series_row(data_frame):
    return data_frame.loc[:,heartrate_as_string].mean()

def get_step_count_difference_for_budget_time_series_row(data_frame, initial_step_count):
    return data_frame.iloc[-1, data_frame.columns.get_loc(step_count_as_string)] - initial_step_count

#its return value indicates whether we should increase the index when calling the function the next time (true) or not (false)
#returns true if row was added to time series, if not it is because no data for the time series with that specific label and minute exists
def add_budget_time_series_row(row_index,data_frame_with_label,initial_step_count,X,y,label,minute):
    data_frame_with_minute = data_frame_with_label[(data_frame_with_label[minute_timestamp_as_string]==minute)]
    acc_x_mean = get_mean_accelerometer_data_for_budget_time_series_row(data_frame_with_minute,x_accelerometer_as_string)
    if (np.isnan(acc_x_mean)): #if the mean of any data is nan, we know that there is no data for that minute
        return False
    acc_y_mean = get_mean_accelerometer_data_for_budget_time_series_row(data_frame_with_minute,y_accelerometer_as_string)
    acc_z_mean = get_mean_accelerometer_data_for_budget_time_series_row(data_frame_with_minute,z_accelerometer_as_string)
    heart_rate_mean = get_mean_heart_rate_data_for_budget_time_series_row(data_frame_with_minute)
    step_count_difference = get_step_count_difference_for_budget_time_series_row(data_frame_with_minute,initial_step_count)
    X[row_index] = np.array([acc_x_mean,acc_y_mean,acc_z_mean,heart_rate_mean, step_count_difference])
    y[row_index] = np.array([label])
    return True

def make_budget_time_series_from_data_frame(data_frame):
    row_count = int(get_budget_time_series_row_count(data_frame))
    X = np.empty((row_count,5))  #row,column. 5 is number of columns (acc_x,acc_y,acc_z,heartrate,stepcounter)
    y = np.empty((row_count,1)) #row,column. 1 is number of columns (label)
    max_minute = int(data_frame.loc[:,minute_timestamp_as_string].nunique())
    budget_time_series_index = 0
    for i in range(NUMBER_OF_LABELS): #for every label (#number of labels = 4, and they are indexed from 0, so we loop from 0 through 3)
        data_frame_with_label = get_data_frame_with_label(data_frame,i)
        if data_frame_with_label.empty:
            continue
        initial_step_count = get_initial_step_count(data_frame_with_label)
        for j in range(max_minute): #for every minute
            if add_budget_time_series_row(budget_time_series_index,data_frame_with_label,initial_step_count,X,y,i,j): #if row was added to time_series 
                budget_time_series_index += 1
    return X,y

if __name__ == '__main__':
    X,y = make_budget_time_series_from_data_frame(data)
    
    robust_scaler = preprocessing.RobustScaler()
    X_robust_scaled = robust_scaler.fit_transform(X)
    
    min_max_scaler = preprocessing.MinMaxScaler()
    X_minmax_scaled = min_max_scaler.fit_transform(X)
    
    scaler = preprocessing.StandardScaler()
    X_standard_scaled = scaler.fit_transform(X)
    
    
    X_train, X_test, y_train, y_test = train_test_split(X_robust_scaled, y, test_size = 0.25, random_state=42)
    nearest_centroid = NearestCentroid()

    nearest_centroid.fit(X_train, np.ravel(y_train))

    print("accuracy:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))



"""
todo:
prøv accuracy med enkelte labels
try with more data (both fit and test)
"""