package com.jonwid78.phone1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

/**
 * Created by Ferit on 16-Oct-17.
 */

class Tracking(val context: Context, val googleApi: GoogleApiClient) {


    private var lastLocation: Location? = null

    private var distance = 0.0F
    private var time = 0L
    private var speed = 0.0F
    private var _highestSpeed = 0.0F

    var highestSpeed: Float = 0.0f
        get() = _highestSpeed


    @SuppressLint("MissingPermission")
    fun startTracking(endResults: EndResults? = null, onLocationReceived: (distance: Float, speed: Float, time: Long) -> Unit) {

        if (endResults != null) {
            distance = endResults.distance
            time = endResults.time
            speed = endResults.speed
        }

        Log.d("Client", "Google API connection ${googleApi.isConnected}")
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest()
            locationRequest.interval = 2000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(loc: LocationResult?) {
                    super.onLocationResult(loc)

                    Log.d(HomeFragment.TAG, "Location: ${loc?.lastLocation}")
                    calculateRelevantInfo(location = loc!!.lastLocation)

                    onLocationReceived(distance, speed, time)

                }

                override fun onLocationAvailability(p0: LocationAvailability?) {
                    super.onLocationAvailability(p0)
                    Log.d(HomeFragment.TAG, "${p0?.isLocationAvailable}")
                }
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApi, locationRequest, locationCallback, null)
        }
    }

    fun calculateRelevantInfo(location: Location) {
        if (lastLocation == null) {
            lastLocation = location
        }

        if (lastLocation != location) {
            val calculatedDistance = lastLocation!!.distanceTo(location)
            val calculatedTime = (location.elapsedRealtimeNanos - lastLocation!!.elapsedRealtimeNanos) / 1_000_000_000
            distance += Math.round(calculatedDistance)
            time += calculatedTime

            if ((distance / time) > highestSpeed) {
                highestSpeed = distance / time
            }
            speed = distance / time


            Log.d("Ferit", "${time / 1_000_000_000} seconds and the speed is ${speed}")
        }

        lastLocation = location

    }
}

// data class TrackedData(val latitude: Double, val longitude: Double, val time: Long)