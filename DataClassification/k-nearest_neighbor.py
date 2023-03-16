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
ourWalking2 = pd.read_csv(r"data\ourData\walking_2023-03-16_12_30_34.csv")
ourCycling = pd.read_csv(r"data\ourData\cycling_2023-03-11_11_22_01.csv")
ourRunning = pd.read_csv(r"data\ourData\running_2023-03-12_06_10_19.csv")
ourRunning2 = pd.read_csv(r"data\ourData\running_2023-03-16_12_48_25.csv")
ourIdle = pd.read_csv(r"data\ourData\idle_2023-03-10_11_33_21.csv")
ourIdle2 = pd.read_csv(r"data\ourData\idle_2023-03-16_10_59_38.csv")
ourIdle3 = pd.read_csv(r"data\ourData\idle_2023-03-16_13_00_55.csv")
test = pd.read_csv(r"data\ourData\test.csv")

own_heartrate = "heart_rate"
own_x_accelerometer = "acc_x"
own_y_accelerometer = "acc_y"
own_z_accelerometer = "acc_z"
ownLabel = "label"
own_timestamp = "timestamp"
minute_timestamp = "minutes"
NANOSEC_TO_MINUTE_FACTOR = 60000000000

def resetNumpyArrays(accXData,accYData,accZData,heartRateData):
    accXData = np.array([])
    accYData = np.array([])
    accZData = np.array([])
    heartRateData = np.array([])

def makeTimeSeries(file):
    #file.replace(to_replace=pd.NA, value=0, inplace=True)
    #file.replace(to_replace=None, value=0, inplace=True)
    maxMinute = file.loc[:,minute_timestamp+1].max()
    hstacks = []
    labels = []
    firstTimestamp = -1
    accXData = None
    accYData = None
    accZData = None
    heartRateData = None
    resetNumpyArrays(accXData,accYData,accZData,heartRateData)
    maxMinute = file.loc[:,minute_timestamp].max()  #file[file[minute_timestamp]].max()
    for i in range(maxMinute+1):
        #try:
        subFile = file[(file[minute_timestamp]==i)]
        accXData = subFile.loc[:,own_x_accelerometer]
        accYData = subFile.loc[:,own_y_accelerometer]
        accZData = subFile.loc[:,own_z_accelerometer]
        heartRateData = subFile.loc[:,own_heartrate]
        labelData = subFile.loc[:,ownLabel]
        #except KeyError:
        #    continue
        hstacks.append(np.hstack((accXData,accYData,accZData,heartRateData)))
        labels.append(labelData)
        #hstacks = np.stack((hstacks,np.hstack((accXData,accYData,accZData,saa  heartRateData))))
        #hstacks[i] = np.hstack((accXData,accYData,accZData,heartRateData))
        #print(hstacks)
        #labels = np.stack((labels,labelData))
        resetNumpyArrays(accXData,accYData,accZData,heartRateData)
    #print(hstacks)
    return hstacks, labels

def getRows(file):
    result = 0
    for i in range(4):
        subFile = file[(file[ownLabel]==i)]
        if np.isnan(subFile.loc[:,minute_timestamp].max()):
            continue
        result += subFile.loc[:,minute_timestamp].max()
    return result 

    
def makeBudgetTimeSeries(file):
    firstRun = True
    rowCount = getRows(file)
    rows = np.empty((rowCount+1,4))
    rows[0] = [1,2,3,4]
    labels = np.empty((rowCount+1,1))
    #label = file[ownLabel].iloc[0]
    accXMean = None
    accYMean = None
    accZMean = None
    heartRateMean = None
    resetNumpyArrays(accXMean,accYMean,accZMean,heartRateMean)
    maxMinute = file.loc[:,minute_timestamp].max() 
    counter = 0
    for i in range(4):
        subFile = file[(file[ownLabel]==i)]
        for j in range(maxMinute+1):
            subSubFile = subFile[(file[minute_timestamp]==j)]
            accXMean = subSubFile.loc[:,own_x_accelerometer].abs().mean()
            accYMean = subSubFile.loc[:,own_y_accelerometer].abs().mean()
            accZMean = subSubFile.loc[:,own_z_accelerometer].abs().mean()
            heartRateMean = subSubFile.loc[:,own_heartrate].mean()
            if (np.isnan(accXMean)):
                continue
            # if firstRun:
            #     rows = np.array([accXMean,accYMean,accZMean,heartRateMean])
            #     labels = np.array([i])
            #     firstRun = False
            #     continue
            #print("rows:",rows)
            #print("next array: ",np.array([accXMean,accYMean,accZMean,heartRateMean]))
            rows[counter] = np.array([accXMean,accYMean,accZMean,heartRateMean])
            labels[counter] = np.array([i])
            
            counter += 1
            
            
            #rows = np.append(rows,np.array([accXMean,accYMean,accZMean,heartRateMean]), axis=0)
            #print(rows)
            #rows[i] = np.array([accXMean,accYMean,accZMean,heartRateMean])
            #labels = np.append(labels,np.array([i]), axis=0)
            #labels[i] = label
    return rows,labels


testX,testY = makeBudgetTimeSeries(test)
X_train, X_test, y_train, y_test = train_test_split(testX, testY, test_size = 0.5, random_state=42)

#ourIdleX,ourIdleY = makeBudgetTimeSeries(ourIdle)
#ourIdleX2,ourIdleY2 = makeBudgetTimeSeries(ourIdle2)
#ourWalkingX,ourWalkingY = makeBudgetTimeSeries(ourWalking2)

#print("running:")
#ourRunningX,ourRunningY = makeBudgetTimeSeries(ourRunning2)

knn = NearestCentroid()

#print("x:",testX)
#print("y:",testY)
knn.fit(X_train, np.ravel(y_train,order='C'))
#knn.fit(ourWalkingX,np.ravel(ourWalkingY,order='C'))

print("accuracy:", accuracy_score(knn.predict(X_test),np.ravel(y_test,order='C')))

quit()
    # for index,row in file.iterrows():
    #     accXData = np.append(accXData,row[own_x_accelerometer])
    #     accYData = np.append(accYData,row[own_y_accelerometer])
    #     accZData = np.append(accZData,row[own_z_accelerometer])
    #     heartRateData = np.append(heartRateData,row[own_heartrate])       
    #     timestamp = row[own_timestamp]
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



knn = KNeighborsClassifier(metric=dtw,n_neighbors=4)

print(type(ourIdleY))
#ourIdleX = np.reshape(ourIdleX,(-1,1))

print(ourIdleX)
knn.fit(ourIdleX, ourIdleY)

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
