package com.license.workguru_app.profile.data.remote

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.HistoryResponse
import com.license.workguru_app.timetracking.data.remote.DTO.CategoryResponse
import com.license.workguru_app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileApi {

    @GET(Constants.GET_USER_HISTORY)

    suspend fun listUserHistory(@Header("Authorization") token:String): List<HistoryResponse>
}