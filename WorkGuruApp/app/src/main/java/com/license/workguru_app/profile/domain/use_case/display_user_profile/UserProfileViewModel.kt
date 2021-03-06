package com.license.workguru_app.profile.domain.use_case.display_user_profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.profile.data.remote.DTO.UserProfile
import com.license.workguru_app.profile.data.repository.ProfileRepository

class UserProfileViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    val data:MutableLiveData<UserProfile> = MutableLiveData()
    suspend fun getUserProfileInfo( ):Boolean {

        val access_token = getToken()
        try {
            val result = repository.getUserProfile("Bearer " + access_token)
            data.value = result.data
            Log.d("PROFILE", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("PROFILE", "UserProfileViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}