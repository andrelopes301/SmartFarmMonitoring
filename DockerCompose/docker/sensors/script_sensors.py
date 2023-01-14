#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from datetime import datetime
import time
import random

plantationID = os.environ['PLANTATION_ID']
Email = os.environ['USER_EMAIL']

cred = credentials.Certificate('./serviceAccountKey.json')

print('[Info] Initializing Firestore connection...')
firebase_admin.initialize_app(cred)
db = firestore.client()
print('[Info] Connection initialized!')




def sendReadings(seconds):

    reading = db.collection('users').document(Email).collection('plantations').document(plantationID)
    doc = reading.get()

    humidity = doc.to_dict()["readings"][-1]["humidity"]
    if humidity == 0:
        humidity = 20

    light = doc.to_dict()["readings"][-1]["light"]
    if light == 0:
        light = 70

    moisture = doc.to_dict()["readings"][-1]["moisture"]
    if moisture == 0:
        moisture = 10

    temperature = doc.to_dict()["readings"][-1]["temperature"]
    if temperature == 0:
        temperature = 20

    if not doc.to_dict()["waterOn"]:
        humidity = random.randint(humidity - 1, humidity + 1)
        light = random.randint(light - 1, light + 1)
        moisture = random.randint(moisture - 3, moisture + 1)
        temperature = random.randint(temperature - 1, temperature + 1)
        timestamp = int(time.time())

        reads = {"humidity": humidity, "light": light, "moisture": moisture, "temperature": temperature, "time": timestamp}
        reading.update({'readings': firestore.firestore.ArrayUnion([reads])})

        reads["time"] = datetime.fromtimestamp(reads["time"]).strftime('%Y-%m-%d %H:%M:%S')
        print(f"[Info] Sensor values updated: {reads}")

        time.sleep(seconds)


while True:
    sendReadings(5)
