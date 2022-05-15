package com.license.workguru_app.pomodoro.data.source.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.license.workguru_app.AuthorizedActivity
import com.license.workguru_app.pomodoro.data.source.utils.PrefUtil
//import com.license.workguru_app.utils.NotificationUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(AuthorizedActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}