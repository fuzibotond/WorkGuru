package com.license.workguru_app.help_request.domain.use_cases.change_status

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_cities.ListCitiesViewModel

class ChangeStatusViewModelFactory(private val context: Context, private val repository: HelpRequestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChangeStatusViewModel(context, repository) as T
    }
}