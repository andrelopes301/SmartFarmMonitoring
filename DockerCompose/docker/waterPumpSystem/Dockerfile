FROM python:latest

#COPY script_notifications.py .

#COPY requirements.txt .
RUN pip install requests firebase_admin

WORKDIR /usr/app/src

COPY script_notifications.py ./
COPY serviceAccountKey.json ./
COPY weather.py ./

#CMD [ "python3", "script_notifications.py" ]
