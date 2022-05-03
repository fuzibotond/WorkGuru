package com.license.workguru_app.timetracking.domain.use_case.create_new_project

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository
import com.license.workguru_app.timetracking.domain.use_case.list_categories.ListCategoriesViewModel

class NewProjectViewModelFactory(private val context: Context, private val repository: TimeTrackingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewProjectViewModel(context, repository) as T
    }
}