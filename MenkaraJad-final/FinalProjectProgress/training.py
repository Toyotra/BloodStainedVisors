import tensorflow as tf
import numpy as np
import os
import json

from keras.utils import to_categorical


if __name__ =="__main__":
    
    
    labels = []
    
    X_data =[]
    Y_data = []
    
    
    X_train = []
    X_test = []
    Y_train = []
    Y_test = []
    
    label_encoder = {}
    
    
    n=0
    for file in os.listdir("dataset"):
        
        #this encodes the names of each of the json as a new label associated with a number
        label_encoder[n] = file[:-5]
        labels.append(file)
        path = os.path.join("dataset", file)
        with open(path, "r") as json_file:
            data = json.load(json_file) 
        
        np_data = np.array(data)
        x=0
        
        for array in np_data:
            X_data.append(array)
            Y_data.append(n)
        
        
        
        n+=1

    
    
    # splits up data so the last 1000 points are part of the testing set which is used to validate whether or not the model is accurate
    X_train = X_data[:-500]
    X_test = X_data[-500:] 
    
    Y_train = Y_data[:-500]
    Y_test = Y_data[-500:]
    
    # one hot encoder the y values
    Y_data = to_categorical(Y_data)
    Y_train = to_categorical(Y_data)
    Y_test = to_categorical(Y_data)
    print(X_train, Y_train)
        
        


