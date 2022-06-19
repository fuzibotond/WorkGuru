package com.license.workguru_app.profile.domain.use_case.delete_face_from_profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.profile.domain.use_case.change_user_profile_data.ChangeProfileDataViewModel

class DeleteFaceViewModelFactory(private val context: Context, private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeleteFaceViewModel(context, repository) as T
    }
}