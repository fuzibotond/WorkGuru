package com.license.workguru_app.help_request.domain.use_cases.get_messages_related_to_a_user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.change_status.ChangeStatusViewModel

class ListMessagesRelatedToAUserFactory(private val context: Context, private val repository: HelpRequestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListMessagesRelatedToAUser(context, repository) as T
    }
}