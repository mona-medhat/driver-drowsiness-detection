import cv2
import numpy as np
import glob
import socket

#Sets up listener
listensocket = socket.socket()
listenPort = 8000
numberOfConnections=999
thisIp = socket.gethostname()
listensocket.bind(('0.0.0.0', listenPort))

#Starts Server
listensocket.listen(numberOfConnections)
print("Started Listening")

#Accepts Connection
(clientsocket, address) = listensocket.accept()
print("Connected")

#Define File Name
fname = "test.png"

#Opens File
f = open(fname, 'wb')
datain = 1

#Receives Image
while datain:
    datain = clientsocket.recv(9999999) #Gets incomming data
    f.write(datain) #Writes data to file

print("image recieved")
f.close()
listensocket.close()

frame = cv2.imread('test.png', 1)
cv2.imshow("image", frame)
cv2.waitKey()

count = 0

gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
#haarcascade_frontalface_default.xml
faceCascade = cv2.CascadeClassifier('C:\\Users\\user\\AppData\\Local\\Programs\\Python\\Python36-32\\Lib\\site-packages\\cv2\\data\\haarcascade_frontalface_default.xml')

eyeCascade = cv2.CascadeClassifier('C:\\Users\\user\\AppData\\Local\\Programs\\Python\\Python36-32\\Lib\\site-packages\\cv2\\data\\haarcascade_eye.xml')

faces = faceCascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=5)

for (x, y, w, h) in faces:
    cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 255), 2)
    roi_gray = gray[y:y+h, x:x+w]
    roi_color = frame[y:y+h, x:x+w]

    eyes = eyeCascade.detectMultiScale(roi_gray, scaleFactor=1.2, minNeighbors=6)
    print("number of opened eyes = ",len(eyes))
    if len(eyes) < 2:
        (clientsocket2, address) = listensocket.accept()
        clientsocket2.send(bytes("sleepy-driver", "utf-8"))
        print('alarm')
