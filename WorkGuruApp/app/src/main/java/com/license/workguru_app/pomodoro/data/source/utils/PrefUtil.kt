package com.license.workguru_app.pomodoro.data.source.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.license.workguru_app.AuthorizedActivity


class PrefUtil {
    companion object {
        var timerLength:Int = 1
        fun setTimerLength(context: Context,timeInMinutes:Int ){
            timerLength = timeInMinutes
        }
        fun getTimerLength(context: Context, ): Int{
            //placeholder
            return timerLength
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.resocoder.timer.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }


        private const val TIMER_STATE_ID = "com.resocoder.timer.timer_state"

        fun getTimerState(context: Context): AuthorizedActivity.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return AuthorizedActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: AuthorizedActivity.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }


        private const val SECONDS_REMAINING_ID = "com.resocoder.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }


        private const val ALARM_SET_TIME_ID = "com.resocoder.timer.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return  preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }

        fun saveStringDataOnDifferentPref(context: Context,sharedPrefName:String, content:String,  naming:String ){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.apply{
                putString(naming, content)
            }.apply()
            Log.d("PREF", "Saved string data! --> ${content}")
        }

        fun saveIntDataOnDifferentPref(
            context: Context,
            sharedPrefName:String, content: Int?, naming:String ){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.apply{
                if (content != null) {
                    putInt(naming, content)
                }
            }.apply()
            Log.d("PREF", "Saved string data! --> ${content}")
        }
         fun getIntDataFromPref(
            context: Context,
            sharedPrefName:String,  naming:String ):Int{
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(naming, 0)
        }
    }
}
