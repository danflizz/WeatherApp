package com.example.weatherapp.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Geocoding {

    private final Geocoder geocoder;

    /**
     * Android has a built-in class for reverse geocoding: the Geocoder class.
     * It's a powerful yet straightforward tool to turn coordinates into a readable address.
     * @param context to initialize the Geocoder
     */
    public Geocoding(Context context) {
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }

    public Pair<Double, Double> getLatLngFromAddress(String address) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new Pair<>(location.getLatitude(), location.getLongitude());
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    // Helper class to represent a Pair of coordinates
    public static class Pair<T, U> {
        public final T first;
        public final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
}
