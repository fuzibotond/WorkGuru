package com.license.workguru_app.admin.domain.use_cases.accept_user_by_id

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.admin.data.repository.AdminRepository
import com.license.workguru_app.admin.domain.use_cases.list_users_who_are_waiting.ListUsersWhoAreWaitingViewModel

class AcceptUserViewModelFactory(private val context: Context, private val repository: AdminRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AcceptUserViewModel(context, repository) as T
    }
}