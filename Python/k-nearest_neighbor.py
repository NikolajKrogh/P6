import numpy as np
import pandas as pd 
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import preprocessing
from dtaidistance import dtw_visualisation as dtwvis
from dtaidistance import dtw
from scipy.spatial import distance
from tslearn.metrics import dtw
import pickle
from onnx import TensorProto
from onnx.helper import (
    make_model, make_node, make_graph,
    make_tensor_value_info)
from onnx.checker import check_model
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType 

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

#Build the algorithm
knn = KNeighborsClassifier(metric=dtw, n_neighbors=400)
#Fill the model with data
knn.fit(X_train_minmax, y_train.values.ravel())


initial_type = [('float_input', FloatTensorType([None, 4]))]

#Save the knn model to a binary file
filename = 'knn_model.onnx'
converted_model = convert_sklearn(knn, initial_types=initial_type)
with open( "knn_model.onnx", "wb" ) as f:
    f.write( converted_model.SerializeToString() )



#pickle.dump(knn, open(filename, 'wb'))

print("Walking dataset:", accuracy_score(knn.predict(walking_x),walking_y))
