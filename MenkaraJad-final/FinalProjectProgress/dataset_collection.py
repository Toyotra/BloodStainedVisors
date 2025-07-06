'''
Jad Menkara
Monday May 26th, 2025
Program for classifying different hand positions with mediapipe

'''
import cv2
import mediapipe as mp
import numpy as np
import os
import numpy as np
import json

mp_drawing = mp.solutions.drawing_utils
mp_hands = mp.solutions.hands

    
    
    
    
# change these to change the current gesture you are trying to target
training_mode = True
current_gesture = "thumbs_up_right"
landmark_json = []
continuous_data_stream = True #used so that all frames are taken for data in the model


dataset_size = 5000



cap = cv2.VideoCapture(0)

def add_dataset(current_landmark):
    new_labels = []
    for lm in current_landmark:
        new_labels.append(lm.x); new_labels.append(lm.y);
    landmark_json.append(new_labels)
    return data_normalizer(new_labels)
    

def data_normalizer(input_list):
    x_values = []
    y_values = []
    
    normalized_data = []
    for i in range(len(input_list)):
        if i%2==0:
            x_values.append(input_list[i])
        else:
            y_values.append(input_list[i])
    
    minX = min(x_values)
    minY = min(y_values)
    maxX = max(x_values)
    maxY = max(y_values)
    
    for i in range(len(x_values)):
        
        normalized_data.append((x_values[i]-minX) / (maxX-minX))
        normalized_data.append((y_values[i]-minY) / (maxY-minY))
        
    return normalized_data


# used to print the # of data points in the dataset currently
n=0
    
    
#https://mediapipe.readthedocs.io/en/latest/solutions/hands.html
if __name__ == "__main__":
    with mp_hands.Hands(min_detection_confidence=0.6,min_tracking_confidence=0.5) as hands:
            
            while True:
                
                ret, frame = cap.read()
                
                if not ret:
                    break
                h, w, _ = frame.shape
                
                
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                
                frame.flags.writeable = False
                results = hands.process(frame)
                frame.flags.writeable = True
                
                frame = cv2.cvtColor(frame, cv2.COLOR_RGB2BGR)
                
                add_data = False
                if continuous_data_stream  or cv2.waitKey(10) == ord(' '):
                    add_data=True
                
                if results.multi_hand_landmarks:
                    for hand_landmarks in results.multi_hand_landmarks:
                        
                        
                        label = "current_label"
                        
                        mp_drawing.draw_landmarks(
                            frame,
                            hand_landmarks,
                            mp_hands.HAND_CONNECTIONS,
                            mp_drawing.DrawingSpec(color=(0, 0, 0), thickness=2, circle_radius=2),
                            mp_drawing.DrawingSpec(color=(255, 255, 255), thickness=2, circle_radius=3),
                        )
                        
                        if training_mode and add_data:
                            label_outputs = add_dataset(hand_landmarks.landmark)
                            #print(label_outputs)
                            n+=1
                            print(n)
                        
                        x_values=[]
                        y_values=[]
                        for lm in hand_landmarks.landmark:
                            x_values.append(int(lm.x*w))
                            y_values.append(int(lm.y*h))
                        
                        
                        cv2.rectangle(frame, (min(x_values), min(y_values)), (max(x_values), max(y_values)), (255,255,0), 2)
                        cv2.putText(frame, label, (min(x_values), min(y_values)- 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (255, 255, 255), 2)
                        
                cv2.imshow("Webcam", frame)
                
                if training_mode:
                    cv2.putText(frame, "To add data to the dataset, press the space key", (20,20), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0,0,0), 2)
                    #cv2.putText(frame, f"{n}", (20,400), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0,0,0), 2)
                    
                # stops loop when you reach the dataset size
                if cv2.waitKey(10) == ord('q') or n==dataset_size:
                    if training_mode:
                        
                        with open(f"dataset/{current_gesture}.json", "w") as f:
                            json.dump(landmark_json, f, indent=4)
                    break
            
            
            cap.release()
            cv2.destroyAllWindows()