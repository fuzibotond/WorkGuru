package com.license.workguru_app.utils

import android.content.Context
import android.content.SharedPreferences

class ProfileUtil {
    companion object{
        private const val SHARED_PREFS = "sharedPrefs"

        fun getStringPref(context:Context, key:String): String? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val savedItem = sharedPreferences.getString(key, null)
            return savedItem
        }

        fun getIntPref(context:Context, key:String): Int? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val savedItem = sharedPreferences.getInt(key, 0)
            return savedItem
        }

        fun getLongPref(context:Context, key:String): Long? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val savedItem = sharedPreferences.getLong(key, 0L)
            return savedItem
        }

         fun saveUserIntData(context: Context, data:Int, key: String ){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply{
                putInt(key, data)
            }.apply()
        }

         fun saveUserStringData(context: Context, data:String, key: String ){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply{
                putString(key, data)
            }.apply()
        }

         fun saveUserLongData(context: Context, data:Long, key: String ){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply{
                putLong(key, data)
            }.apply()
        }
    }
}