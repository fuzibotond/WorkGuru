package com.license.workguru_app.profile.domain.use_case.display_user_profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.display_user_insights.UserHistoryViewModel

class UserProfileViewModelFactory(private val context: Context, private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserProfileViewModel(context, repository) as T
    }
}