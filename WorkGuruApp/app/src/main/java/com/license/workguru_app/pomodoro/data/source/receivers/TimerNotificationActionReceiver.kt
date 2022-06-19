package com.license.workguru_app.pomodoro.data.source.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.license.workguru_app.AuthorizedActivity
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.pomodoro.data.source.utils.PrefUtil
import com.license.workguru_app.timetracking.data.source.services.TimerService
import com.license.workguru_app.utils.Constants
import com.license.workguru_app.utils.NotificationUtil


class TimerNotificationActionReceiver : BroadcastReceiver() {
    private lateinit var serviceIntent: Intent
    override fun onReceive(context: Context, intent: Intent) {
        serviceIntent = Intent(context, TimerService::class.java)
        when (intent.action){
            Constants.ACTION_STOP -> {
                AuthorizedActivity.removeAlarm(context)
                PrefUtil.setTimerState(AuthorizedActivity.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
            }
            Constants.ACTION_PAUSE -> {
                var secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = AuthorizedActivity.nowSeconds

                secondsRemaining -= nowSeconds - alarmSetTime
                PrefUtil.setSecondsRemaining(secondsRemaining, context)

                AuthorizedActivity.removeAlarm(context)
                PrefUtil.setTimerState(AuthorizedActivity.TimerState.Paused, context)

                NotificationUtil.showTimerPaused(context)
            }
            Constants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val wakeUpTime = AuthorizedActivity.setAlarm(context, AuthorizedActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(AuthorizedActivity.TimerState.Running, context)
//                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
            Constants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val wakeUpTime = AuthorizedActivity.setAlarm(context, AuthorizedActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(AuthorizedActivity.TimerState.Running, context)
                PrefUtil.setSecondsRemaining(secondsRemaining, context)
//                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}