# Jad Menkara
# training.py
# Used to train the model used to classify hand signs, uses tensorflow and keras




import tensorflow as tf
import numpy as np
import os
import json

from keras import layers
from keras import Sequential
from keras.layers import Dense, Dropout
from keras.datasets import mnist
from keras.utils import to_categorical


#runs the code when it is executed by python
if __name__ =="__main__":
    
    
    labels = [] #contains the labels for classifying
    
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
    X_train = np.array(X_data)[:-500]
    X_test = np.array(X_data)[-500:] 
    
    Y_train = np.array(Y_data)[:-500]
    Y_test = np.array(Y_data)[-500:]
    
    # one hot encoder the y values
    Y_data = to_categorical(Y_data, num_classes=n)
    Y_train = to_categorical(Y_train, num_classes=n)
    Y_test = to_categorical(Y_test, num_classes=n)
    
    # print(X_train, Y_train)
        
    model = tf.keras.Sequential()
    
    model.add(Dense(128, activation='relu', input_shape=(X_train.shape[1],)))
    model.add(Dense(128, activation='relu'))
    model.add(Dense(n,  activation="softmax"))

    model.compile(
        optimizer="rmsprop",
        loss="categorical_crossentropy",
        metrics=["accuracy"],
    )
    
    
    
    training_results = model.fit(X_train, #actually trains the data
                             Y_train,
                             epochs=100, #originally it was higher but it was lowered due to innaccuracy from overfitting
                             batch_size=64,
                             validation_data=(X_test, Y_test))
    
    
    
    train_loss = training_results.history["loss"]
    train_acc  = training_results.history["accuracy"]
    valid_loss = training_results.history["val_loss"]
    valid_acc  = training_results.history["val_accuracy"]
    
    loss,accuracy = model.evaluate(X_test, Y_test, verbose = 0)

    print(f"accuracy: {accuracy}")
    print(f"training loss: {train_loss}\ntraining accuracy: {train_acc}\nvalidation loss: {valid_loss}\nvalidation accuracy: {valid_acc}")
    
    
    
    model.save('model.keras')
    
    
    with open("labels.data", "w") as f: #writes to the labels.data file which has the label information to be used by the classifier
        for label in label_encoder:
            f.write(f"{label}:{label_encoder[label]},")
        