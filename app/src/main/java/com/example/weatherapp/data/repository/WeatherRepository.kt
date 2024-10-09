package com.example.weatherapp.data.repository

import com.example.weatherapp.data.network.ApiService
import com.example.weatherapp.data.model.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        val apiKey = "227fa6e3efac7c4a90659735160b86a6"
        return apiService.fetchWeather(lat, lon, apiKey)
    }
}