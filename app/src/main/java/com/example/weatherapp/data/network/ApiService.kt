package com.example.weatherapp.data.network

import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun fetchWeather(@Query("lat") latitude: Double,
                             @Query("lon") longitude: Double,
                             @Query("appid") apiKey: String, ): WeatherResponse
}