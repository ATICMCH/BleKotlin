package com.mch.blekot.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("StaticFieldLeak")
object PreferencesManager {

    private const val PREFERENCES = "preferences"
    private lateinit var mContext: Context

    fun setContext(context: Context){
        mContext = context
    }

    fun setString(key: String?, save: String?) {
        val sharedPref: SharedPreferences =
            mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, save)
        editor.apply()
    }

    fun setBoolean(key: String?, save: Boolean){
        val sharedPref: SharedPreferences =
            mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, save)
        editor.apply()
    }

    fun getString(key: String?): String? {
        val sharedPref: SharedPreferences =
            mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }
    fun getBoolean(key: String?): Boolean {
        val sharedPref: SharedPreferences =
            mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, true)
    }
}


