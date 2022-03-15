package com.license.workguru_app.authentification.domain.use_case.reset_your_password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_in_with_email.LoginViewModel


class ForgotPasswordViewModelFactory(private val context: Context, private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ForgotPasswordViewModel(context, repository) as T
    }
}