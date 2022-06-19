package com.license.workguru_app.help_request.domain.use_cases.send_message

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository
import com.license.workguru_app.help_request.domain.use_cases.list_statuses.ListStatusesViewModel

class SendMessageViewModelFactory(private val context: Context, private val repository: HelpRequestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SendMessageViewModel(context, repository) as T
    }
}