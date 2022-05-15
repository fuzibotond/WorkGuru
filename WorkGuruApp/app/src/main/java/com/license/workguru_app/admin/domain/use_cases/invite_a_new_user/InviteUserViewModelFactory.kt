package com.license.workguru_app.admin.domain.use_cases.invite_a_new_user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.admin.domain.repository.AdminRepository

class InviteUserViewModelFactory(private val context: Context, private val repository: AdminRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InviteUserViewModel(context, repository) as T
    }
}