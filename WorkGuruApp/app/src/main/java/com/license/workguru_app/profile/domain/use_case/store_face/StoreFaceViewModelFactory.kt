package com.license.workguru_app.profile.domain.use_case.store_face

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.delete_face_from_profile.DeleteFaceViewModel

class StoreFaceViewModelFactory(private val context: Context, private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StoreFaceViewModel(context, repository) as T
    }
}