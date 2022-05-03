package com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.Data
import com.license.workguru_app.timetracking.data.remote.DTO.PauseTimerRequest
import com.license.workguru_app.timetracking.data.remote.DTO.StartStopTimerRequest
import com.license.workguru_app.timetracking.data.remote.DTO.StartTimerResponse
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class StartPauseStopViewModel(val context: Context, val repository: TimeTrackingRepository) : ViewModel() {
    val startedTimer:MutableLiveData<StartTimerResponse> = MutableLiveData()
    suspend fun startTimer(automatic:Boolean, project_id:String, description:String ):Boolean {
        val access_token = getToken()
        try {
            val request = StartStopTimerRequest(automatic = automatic, project_id = project_id)
            Log.d("Timer", "${request}")
            val result = repository.startTimer("Bearer " + access_token, request)
            Toast.makeText(context, "You just started a new timer!", Toast.LENGTH_SHORT).show()
            Log.d("TIMER", "${result}")
            startedTimer.value = result
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("TIMER", "StartPauseStopViewModel - exception: ${e.toString()}")
            return false
        }
    }

    suspend fun stopTimer(automatic:Boolean, project_id:String ):Boolean {
        val access_token = getToken()
        try {
            val request = StartStopTimerRequest(automatic, project_id)
            val result = repository.stopTimer("Bearer " + access_token, request)
            Log.d("TIMER", "${result}")
            Toast.makeText(context, "The timer was stopped!", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("TIMER", "StartPauseStopViewModel - exception: ${e.toString()}")
            return false
        }
    }

    suspend fun pauseTimer(automatic:Boolean,description:String, project_id:String ):Boolean {
        val access_token = getToken()
        try {
            val request = PauseTimerRequest(automatic,description, project_id)
            val result = repository.pauseTimer("Bearer " + access_token, request)
            Toast.makeText(context, "The timer was paused!", Toast.LENGTH_SHORT).show()

            Log.d("TIMER", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("TIMER", "StartPauseStopViewModel - exception: ${e.toString()}")
            return false
        }
    }

    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}