package com.license.workguru_app.authentification.domain.use_case.register_manually

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.reset_your_password.ForgotPasswordViewModel

class RegisterViewModelFactory(private val context: Context, private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(context, repository) as T
    }
}