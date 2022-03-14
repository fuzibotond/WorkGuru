package com.example.workguru_app.authentification.domain.use_case.log_in_with_email



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workguru_app.authentification.domain.repository.AuthRepository


class LoginViewModelFactory(private val context: Context, private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(context, repository) as T
    }
}