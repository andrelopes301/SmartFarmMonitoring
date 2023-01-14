FROM python:latest

RUN pip install requests firebase_admin

WORKDIR /usr/app/src

COPY serviceAccountKey.json ./
COPY script_sensors.py ./
