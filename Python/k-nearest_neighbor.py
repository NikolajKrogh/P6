import numpy as np
import pandas as pd 
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from skl2onnx import to_onnx
import onnx
import onnxruntime as rt

#region imported data 
pamapData = pd.read_csv(r"data\pamapdataWithHR.csv")
recordedWalkingData = pd.read_csv(r"data\walking_2023-03-02_16_43_58.csv")

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

#Build the classifier (algorithm)
knn = KNeighborsClassifier(n_neighbors=400)
#Fill the model with data
knn.fit(X_train_minmax, y_train.values.ravel())
X = X_pamap_no_heart.to_numpy()
print(X)

#Save the knn model to a binary file
filename = 'knn_model.onnx'

#X is a numpy array and target_opset is saying which version of onnx we should use
onnx = to_onnx(knn, X.astype(np.float32), target_opset=12)

#save the serialized knn model 
with open( "knn_model.onnx", "wb" ) as f:
    f.write( onnx.SerializeToString())

#Load the serialized model and make new predictions
session = rt.InferenceSession('knn_model.onnx')
input_name = session.get_inputs()[0].name
onnx_prediction = session.run(None, {input_name:X_test_scaled.astype(np.float32)[:100]})
print(onnx_prediction)
#loaded_model = onnx.load('knn_model.onnx')
#print(onnx.checker.check_model(loaded_model))


print("Walking dataset:", accuracy_score(knn.predict(walking_x),walking_y))
