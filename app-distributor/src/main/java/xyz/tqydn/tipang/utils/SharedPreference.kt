package xyz.tqydn.tipang.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreference(val context: Context) {
    companion object {
        const val MEETING_PREF = "USER_PREF"
    }

    private val sharedPref = context.getSharedPreferences(MEETING_PREF, 0)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val gson = Gson()

    fun setValues(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getValues(key: String): String? {
        return sharedPref.getString(key, "")
    }

    fun setList(key: String, list: List<String>){
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getList(key: String): ArrayList<String> {
        val json = sharedPref.getString(key, null)
        val type = object: TypeToken<ArrayList<String>>(){}.type
        return gson.fromJson(json, type)
    }
}