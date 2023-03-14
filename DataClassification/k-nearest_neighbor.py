import numpy as np
import pandas as pd 
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from skl2onnx import to_onnx
from tslearn.metrics import dtw
from sklearn.neighbors import NearestCentroid

#region imported data 
pamapData = pd.read_csv(r"data\pamapdataWithHR.csv")
recordedWalkingData = pd.read_csv(r"data\walking_2023-03-02_16_43_58.csv")
ourWalking = pd.read_csv(r"data\ourData\walking_2023-03-10_13_54_52.csv")
ourCycling = pd.read_csv(r"data\ourData\cycling_2023-03-11_11_22_01.csv")
ourRunning = pd.read_csv(r"data\ourData\running_2023-03-12_06_10_19.csv")
ourIdle = pd.read_csv(r"data\ourData\idle_2023-03-10_11_33_21.csv")

own_heartrate = "heart_rate"
own_x_accelerometer = "acc_x"
own_y_accelerometer = "acc_y"
own_z_accelerometer = "acc_z"
ownLabel = "label"
own_timestamp = "timestamp"
NANOSEC_TO_MINUTE_FACTOR = 60000000000

def resetNumpyArrays(accXData,accYData,accZData,heartRateData):
    accXData = np.array([])
    accYData = np.array([])
    accZData = np.array([])
    heartRateData = np.array([])

def makeTimeSeries(file):
    hstacks = np.empty([])
    firstTimestamp = -1
    accXData = None
    accYData = None
    accZData = None
    heartRateData = None
    resetNumpyArrays(accXData,accYData,accZData,heartRateData)
    i = 0
    for index,row in file.iterrows():
        accXData = np.append(accXData,row[own_x_accelerometer])
        accYData = np.append(accYData,row[own_y_accelerometer])
        accZData = np.append(accZData,row[own_z_accelerometer])
        heartRateData = np.append(heartRateData,row[own_heartrate])       
        timestamp = row[own_timestamp]
        if firstTimestamp == -1:
            firstTimestamp = timestamp
        elif timestamp - firstTimestamp >= 1 * NANOSEC_TO_MINUTE_FACTOR:
            hstacks = np.append(hstacks,np.hstack((accXData,accYData,accZData,heartRateData)))
            resetNumpyArrays(accXData,accYData,accZData,heartRateData)
            i+=1
    return hstacks

ourIdleX = makeTimeSeries(ourIdle)
ourIdleY = np.empty(len(ourIdleX)); ourIdleY.fill(0) #idle label = 0
# ourWalkingX = makeTimeSeries(ourWalking)
# ourWalkingY = np.empty(len(ourWalkingX)); ourWalkingY.fill(1)  #walking label = 1
# ourRunningX = makeTimeSeries(ourRunning)
# ourRunningY = np.empty(len(ourRunningX)); ourRunningY.fill(2)  #running label = 2
# ourCyclingX = makeTimeSeries(ourCycling)
# ourCyclingY = np.empty(len(ourCyclingX)); ourCyclingY.fill(3)  #cycling label = 3

print(type(ourIdleX))

knn = KNeighborsClassifier(metric=dtw,n_neighbors=400)
knn.fit(ourIdleX, ourIdleY)


            





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
#endregion 

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
knn = KNeighborsClassifier(metric=dtw,n_neighbors=400)
print(X_train_minmax)
#Fill the model with data
knn.fit(X_train_minmax, y_train.values.ravel())


print("Walking dataset:", accuracy_score(knn.predict(walking_x),walking_y))
