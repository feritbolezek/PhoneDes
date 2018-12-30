package com.jonwid78.phone1

import android.location.Location

/**
 * Created by Ferit on 23-Oct-17.
 */
interface LocationUpdatedListener {
    fun locationUpdated(distance: Float, speed: Float, time: Long, highestSpeed: Float)
}