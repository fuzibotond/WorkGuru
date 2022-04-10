package com.license.workguru_app.timetracking.domain.repository

import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginResponse
import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.timetracking.data.remote.DTO.CategoryResponse
import com.license.workguru_app.timetracking.data.remote.DTO.ProjectResponse

class TimeTrackingRepository {
    suspend fun listProjects(access_token:String,automatic:Boolean, project_id:String): ProjectResponse {
        return RetrofitInstance.apiTimeTracking.listProjects(access_token,automatic, project_id)
//        return RetrofitInstance.apiTimeTracking.listProjects(access_token,request = request)
    }
    suspend fun listCategories(access_token:String): CategoryResponse {
        return RetrofitInstance.apiTimeTracking.listCategories(access_token)
    }
}