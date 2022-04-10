package com.license.workguru_app.timetracking.data.remote

import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginResponse
import com.license.workguru_app.timetracking.data.remote.DTO.CategoryResponse
import com.license.workguru_app.timetracking.data.remote.DTO.ProjectResponse
import com.license.workguru_app.utils.Constants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TimeTrackingApi {
    @GET(Constants.GET_PROJECT_URL)
//    suspend fun listProjects(@Header("Authorization") token:String, @Body request: ProjectRequest): ProjectResponse
    suspend fun listProjects(@Header("Authorization") token:String, @Header("automatic")automatic:Boolean, @Header("product_id") project_id:String): ProjectResponse

    @GET(Constants.GET_CATEGORIES_URL)
    suspend fun listCategories(@Header("Authorization") token:String): CategoryResponse
}