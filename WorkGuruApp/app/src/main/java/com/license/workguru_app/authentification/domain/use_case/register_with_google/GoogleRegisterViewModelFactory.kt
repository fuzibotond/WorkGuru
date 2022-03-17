package com.license.workguru_app.authentification.domain.use_case.register_with_google

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_in_with_google.GoogleLoginViewModel

class GoogleRegisterViewModelFactory(private val context: Context, private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GoogleRegisterViewModel(context, repository) as T
    }
}