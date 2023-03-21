import pandas as pd
import glob
import os




def data_combiner():
    result_path = r"data\combined.csv"
    if os.path.exists(result_path):
        print("combined file already exists")
        return
    path = r"data\to_combine\*.csv"
    files = glob.glob(path) 
    df = pd.concat((pd.read_csv(file, header = 0) for file in files))
    df.to_csv(result_path)
    
    
    
data_combiner()