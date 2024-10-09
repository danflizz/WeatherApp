package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    
    val weatherLiveData = MutableLiveData<WeatherResponse>()

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val weather = withContext(Dispatchers.IO) {
                weatherRepository.getWeather(lat, lon)
            }
            weatherLiveData.postValue(weather)
        }
    }
}