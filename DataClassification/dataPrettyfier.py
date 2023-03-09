import os
import pandas as pd
from pathlib import Path
import pandas as pd
import glob
import os
import math



latestHrData = 0
latestTime = 0
firstRun = True
data = pd.read_csv(r"data\newpamapdata.csv")
data = data.fillna(0)
for i,row in data.iterrows():
    if row['heartrate'] == 0:
        print(i,row['heartrate'])
    continue
quit()

    
    if row['time'] - latestTime != 1:
        if not firstRun:
            latestTime = row['time']
            latestHrData = 0
            continue
        firstRun = False 
    latestTime = row['time']
    currentHrData = row['heartrate']
    if currentHrData != 0:
        latestHrData = currentHrData
    else:
        data['heartrate'][i] = latestHrData
print(data["heartrate"])

#data.to_csv(r"data\newpamapdata.csv")
    #if not row['heartrate'].empty:
    #    latestHRData = row['heartrate']
    #    print(latestHRData)
    #data[]
        

        
quit()


data['activityType'] = data['activityType'].replace(4,0)
data['activityType'] = data['activityType'].replace(5,1)

print(data)
data.to_csv(r"data\pamapData.csv")



df = pd.concat(map(pd.read_csv, glob.glob(r'C:\Users\madsl\OneDrive - Aalborg Universitet\something on skrivebord\watchdata\pamap data\PAMAP2_Dataset\Protocol/*.csv')))
df.to_csv(rf"{path}\new.csv")



all_files = glob.glob(os.path.join(path , r"\*.csv"))

li = []

for filename in all_files:
    df = pd.read_csv(filename, index_col=None, header=0)
    li.append(df)

frame = pd.concat(li, axis=0, ignore_index=True)

frame.to_csv(rf"{path}\new.csv")
quit()


for filename in os.listdir(path):
    if not os.path.isfile(os.path.join(path,filename)):
        continue
    data = pd.read_csv(os.path.join(path,filename),sep="\s+")
    columns = ["timestamp","activityType","heartrate","temperature","3d_accel_x_1","3d_accel_y_1","3d_accel_z_1","3d_accel_x_2","3d_accel_y_2","3d_accel_z_2"]
    i = 0
    while len(columns) < 54:
        columns.append(f"useless_data_{i}")
        i += 1
    data.columns = columns
    data = data.loc[(data["activityType"] == 4) | (data["activityType"] == 5)]
    data.to_csv(fr"{path}\processed\new_{Path(filename).stem}.dat")
    
    
    

#convert this to 1 csv file:
    #C:\Users\madsl\OneDrive - Aalborg Universitet\something on skrivebord\watchdata\pamap data\PAMAP2_Dataset\Protocol
#u can see what each column means her:
    #C:/Users/madsl/Downloads/readme.pdf