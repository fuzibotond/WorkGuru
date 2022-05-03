package com.license.workguru_app.timetracking.domain.use_case.start_pause_stop_timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class StartPauseStopViewModelFactory(private val context: Context, private val repository: TimeTrackingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StartPauseStopViewModel(context, repository) as T
    }
}