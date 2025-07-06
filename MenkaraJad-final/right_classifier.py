# Jad Menkara
# right_classifier.py
# Used to track the person on the right and send updated controls to the server





# imports
import cv2
import mediapipe as mp
import numpy as np
import os
import dataset_collection
import tensorflow as tf
from tensorflow import keras
import time
import requests
import json
import math
import numpy as np



# I planned on using this to calculate if there was a fast movement for the past 10
# frames to see if a punch registered but didn't have enough time
# to have it fully implimented, it didn't work very well
def calculate_velocity_hand(landmarkInput, prevVel):
    
    # this uses only the core body landmarks to calcualte how much you are moving
    x1 = landmarkInput[11].x
    x2 = landmarkInput[23].x
    x3 = landmarkInput[17].x
    x4 = landmarkInput[24].x
    
    avg = (x1+x2+x3+x4)/4
    
    return (avg-prevVel)*10

#checks if something is a punch by checking angles between body joints using mediapipe landmarks
def isPunch(landmarkListX, landmarkListY):
    #gets major arm points for the left side
    point1 = point(landmarkListX[23], landmarkListY[23])
    point2 = point(landmarkListX[11], landmarkListY[11])
    point3 = point(landmarkListX[13], landmarkListY[13])
    point4 = point(landmarkListX[15], landmarkListY[15])
    
    if calculate_angle(point1, point2, point3)>50:
        if calculate_angle(point2, point3, point4)>140:
            return 1

    #gets major arm points for the right side
    point1 = point(landmarkListX[24], landmarkListY[24])
    point2 = point(landmarkListX[12], landmarkListY[12])
    point3 = point(landmarkListX[14], landmarkListY[14])
    point4 = point(landmarkListX[16], landmarkListY[16])
    
    if calculate_angle(point1, point2, point3)>63:
        if calculate_angle(point2, point3, point4)>148:
            return 1
    return 0
        

# checks if something is a kick by seeing if the angles between leg landmarks indicate a leg extension
def isKick(landmarkListX, landmarkListY):
    point0 = point(landmarkListX[11], landmarkListY[11])
    point1 = point(landmarkListX[23], landmarkListY[23])
    point2 = point(landmarkListX[25], landmarkListY[25])
    point3 = point(landmarkListX[27], landmarkListY[27])
    
    point00 = point(landmarkListX[12], landmarkListY[12])
    point4 = point(landmarkListX[24], landmarkListY[24])
    point5 = point(landmarkListX[26], landmarkListY[26])
    point6 = point(landmarkListX[28], landmarkListY[28])
    
    
    #only returns 1 if either leg is extended using the angles between major points
    if calculate_angle(point0, point1, point2)<130:
        #print("this is working now")
        if calculate_angle(point1, point2, point3)>115:
            return 1
    if calculate_angle(point00, point4, point5)<130:
        #print("this is working now")
        if calculate_angle(point4, point5, point6)>115:
            return 1
    return 0
    
    
    
    
    
    
    
    
    
# calculates the angle between 3 points
def calculate_angle(point1, point2, point3):
    
    # gets the side lengths between the 3 points
    length1 = math.sqrt((point1.y-point2.y)**2+(point1.x-point2.x)**2)
    length2 = math.sqrt((point3.y-point2.y)**2+(point3.x-point2.x)**2)
    
    length3 = math.sqrt((point1.y-point3.y)**2+(point1.x-point3.x)**2)
    
    
    
    # we can then rearrange cosine law to get the angle between the 3 points using 3 lengthss
    angle = math.degrees(math.acos(
        np.clip(
            ((-length3**2+length1**2+length2**2)/
            (2*length1*length2)),
            -1,
            1
        )
    ))
    
    return angle

# this is used to see if you are moving left or right, it takes average x valyes of 4 main body joints
def get_average_position(xLandmarks):
    x1 = xLandmarks[11]
    x2 = xLandmarks[12]
    x3 = xLandmarks[23]
    x4 = xLandmarks[24]
    
    return (x1+x2+x3+x4)/4



