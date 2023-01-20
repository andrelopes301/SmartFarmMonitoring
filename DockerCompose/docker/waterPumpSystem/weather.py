#!/usr/bin/env python3
# -*- coding: utf-8 -*-
    

import requests
import time


# Replace YOUR_API_KEY with your actual API key
api_key = #Place here your API_KEY

# Specify the location for which you want to get the forecast
city_id = 2732265  # Viseu

# Send a GET request to the OpenWeatherMap API
url = f'https://api.openweathermap.org/data/2.5/forecast?id={city_id}&appid={api_key}'
response = requests.get(url)

# Parse the JSON response
data = response.json()


# Extract the rain probability from the JSON response
#rain_probability = data['list'][15]['pop']


def getPop():
    #for number, dateTime in enumerate(data["list"]):
       # print(f'The index of {number} is {dateTime}') 
        #print(f"Current time: {int(time.time())}")
        #print(data["list"][number]["dt"])
        
        dif = data["list"][0]["dt"] - int(time.time())
        
        #print(f"Time dif: {dif}")

            
        if dif <= 5400:
            #print(f'Rain probability in "{data["list"][1]["dt_txt"]}": {data["list"][1]["pop"] * 100}%')
            rain_probability = data["list"][1]["pop"] * 100
        else:
            #print(f'Rain probability in "{data["list"][0]["dt_txt"]}": {data["list"][0]["pop"] * 100}%')
            rain_probability = data["list"][0]["pop"] * 100
  
        return rain_probability



#print(f'Rain probability in the next 3 hours: {rain_probability}')

getPop()
