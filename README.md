
# Smart Farm Monitoring

This Github project is a smart irrigation system designed to improve water efficiency in agriculture. The system utilizes various technologies such as Docker containers, a weather API, and a mobile application to enhance the functionality and user interactivity. The system architecture can be seen in the included diagram, showing how all the components interact and communicate with each other.


#### System Architecture
<p>
  <img src="https://i.ibb.co/KFWG9Mw/architecture.png" width="50%" >
 </p>





## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

1.  Clone the repository to your local machine:

Copy code

`git clone https://github.com/[username]/SmartFarmMonitoring.git`

2.  Build the Docker containers by running the following command in the project directory:

`docker-compose up --build`

3.  Connect your device or create an emulator
4.  Build and run the mobile application in Android Studio

## Dependencies

This project uses the following dependencies:

-   Docker: for creating and managing the containers used in the system
-   Firebase Firestore: a cloud-based NoSQL database that stores and syncs data in real-time
-   Firebase SDK: for interacting with Firestore and sending notifications
-   AccuWeather API: for gathering forecasted weather data

## Features

-   The system utilizes sensor data to predict the probability of precipitation and adjust irrigation schedules in real-time.
-   The mobile application allows for remote control and monitoring of the irrigation system.
-   The system utilizes Firestore to store and retrieve sensor data, allowing for real-time updates.
-   The system uses a Python script to generate sensor data, making the system more accurate.
-   The system uses Docker containers to separate the various functionalities and improve scalability and maintainability.
-   The system uses AccuWeather API to gather the forecasted weather data

## Sources

This project was developed for a research project in the field of agriculture and water conservation.

## License

This project is licensed under the MIT License - see the [LICENSE] file for details.
