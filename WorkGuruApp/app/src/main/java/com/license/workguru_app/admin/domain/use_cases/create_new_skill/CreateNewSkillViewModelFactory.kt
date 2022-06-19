package com.license.workguru_app.admin.domain.use_cases.create_new_skill

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.admin.data.repository.AdminRepository

class CreateNewSkillViewModelFactory(private val context: Context, private val repository: AdminRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateNewSkillViewModel(context, repository) as T
    }
}