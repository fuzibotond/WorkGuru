package com.license.workguru_app.timetracking.domain.use_case.list_projects

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class ListProjectsViewModelFactory(private val context: Context, private val repository: TimeTrackingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListProjectsViewModel(context, repository) as T
    }
}