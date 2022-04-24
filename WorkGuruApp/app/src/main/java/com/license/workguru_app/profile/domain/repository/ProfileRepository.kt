package com.license.workguru_app.profile.domain.repository

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.HistoryResponse
import com.license.workguru_app.timetracking.data.remote.DTO.CategoryResponse

class ProfileRepository {
    suspend fun listUserHistory(access_token:String): List<HistoryResponse> {
        return RetrofitInstance.apiProfile.listUserHistory(access_token)
    }
}