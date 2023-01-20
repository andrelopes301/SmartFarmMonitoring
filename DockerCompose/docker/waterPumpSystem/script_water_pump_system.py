
import os
import firebase_admin
import threading
from firebase_admin import credentials
from firebase_admin import firestore
import time

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



def water_plantation(doc):
    moisture = doc.to_dict()["readings"][-1]["moisture"]

    if moisture >= 80 and doc.to_dict()["waterOn"] != False:
        db.collection('users').document(Email).collection("plantations").document(plantationID).update(
            {'waterOn': False})

    moisture += 1
    time.sleep(0.5)

    humidity = doc.to_dict()["readings"][-1]["humidity"]
    light = doc.to_dict()["readings"][-1]["light"]
    temperature = doc.to_dict()["readings"][-1]["temperature"]

    reads = {"humidity": humidity,
                 "light": light,
                 "moisture": moisture, "temperature": temperature,
                 "time": int(time.time())}
    reading = db.collection('users').document(Email).collection('plantations').document(plantationID)
    reading.update({'readings': firestore.firestore.ArrayUnion([reads])})


def on_snapshot(doc_snapshot):

    for doc in doc_snapshot:
        print(f'[Info] Data of Plantation {doc.to_dict()["id"]} updated!')

        if doc.to_dict()["automaticWaterOn"]:
            if doc.to_dict()["readings"][-1]["moisture"] < 15 and doc.to_dict()["waterOn"] != True:
                 #if pop < 50:
                 db.collection('users').document(Email).collection("plantations").document(plantationID).update({'waterOn': True})

        if doc.to_dict()["waterOn"]:
           water_plantation(doc)

    callback_done.set()


doc_ref = db.collection('users').document(Email).collection('plantations').document(plantationID)
doc_ref_user = db.collection('users').document(Email)


# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)

# Keep the app running
while True:
    time.sleep(1)
    # print('Listening for data change...')



