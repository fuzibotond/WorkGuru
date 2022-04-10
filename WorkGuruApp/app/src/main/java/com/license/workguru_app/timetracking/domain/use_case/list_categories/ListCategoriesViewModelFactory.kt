package com.license.workguru_app.timetracking.domain.use_case.list_categories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class ListCategoriesViewModelFactory(private val context: Context, private val repository: TimeTrackingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListCategoriesViewModel(context, repository) as T
    }
}