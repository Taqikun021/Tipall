package xyz.tqydn.tipang.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {
    companion object {
        const val MEETING_PREF = "USER_PREF"
    }

    private val sharedPref = context.getSharedPreferences(MEETING_PREF, 0)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun setValues(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getValues(key: String): String? {
        return sharedPref.getString(key, "")
    }
}