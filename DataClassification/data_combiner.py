import pandas as pd
import glob
import os




def data_combiner():
    result_path = r"data\combined.csv"
    if os.path.exists(result_path):
        print("combined file already exists")
        prompt = ""
        while prompt != "y" and prompt != "n":
            prompt = input("Do you want to replace the file or not (y=replace / n= do not replace) ").lower()
        if prompt == "n":
            return
    path = r"data\to_combine\*.csv"
    files = glob.glob(path) 
    df = pd.concat((pd.read_csv(file, header = 0) for file in files))
    df.to_csv(result_path,index=False)
    print(f"written to file: {result_path}")
    
    
    
data_combiner()