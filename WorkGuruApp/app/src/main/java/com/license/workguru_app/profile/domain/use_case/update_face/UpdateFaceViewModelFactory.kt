package com.license.workguru_app.profile.domain.use_case.update_face

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.delete_face_from_profile.DeleteFaceViewModel

class UpdateFaceViewModelFactory(private val context: Context, private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpdateFaceViewModel(context, repository) as T
    }
}