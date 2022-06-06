package com.license.workguru_app.profile.domain.use_case.display_all_colleagues

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.Colleague
import com.license.workguru_app.profile.data.repository.ProfileRepository

class ListColleaguesViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    val colleagues: MutableLiveData<List<Colleague>> = MutableLiveData()
    suspend fun listColleagues(page:Int ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.listColleagues("Bearer " + access_token,page)
            colleagues.value = result.data
            Log.d("COLLEAGUES", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("COLLEAGUES", "ListColleaguesViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}