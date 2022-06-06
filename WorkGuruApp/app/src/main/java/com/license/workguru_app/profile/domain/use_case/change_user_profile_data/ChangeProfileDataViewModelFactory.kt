package com.license.workguru_app.profile.domain.use_case.change_user_profile_data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.profile.data.repository.ProfileRepository

class ChangeProfileDataViewModelFactory(private val context: Context, private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChangeProfileDataViewModel(context, repository) as T
    }
}