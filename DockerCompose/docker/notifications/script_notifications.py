#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import os
import firebase_admin
from weather import getPop
import threading
from firebase_admin import credentials
from firebase_admin import firestore
import time
import json

plantationID = os.environ['PLANTATION_ID']
Email = os.environ['USER_EMAIL']

serverToken = 'AAAAQpQRN8A:APA91bGD1dBD4bcrjlQeCqVxaxFL4ENV0gbXvd1mfcjyGRc9E_WyTJy6-H98vsl1ZXlAf8Qod51Sn8peESVMRCEaY-ZkVVlgaUGWEZTsMcaJGCdBysGVvRgEYCbHuedkMFjCiqVadQMH'
cred = credentials.Certificate('./serviceAccountKey.json')

print('[Info] Initializing Firestore connection...')

firebase_admin.initialize_app(cred)
db = firestore.client()
deviceToken = db.collection('users').document(Email).get().get("deviceToken")
print('[Info] Connection initialized!')

# Create an Event for notifying main thread.
callback_done = threading.Event()


def sendMessage(typeMSG, title, msgBody):
    print("[Info] Sending message...")
    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'key=' + serverToken,
    }

    body = {
        'notification': {'title': title,
                         'body': msgBody
                         },
        'to':
            deviceToken,
        'priority': 'high',
        #   'data': dataPayLoad,
    }
    requests.post("https://fcm.googleapis.com/fcm/send", headers=headers, data=json.dumps(body))

    notif = {
        u'type': typeMSG,
        u'title': title,
        u'body': msgBody,
        u'time': int(time.time())
    }


    db.collection('users').document(Email).collection("notifications").add(notif)
    print(f'[Info] Successfully sent message: "{msgBody}"')
    #doc_watch.unsubscribe()



def water_plantation(doc):

    moisture = doc.to_dict()["readings"][-1]["moisture"]
    plantationName = doc.to_dict()["name"]

    if moisture >= 80:
        db.collection('users').document(Email).collection("plantations").document(plantationID).update(
            {'waterOn': False})
        sendMessage("info", "Plantation Alert", f"Water pump of plantation {plantationName} turned OFF!")
        return

    moisture += 5
    time.sleep(3)

    humidity = doc.to_dict()["readings"][-1]["humidity"]
    light = doc.to_dict()["readings"][-1]["light"]
    temperature = doc.to_dict()["readings"][-1]["temperature"]

    reads = {"humidity": humidity,
                 "light": light,
                 "moisture": moisture, "temperature": temperature,
                 "time": int(time.time())}
    reading = db.collection('users').document(Email).collection('plantations').document(plantationID)
    reading.update({'readings': firestore.firestore.ArrayUnion([reads])})








def on_snapshot(doc_snapshot, changes, read_time):
    for doc in doc_snapshot:
        print(f'[Info] Data of Plantation {doc.to_dict()["id"]} updated!')

        plantationName = doc.to_dict()["name"]

        # print(f'Moisture: {doc.to_dict()["readings"][-1]["moisture"]}')
        #if doc.to_dict()["readings"][-1]["moisture"] >= 80:
        #  db.collection('users').document(Email).collection("plantations").document(plantationID).update({'waterOn': False})

        pop = getPop()

        #if doc.to_dict()["notificationsOn"]:

        if doc.to_dict()["automaticWaterOn"]:
            if doc.to_dict()["readings"][-1]["moisture"] < 20:
                 #if pop < 50:
                 db.collection('users').document(Email).collection("plantations").document(plantationID).update({'waterOn': True})
                 sendMessage("info", "Plantation Alert", f"Water pump of plantation {plantationName} turned ON!")


        if doc.to_dict()["waterOn"]:
           water_plantation(doc)


        #if pop < 101:
            # sendMessage("Rain", "Weather warning", f"{int(pop)}% chance of rain in the next 3 hours!")

        # if doc.to_dict()["readings"][-1]["light"] > 90:
         #      sendMessage("Rain", "Weather warning", f"{int(pop)}% chance of rain in the next 3 hours!")


        #if doc.to_dict()["readings"][-1]["moisture"] >= 85:
            #sendMessage("alert", "Plantation warning", f"Your plantation {plantationName} is oversaturated!")

        #if doc.to_dict()["readings"][-1]["moisture"] <= 20:
            #sendMessage("alert", "Plantation warning", f"Your plantation {plantationName} needs water!")

         #if doc.to_dict()["readings"][-1]["humidity"] > 90:

    callback_done.set()



#To remove notifications
def delete_collection(coll_ref, batch_size):
    docs = coll_ref.list_documents(page_size=batch_size)
    deleted = 0

    for doc in docs:
        # print(f'Deleting doc {doc.id} => {doc.get().to_dict()}')
        doc.delete()
        deleted = deleted + 1

    if deleted >= batch_size:
        return delete_collection(coll_ref, batch_size)


def on_snapshot_user(doc_snapshot, changes, read_time):
    for doc in doc_snapshot:
        if doc.to_dict()["removeNotifications"]:
            delete_collection(db.collection('users').document(Email).collection("notifications"), 10)
            user = db.collection('users').document(Email)
            user.update({'removeNotifications': False})
            print('[Info] All notifications have been removed!')
    callback_done.set()


doc_ref = db.collection('users').document(Email).collection('plantations').document(plantationID)
doc_ref_user = db.collection('users').document(Email)


# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)
doc_watch_user = doc_ref_user.on_snapshot(on_snapshot_user)

# Keep the app running
while True:
    time.sleep(1)

    # print('Listening for data change...')



