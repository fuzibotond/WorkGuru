package com.license.workguru_app.authentification.domain.use_case.log_in_with_face_recognition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.authentification.data.repository.AuthRepository
import com.license.workguru_app.authentification.domain.use_case.log_in_with_google.GoogleLoginViewModel

class FaceRecognitionViewModelFactory(private val context: Context, private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FaceRecognitionViewModel(context, repository) as T
    }
}