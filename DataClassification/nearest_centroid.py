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
    testX,testY = make_budget_time_series_from_data_frame(data)
    X_train, X_test, y_train, y_test = train_test_split(testX, testY, test_size = 0.25, random_state=42)
    nearest_centroid = NearestCentroid()

    nearest_centroid.fit(X_train, np.ravel(y_train))

    print("accuracy:", accuracy_score(nearest_centroid.predict(X_test),np.ravel(y_test)))



"""
todo:
make pretty
prøv accuracy med enkelte labels
prøv med min max scaling / standard scaling
"""
quit()
    # for index,row in file.iterrows():
    #     accXData = np.append(accXData,row[x_accelerometer])
    #     accYData = np.append(accYData,row[y_accelerometer])
    #     accZData = np.append(accZData,row[z_accelerometer])
    #     heartRateData = np.append(heartRateData,row[heartrate])       
    #     timestamp = row[timestamp]
    #     if firstTimestamp == -1:
    #         firstTimestamp = timestamp
    #     elif timestamp - firstTimestamp >= 1 * NANOSEC_TO_MINUTE_FACTOR:
    #         hstacks = np.append(hstacks,np.hstack((accXData,accYData,accZData,heartRateData)))
    #         resetNumpyArrays(accXData,accYData,accZData,heartRateData)
    #         i+=1
    # return hstacks

ourIdleX,ourIdleY = makeTimeSeries(ourIdle)
ourIdleX2,ourIdleY2 = makeTimeSeries(ourIdle2)
# ourIdleY = np.empty(len(ourIdleX)); ourIdleY.fill(0) #idle label = 0
# ourWalkingX = makeTimeSeries(ourWalking)
# ourWalkingY = np.empty(len(ourWalkingX)); ourWalkingY.fill(1)  #walking label = 1
#ourRunningX,ourRunningY = makeTimeSeries(ourRunning)
#ourRunningY = np.empty(len(ourRunningX)); ourRunningY.fill(2)  #running label = 2
# ourCyclingX = makeTimeSeries(ourCycling)
# ourCyclingY = np.empty(len(ourCyclingX)); ourCyclingY.fill(3)  #cycling label = 3



nearest_centroid = KNeighborsClassifier(metric=dtw,n_neighbors=4)

print(type(ourIdleY))
#ourIdleX = np.reshape(ourIdleX,(-1,1))

print(ourIdleX)
nearest_centroid.fit(ourIdleX, ourIdleY)

#print("Walking dataset:", accuracy_score(knn.predict(ourIdleX2),ourIdleY2))

            





quit()

walking_heartrate = "heartrate"
walking_x_accelerometer = "acc_x"
walking_y_accelerometer = "acc_y"
walking_z_accelerometer = "acc_z"
walkingLabel = "label"

walking_x = recordedWalkingData[[walking_x_accelerometer,walking_y_accelerometer,walking_z_accelerometer]]
walking_y = recordedWalkingData[[walkingLabel]]


pamapx = "3d_accel_x_2"
pamapy = "3d_accel_y_2"
pamapz = "3d_accel_z_2"
pamapheart = "heartrate"
pamaplabel = "activityType"

X_pamap_no_heart = pamapData[[pamapx,pamapy,pamapz]]
X_pamap = pamapData[[pamapx,pamapy,pamapz,pamapheart]]
Y_pamap = pamapData[[pamaplabel]]

X_train, X_test, y_train, y_test = train_test_split(X_pamap_no_heart, Y_pamap, test_size = 0.2, random_state=42)

#region DTW with one dimension
s1 = np.array(recordedWalkingData[[walking_x_accelerometer]], dtype=object)
s2 = np.array(pamapData[[pamapx]], dtype=object)
#set_array_len = min(len(s1), len(s2))
#s1 = s1.ravel()
#s2 = s2.ravel()
#s1 = s1[:set_array_len]
#s2 = s2[:set_array_len]
#path = dtw.warping_path(s1, s2)
#dtwvis.plot_warping(s1, s2, path, filename="warp.png")
#distance, paths = dtw.warping_paths(s1, s2)
#print(distance)
#print(paths)
#endregion

min_max_scaler = preprocessing.MinMaxScaler()
scaler = preprocessing.StandardScaler().fit(X_train)
X_train_scaled = scaler.transform(X_train)
X_train_minmax = min_max_scaler.fit_transform(X_train)
scaler = preprocessing.StandardScaler().fit(X_test)
X_test_scaled = scaler.transform(X_test)
X_test_minmax = min_max_scaler.fit_transform(X_test)


def createTs(myStart, myLength):
    index = pd.date_range(myStart, periods=myLength, freq='H'); 
    values= [random.random() for _ in range(myLength)]
    series = pd.Series(values, index=index);  
    return(series)

#Build the classifier (algorithm)
nearest_centroid = KNeighborsClassifier(metric=dtw,n_neighbors=400)
print(X_train_minmax)
#Fill the model with data
nearest_centroid.fit(X_train_minmax, y_train.values.ravel())


print("Walking dataset:", accuracy_score(nearest_centroid.predict(walking_x),walking_y))
