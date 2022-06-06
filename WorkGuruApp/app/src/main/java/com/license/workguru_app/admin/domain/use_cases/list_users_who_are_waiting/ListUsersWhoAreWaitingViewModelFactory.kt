package com.license.workguru_app.admin.domain.use_cases.list_users_who_are_waiting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.admin.data.repository.AdminRepository
import com.license.workguru_app.admin.domain.use_cases.invite_a_new_user.InviteUserViewModel

class ListUsersWhoAreWaitingViewModelFactory(private val context: Context, private val repository: AdminRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListUsersWhoAreWaitingViewModel(context, repository) as T
    }
}