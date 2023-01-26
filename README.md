
# Smart Farm Monitoring


A Smart Irrigation System that uses Docker, a weather API and an android mobile app to improve water efficiency in agriculture by calculating and supplying water based on sensor data and weather forecast, with real-time monitoring and alerts via mobile app.

The system architecture can be seen in the included diagram, showing how all the components interact and communicate with each other.


### Mobile App Screens / System Architecture
<p>
  <img src="https://i.ibb.co/zGbtGpm/splash.png" width="16%" >
  <img src="https://i.ibb.co/hyS7czS/homepage.png" width="16%" >
  <img src="https://i.ibb.co/F5yW3CF/plantation.png" width="16%" >
  <img src="https://i.ibb.co/KFWG9Mw/architecture.png" width="45%">
 </p>


## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

1.  Clone the repository to your local machine:

  `git clone https://github.com/andrelopes301/Smart-Farm-Monitoring.git`

3.  Add your accuWeather APIKey in the weather.py file.
4.  Connect your device or create an emulator.
5.  Build and run the mobile application in Android Studio.
6.  Add a plantation in the mobile app, for test purposes.
7.  Replace the "USER_EMAIL" and "PLANTATION_ID" in the environment.env file, inside the Docker Compose folder.
7.  Build the Docker containers by running the following command in the project directory:

  `docker-compose up --build`

7. Check the real-time changes in the mobile app.


## Dependencies

This project uses the following dependencies:

-   Docker: for creating and managing the containers used in the system
-   Firebase Firestore: a cloud-based NoSQL database that stores and syncs data in real-time
-   Firebase SDK: for interacting with Firestore, Firebase Authentication, and Firebase Messaging
-   AccuWeather API: for gathering forecasted weather data

## Features

-   The system utilizes sensor data to predict the probability of precipitation and adjust irrigation schedules in real-time.
-   The mobile application allows for remote control and monitoring of the irrigation system.
-   The system utilizes Firestore to store and retrieve sensor data, allowing for real-time updates.
-   The system uses a Python script to generate sensor data, making the system more accurate.
-   The system uses Docker containers to separate the various functionalities and improve scalability and maintainability.
-   The system uses AccuWeather API to gather the forecasted weather data.

## Sources

This project was developed for a research paper, for the subject "Intelligent Systems Integration" in Instituto Politécnico de Viseu – Escola Superior de Tecnologia e Gestão de Viseu.

## License

This project is licensed under the MIT License - see the [LICENSE] file for details.
