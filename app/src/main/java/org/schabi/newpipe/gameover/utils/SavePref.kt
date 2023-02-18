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

    fun setDeviceid(Deviceid: String?) {
        val editor = savepref!!.edit()
        editor.putString("Deviceid", Deviceid)
        editor.apply()
    }

    fun getDeviceid(): String? {
        return savepref!!.getString("Deviceid", "noDeviceid")
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
}
