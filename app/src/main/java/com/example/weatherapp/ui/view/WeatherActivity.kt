package com.example.weatherapp.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.viewmodel.WeatherViewModel
import com.example.weatherapp.utils.Constants.ICON_URL
import com.example.weatherapp.utils.Geocoding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {


    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = stringResource(R.string.weather_app),
                        modifier = Modifier.padding(innerPadding),
                        fontSize = 20.sp
                    )
                }
                LocationPermissionScreen()
                GetPreviousLocation()
                WeatherScreen()

            }
        }
    }

    @Composable
    private fun GetPreviousLocation() {
        val previousLocation = getLocationFromPreferences(LocalContext.current)
        if (previousLocation != null) {
            weatherViewModel.fetchWeather(previousLocation.first, previousLocation.second)
        }
    }


    @Composable
    private fun WeatherScreen() {
        val keyboardController = LocalSoftwareKeyboardController.current
        val context = LocalContext.current
        val geocoding = Geocoding(context)
        var address by remember { mutableStateOf("") }
        var coordinates by remember { mutableStateOf<Geocoding.Pair<Double, Double>?>(null) }
        val weatherLiveData by weatherViewModel.weatherLiveData.observeAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search bar
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.search_for_a_city)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    val result = geocoding.getLatLngFromAddress(address)
                    if (result != null) {
                        val latitude = result.first
                        val longitude = result.second
                        coordinates = Geocoding.Pair(latitude, longitude)
                        weatherViewModel.fetchWeather(latitude, longitude)
                        saveLocationToPreferences(context, latitude, longitude)
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.city_not_found), Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.search))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show data if is available
            weatherLiveData?.let { weatherResponse ->
                val main = weatherResponse.main
                Text(
                    text = stringResource(R.string.the_weather_in_is, weatherResponse.name),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.temperature, (main.temp.toInt()) / 10),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                val weather = weatherResponse.weather.firstOrNull()
                Text(
                    text = stringResource(
                        R.string.weather,
                        weather?.description ?: stringResource(R.string.n_a)
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.feels_like, main.feels_like.toInt() / 10),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.min_temperature, main.temp_min.toInt() / 10),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.max_temperature, main.temp_max.toInt() / 10),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                LoadIcon(url = ICON_URL.plus(weather?.icon.plus("@2x.png")))
            }
        }
    }


    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun LoadIcon(url: String) {
        val painter = rememberImagePainter(
            data = url
        )
        Image(painter = painter, contentDescription = "icon", modifier = Modifier
            .height(150.dp)
            .width(150.dp))
    }

    @Composable
    fun LocationPermissionScreen() {
        val context = LocalContext.current
        var hasLocationPermission by remember { mutableStateOf(false) }
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(
                context
            )

        val locationPermissionsAlreadyGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                val isGranted = permissions.values.reduce { acc, isPermissionGranted ->
                    acc && isPermissionGranted
                }
                hasLocationPermission = isGranted
            })

        if (hasLocationPermission) {

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null,
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    saveLocationToPreferences(context, location.latitude, location.longitude)
                    weatherViewModel.fetchWeather(location.latitude, location.longitude)
                } else {
                    requestNewLocation(context, fusedLocationClient)
                }

            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
            }
        } else {
            LaunchPermission(
                locationPermissionsAlreadyGranted,
                locationPermissionLauncher,
                locationPermissions
            )
        }
    }

    @Composable
    private fun LaunchPermission(
        locationPermissionsAlreadyGranted: Boolean,
        locationPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
        locationPermissions: Array<String>
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner, effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START && !locationPermissionsAlreadyGranted) {
                    locationPermissionLauncher.launch(locationPermissions)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        )
    }

    private fun saveLocationToPreferences(context: Context, latitude: Double, longitude: Double) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("latitude", latitude.toString())
            putString("longitude", longitude.toString())
            apply()
        }
    }

    private fun getLocationFromPreferences(context: Context): Pair<Double, Double>? {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE)
        val latitude = sharedPref.getString("latitude", null)?.toDoubleOrNull()
        val longitude = sharedPref.getString("longitude", null)?.toDoubleOrNull()

        return if (latitude != null && longitude != null) {
            Pair(latitude, longitude)
        } else {
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    location?.let {
                        saveLocationToPreferences(context, it.latitude, it.longitude)
                    }

                    fusedLocationClient.removeLocationUpdates(this)
                }
            }, null
        )
    }

}