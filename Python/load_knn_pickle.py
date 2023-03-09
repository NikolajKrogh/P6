import pandas as pd 
import pickle

recordedWalkingData = pd.read_csv(r"data\walking_2023-03-02_16_43_58.csv")

walking_heartrate = "heartrate"
walking_x_accelerometer = "acc_x"
walking_y_accelerometer = "acc_y"
walking_z_accelerometer = "acc_z"
walkingLabel = "label"

walking_x = recordedWalkingData[[walking_x_accelerometer,walking_y_accelerometer,walking_z_accelerometer]]
walking_y = recordedWalkingData[[walkingLabel]]


loaded_model = pickle.load(open('knn_model.sav', 'rb'))
result = loaded_model.score(walking_x, walking_y)
print(result)
