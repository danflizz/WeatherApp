# WeatherApp

Small weather app where the user can type the city they want to visualize, 
Data displayed: temperature, feels like, min temperature,max temperature ,icon 

[demo_.webm](https://github.com/user-attachments/assets/2bf77629-5468-4228-94d6-6cd0c745461a)


Response example: 

{
  "coord": {
    "lon": -100.4059,
    "lat": 20.6392
  },
  "weather": [
    {
      "id": 804,
      "main": "Clouds",
      "description": "overcast clouds",
      "icon": "04n"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 292.1,
    "feels_like": 291.77,
    "temp_min": 292.1,
    "temp_max": 292.1,
    "pressure": 1013,
    "humidity": 66,
    "sea_level": 1013,
    "grnd_level": 800
  },
  "visibility": 10000,
  "wind": {
    "speed": 3.49,
    "deg": 78,
    "gust": 5.08
  },
  "clouds": {
    "all": 98
  },
  "dt": 1728436260,
  "sys": {
    "type": 1,
    "id": 7159,
    "country": "MX",
    "sunrise": 1728390875,
    "sunset": 1728433387
  },
  "timezone": -21600,
  "id": 4029478,
  "name": "Peñuelas",
  "cod": 200
}

Technologies used: 
Kotlin, Java, MVVM, JUnit, Compose, Retrofit, Mockito, Coroutines, Hilt 

It uses shared preferences to store last city (latitude and longitude) 
