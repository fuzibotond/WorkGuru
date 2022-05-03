package com.license.workguru_app.timetracking.domain.use_case.create_new_category

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class NewCategoryViewModelFactory(private val context: Context, private val repository: TimeTrackingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewCategoryViewModel(context, repository) as T
    }
}