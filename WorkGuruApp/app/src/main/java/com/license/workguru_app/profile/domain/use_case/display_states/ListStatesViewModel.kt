package com.license.workguru_app.profile.domain.use_case.display_states

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.States
import com.license.workguru_app.profile.data.remote.DTO.StatesResponse
import com.license.workguru_app.profile.domain.repository.ProfileRepository

class ListStatesViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
//    val states:MutableLiveData<List<States>> = MutableLiveData()
    var states:MutableLiveData<List<States>> = MutableLiveData<List<States>>()
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun listStates(country_name:String ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.listStates("Bearer " + access_token, country_name )
            var items = mutableListOf<States>()
            result.data.forEach { items.add(it) }
            states.value=items
//            Log.d("STATES", "${states.value}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("STATES", "ListStatesViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}