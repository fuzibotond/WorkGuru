package com.license.workguru_app.admin.domain.use_cases.create_new_skill

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.admin.data.remote.DTO.NewSkillRequest
import com.license.workguru_app.admin.data.repository.AdminRepository

class CreateNewSkillViewModel(val context: Context, val repository: AdminRepository) : ViewModel() {
    suspend fun createNewSkill(name: String):Boolean {
        val access_token = getToken()
        val request = NewSkillRequest(name)

        try {
            val result = repository.createNewSkill("Bearer " + access_token, request)
            Log.d("NEW_SKILL", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.email_already_exist), Toast.LENGTH_SHORT).show()
            Log.d("NEW_SKILL", "CreateNewViewModel - exception: ${e.toString()}")

            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}
