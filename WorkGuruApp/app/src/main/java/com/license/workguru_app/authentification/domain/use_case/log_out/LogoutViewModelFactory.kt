package com.license.workguru_app.authentification.domain.use_case.log_out

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.authentification.data.repository.AuthRepository

class LogoutViewModelFactory(private val context: Context, private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LogoutViewModel(context, repository) as T
    }
}