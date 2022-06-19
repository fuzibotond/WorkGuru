package com.license.workguru_app.help_request.domain.use_cases.list_statuses

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.list_skills.ListSkillsViewModel

class ListStatusesViewModelFactory(private val context: Context, private val repository: HelpRequestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListStatusesViewModel(context, repository) as T
    }
}