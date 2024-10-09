package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherapp.data.model.Clouds
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.Main
import com.example.weatherapp.data.model.Sys
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.model.Wind
import com.example.weatherapp.data.network.ApiService
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    //Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var observer : Observer<WeatherResponse>

    private lateinit var weatherRepository : WeatherRepository
    private lateinit var weatherViewModel : WeatherViewModel

    @Before
    fun setUp() {
        weatherRepository = mock(WeatherRepository::class.java)
        weatherViewModel = WeatherViewModel(weatherRepository)
    }

    @Test
    fun `fetch weather updates LiveData`() {
        // Dummy data
        val lat = 44.34
        val lon = 10.99

        val fakeWeatherResponse = WeatherResponse(
            coord = Coord(lon,lat),
            weather = listOf(Weather(804,"","overcast clouds","04n")),
            base = "",
            main = Main(292.1,292.1,290.1,293.2,1012,0,0,0),
            visibility = 0,
            wind = Wind(0.0,0,0.0),
            clouds = Clouds(0),
            dt = 0,
            sys = Sys(0,0,"",0,0),
            timezone = 0,
            id = 0,
            name = "Mexico city",
            cod = 0
        )

        testCoroutineRule.runBlockingTest {
            doReturn(fakeWeatherResponse).`when`(weatherRepository).getWeather(lat,lon)
            weatherViewModel.weatherLiveData.observeForever(observer)

            weatherViewModel.fetchWeather(lat, lon)

            verify(weatherRepository).getWeather(lat,lon)
            verify(observer).onChanged(fakeWeatherResponse)

        }

    }
}