#checks if you are on left, right, or middle of screen
def isMoving(xLandmarks, frameWidth):
    position = get_average_position(xLandmarks)
    
    if position<frameWidth*1.5/5:
        return 1
    if position>frameWidth*3.5/5:
        return -1
    return 0


class point: # point class containing x and y values
    def __init__(self, x, y):
        self.x = x
        self.y = y
    def getY(self):
        return self.y
    def getX(self):
        return self.x
    def setY(self, n):
        self.y=n
    def setX(self, n):
        self.x=n


def queueList(inputList, value): # this was used for checking the velocities for punching, however went on to be unused
    inputList.append(value)
    inputList.pop(0)
    return inputList


#runs the code
def run_code():
    start_time = time.time()
    velocity = 0
    hand_sign_classifier = keras.models.load_model("model.keras")
    
    #used to draw hands and the body
    mp_drawing = mp.solutions.drawing_utils
    mp_hands = mp.solutions.hands
    
    labels = {}
    
    #again, this was not used
    velcocity_threshhold_punch = -100
    prev_velocities_left = [0,0,0,0,0,0,0,0,0,0]
    prev_velocities_right = [0,0,0,0,0,0,0,0,0,0]
    
    attackCooldownMax = 10
    
    currentAttackCooldown=0
    
    
    #used to send json to the flask server
    jsonPayloadMap = {
        "punch": 0,
        "kick": 0,
        "special": 0,
        "moving": 0,
        "direction": 1      
    }


    # reads the labels file which contains the various labels with the info
    with open("labels.data", "r") as label_file:
        data = label_file.read().split(",")
        
        for string_value in data:
            
            values = string_value.split(":")
            if len(values) != 1:
                labels[int(values[0])] = values[1]
            
    len_labels = len(list(labels))
    
    cap = cv2.VideoCapture(1)
    
    
    
    # used for the pose and hand classification
    mp_pose = mp.solutions.pose.Pose(min_detection_confidence=0.2,
    min_tracking_confidence=0.2)
    
    hands = mp_hands.Hands(min_detection_confidence=0.6,min_tracking_confidence=0.5)
    current_avg_pos = 0.5
    
    #also used for getting velocity but later scrapped
    prevHandXLeft = 0
    prevHandXRight = 0
    
    
    #used to send requests to server when timer is hit
    requestTimer=0
    requestTimerMax=5
    
    ret, old_frame = cap.read()
    h, w, _ = old_frame.shape
    
    while True:
        current_time = time.time()
        ret, old_frame = cap.read()
        
        if(ret==False):
            continue
        
        old_frame = cv2.flip(old_frame,1)
        
        old_frame = cv2.cvtColor(old_frame, cv2.COLOR_BGR2RGB)
                
        # used to process the poses for the current frame
        old_frame.flags.writeable = False
        results = hands.process(old_frame)
        old_frame.flags.writeable = True         
        results_pose = mp_pose.process(old_frame)
        
        old_frame = cv2.cvtColor(old_frame, cv2.COLOR_RGB2BGR)
        #print(currentAttackCooldown)
        if currentAttackCooldown>0:
            currentAttackCooldown+=1
        
        
        
        
        frame = old_frame.copy()
        if results.multi_hand_landmarks:
                for hand_landmarks in results.multi_hand_landmarks:
                    mp_drawing.draw_landmarks( #draws hands to screen
                        frame,
                        hand_landmarks,
                        mp_hands.HAND_CONNECTIONS,
                        mp_drawing.DrawingSpec(color=(0, 0, 0), thickness=2, circle_radius=2),
                        mp_drawing.DrawingSpec(color=(255, 255, 255), thickness=2, circle_radius=3),
                    )
                    
                    input_data = []
                    x_values=[]
                    y_values=[]
                    
                    #gets x and y values for the hand landmarks
                    for lm in hand_landmarks.landmark:
                        input_data.append(lm.x);input_data.append(lm.y)
                        x_values.append(int(lm.x*w))
                        y_values.append(int(lm.y*h))
                        
                    #print(x_values[0] - prevX)
                    
                    #converts to numpy array and normalizes them
                    input_array = np.array(dataset_collection.data_normalizer(input_data)).reshape(1,42,1)
                    
                    #gets the predictions and gets the label from said predictions
                    predictions = hand_sign_classifier.predict(input_array)
                    label = f"{labels[np.argmax(predictions)]}"
                    
                    #tf_input_data = tf.convert_to_tensor(np.array(input_data), dtype=tf.float32)
                    
                    cv2.rectangle(frame, (min(x_values), min(y_values)), (max(x_values), max(y_values)), (255,255,0), 2)
                    cv2.putText(frame, label, (min(x_values), min(y_values)- 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (255, 255, 255), 2)
                    
                    if label =="special": #checks if you are doing the special attack
                        jsonPayloadMap["special"] = 1
                        if currentAttackCooldown==0:
                            print("----------------------------------------------------------")
                            print("----------------------------------------------------------")
                            print("-------------------------SPECIAL--------------------------")
                            print("----------------------------------------------------------")
                            print("----------------------------------------------------------")
                            currentAttackCooldown+=1
                    #break
        if results_pose.pose_landmarks: #draws the landmarks for your current pose
            mp_drawing.draw_landmarks(
                frame, 
                results_pose.pose_landmarks, 
                mp.solutions.pose.POSE_CONNECTIONS,
                mp_drawing.DrawingSpec(color=(0, 0, 0), thickness=3, circle_radius=2),
                mp_drawing.DrawingSpec(color=(255, 255, 255), thickness=4, circle_radius=2)
            )
            '''
            jsonPayloadMap = {
        "punch": 0,
        "kick": 0,
        "special": 0,
        "moving": 0
        } 
            '''
            
            #gets x and y values for ur current pose
            x_values=[]
            y_values=[]
            for lm in results_pose.pose_landmarks.landmark:
                x_values.append(int(lm.x*w))
                y_values.append(int(lm.y*w))
                
            currentLeftVel = x_values[19] - prevHandXLeft
            currentRightVel = x_values[20] - prevHandXRight
            
            print(isMoving(x_values, w))
            
            #checks if you're moving
            jsonPayloadMap["moving"] = isMoving(x_values, w)
            
            
            prevHandXRight = currentRightVel 
            prevHandXLeft = currentLeftVel
            
            prev_velocities_right = queueList(prev_velocities_right, currentRightVel)
            prev_velocities_left = queueList(prev_velocities_left, currentLeftVel)
            
            
            
            #print(f"punching: {isPunch(prev_velocities_left, prev_velocities_right, x_values, y_values, velcocity_threshhold_punch)}, right: {currentRightVel}, left: {currentLeftVel}")
            
            
            if isPunch(x_values, y_values) and currentAttackCooldown==0: #checks if you are punching
                jsonPayloadMap["punch"] = 1
                print("----------------------------------------------------------")
                print("----------------------------------------------------------")
                print("--------------------------PUNCH---------------------------")
                print("----------------------------------------------------------")
                print("----------------------------------------------------------")
                currentAttackCooldown+=1
            
            
            if isKick(x_values, y_values) and currentAttackCooldown==0: #checks if you are kicking
                jsonPayloadMap["kick"] = 1
                print("----------------------------------------------------------")
                print("----------------------------------------------------------")
                print("--------------------------KICK----------------------------")
                print("----------------------------------------------------------")
                print("----------------------------------------------------------")
                currentAttackCooldown+=1
        
        if currentAttackCooldown==attackCooldownMax:
            currentAttackCooldown = 0
        if requestTimer==requestTimerMax: #sends a request to our localhost server to send the data to java
            requestTimer=0
            headers = {'Content-type': 'application/json'}
            response = requests.post(url="http://127.0.0.1:5000/api", data=json.dumps(jsonPayloadMap), headers=headers)
            if response.status_code == 200:
                print(response.json())
            else:
                print(f"Error: {response.status_code}")
            jsonPayloadMap = {
                "punch": 0,
                "kick": 0,
                "special": 0,
                "moving": 0,
                "direction": 1
                }
            time.sleep(0.05)
        requestTimer+=1
            
        cv2.imshow("Right_Classifier", frame)
        
        if cv2.waitKey(10) == ord("q"):
            break
    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    run_code()