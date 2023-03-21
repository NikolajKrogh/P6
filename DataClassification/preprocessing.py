import pandas as pd
import os
import random




NANOSEC_TO_MINUTE_FACTOR = 60000000000

path = "data"

#region column names
timestamp = "timestamp"
minute_timestamp = "minutes"
heartrate = "heart_rate"
x_accelerometer = "acc_x"
y_accelerometer = "acc_y"
z_accelerometer = "acc_z"
step_count = "step_count"
session_id = "session_id"
label = "label"
#endregion

def add_minute_column(filepath):
    print(filepath)
    first_run = True
    minute_counter = 0
    first_timestamp = -1
    file = pd.read_csv(filepath)
    if minute_timestamp in file.columns:
        return file
    for index,row in file.iterrows():       
        timestamp_nano_sec = row[timestamp]
        if first_timestamp == -1:
            first_timestamp = timestamp_nano_sec
        elif timestamp_nano_sec - first_timestamp >= 1 * NANOSEC_TO_MINUTE_FACTOR:
            index = file.index[file[timestamp]==timestamp_nano_sec].tolist()[0]
            first_timestamp_index = file.index[file[timestamp]==first_timestamp].tolist()[0]
            if not first_run:
                first_timestamp_index += 1
            file.loc[first_timestamp_index: index, [minute_timestamp]] = minute_counter
            if first_run:
                first_run = False
            first_timestamp = timestamp_nano_sec
            minute_counter += 1
    return file

def add_session_id(file):
    session_id_value = random.randrange(0,1000000)
    if session_id in file.columns:
        return file
    file[session_id] = session_id_value
    return file

def save_csv(file,filepath):
    file.dropna(inplace = True)
    file = file[[timestamp,minute_timestamp,heartrate,x_accelerometer,y_accelerometer,
                 z_accelerometer,step_count,session_id,label]]
    file.to_csv(filepath)

for file in os.listdir(path):
    filepath = os.path.join(path, file)
    if file.endswith('.csv'):
        data_frame = add_minute_column(filepath)
        data_frame = add_session_id(data_frame)
        save_csv(data_frame, filepath)
        