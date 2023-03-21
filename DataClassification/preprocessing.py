import pandas as pd
import os

path = 'data'
NANOSEC_TO_MINUTE_FACTOR = 60000000000

def add_minute_column(filepath):
    first_run = True
    minute_counter = 0
    first_timestamp = -1
    file = pd.read_csv(filepath)
    for index,row in file.iterrows():       
        timestamp_nano_sec = row['timestamp']
        if first_timestamp == -1:
            first_timestamp = timestamp_nano_sec
        elif timestamp_nano_sec - first_timestamp >= 1 * NANOSEC_TO_MINUTE_FACTOR:
            index = file.index[file['timestamp']==timestamp_nano_sec].tolist()[0]
            first_timestamp_index = file.index[file['timestamp']==first_timestamp].tolist()[0]
            if not first_run:
                first_timestamp_index += 1
            file.loc[first_timestamp_index: index, ['minutes']] = minute_counter
            if first_run:
                first_run = False
            first_timestamp = timestamp_nano_sec
            minute_counter += 1

    file.dropna(inplace = True)
    file.to_csv(filepath)

for file in os.listdir(path):
    filepath = os.path.join(path, file)
    if file.endswith('.csv'):
        add_minute_column(filepath)