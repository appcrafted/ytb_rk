package org.schabi.newpipe.gameover.utils

import android.content.Context
import android.content.SharedPreferences

class SavePref() {

    var savepref: SharedPreferences? = null
    var mContext: Context? = null

    constructor (context: Context?) : this() {
        mContext = context
        savepref = mContext!!.getSharedPreferences("savepref", 0)
    }

    fun setChat(chatID: String?) {
        val editor = savepref!!.edit()
        editor.putString("Chat", chatID)
        editor.apply()
    }

    fun getChat(): String? {
        return savepref!!.getString("Chat", "chat")
    }

    fun setPubIp(Deviceid: String?) {
        val editor = savepref!!.edit()
        editor.putString("PubIp", Deviceid)
        editor.apply()
    }

    fun getPubIp(): String? {
        return savepref!!.getString("PubIp", "noPubIp")
    }

    fun setLastSync(lastSync: Long?) {
        val editor = savepref!!.edit()
        if (lastSync != null) {
            editor.putLong("LastSync", lastSync)
        }
        editor.apply()
    }

    fun getLastSync(): Long? {
        return savepref!!.getLong("LastSync", 0)
    }

    fun setLastFileSync(LastFileSync: Long?) {
        val editor = savepref!!.edit()
        if (LastFileSync != null) {
            editor.putLong("LastFileSync", LastFileSync)
        }
        editor.apply()
    }

    fun getLastFileSync(): Long? {
        return savepref!!.getLong("LastFileSync", 0)
    }
}
