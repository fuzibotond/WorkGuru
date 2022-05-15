package com.license.workguru_app.profile.domain.use_case.display_all_colleagues

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_cities.ListCitiesViewModel

class ListColleaguesViewModelFactory(private val context: Context, private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListColleaguesViewModel(context, repository) as T
    }
}