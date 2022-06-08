package com.license.workguru_app.profile.domain.use_case.delete_face_from_profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.StoreFaceRequest
import com.license.workguru_app.profile.data.repository.ProfileRepository


class DeleteFaceViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    suspend fun deleteFacePhoto():Boolean {

        val access_token = getToken()
        try {

            val result = repository.deleteFace("Bearer " + access_token)
            Log.d("PROFILE", "DeleteFaceViewModel - exception: ${result}")

            return true

        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("PROFILE", "DeleteFaceViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}