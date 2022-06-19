package com.license.workguru_app.profile.domain.use_case.display_countries

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.profile.data.remote.DTO.Country
import com.license.workguru_app.profile.data.repository.ProfileRepository

class ListCountriesViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    val countries:MutableLiveData<List<Country>> = MutableLiveData()
    suspend fun listCountries( ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.listCountries("Bearer " + access_token)
            countries.value = result.data
            Log.d("COUNTRIES", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("COUNTRIES", "ListCountriesViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}