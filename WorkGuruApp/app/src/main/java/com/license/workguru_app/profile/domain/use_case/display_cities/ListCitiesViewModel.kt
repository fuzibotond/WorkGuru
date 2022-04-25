package com.license.workguru_app.profile.domain.use_case.display_cities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.CitiesResponse
import com.license.workguru_app.profile.data.remote.DTO.City
import com.license.workguru_app.profile.data.remote.DTO.ProjectHistory
import com.license.workguru_app.profile.domain.repository.ProfileRepository

class ListCitiesViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    val cities: MutableLiveData<List<City>> = MutableLiveData()
    suspend fun listCities(state_id:Int ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.listCities("Bearer " + access_token,state_id)
            cities.value = result.data
            Log.d("CITIES", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("CITIES", "ListCitiesViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}