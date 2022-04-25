package com.license.workguru_app.profile.domain.use_case.change_user_profile_data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.ProfileRequest
import com.license.workguru_app.profile.domain.repository.ProfileRepository

class ChangeProfileDataViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    suspend fun changeData(
        city:String?,
        street_address:String?,
        country: String?,
        avatar: String,
        _method:String,
        state:String
    ):Boolean {

        val request = ProfileRequest(city!!,street_address!!,country!!,avatar!!,_method, state)
        Toast.makeText(context, "${request}", Toast.LENGTH_SHORT).show()

        val access_token = getToken()
        try {
            val result = repository.changeProfileData("Bearer " + access_token,
                city,
                street_address,
                country,
                avatar,
                _method,
                state )
            Log.d("PROFILE", "${result.message}")
            Toast.makeText(context, "${result.message}", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("PROFILE", "ChangeProfileDataViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}