package com.license.workguru_app.timetracking.domain.repository

import com.google.firebase.installations.remote.TokenResult
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginResponse
import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.timetracking.data.remote.DTO.*
import retrofit2.Response

class TimeTrackingRepository {
    suspend fun listProjects(access_token:String,automatic:Boolean, project_id:String): ProjectResponse {
        return RetrofitInstance.apiTimeTracking.listProjects(access_token,automatic, project_id)
//        return RetrofitInstance.apiTimeTracking.listProjects(access_token,request = request)
    }
    suspend fun listCategories(access_token:String): CategoryResponse {
        return RetrofitInstance.apiTimeTracking.listCategories(access_token)
    }

    suspend fun startTimer(access_token:String, request: StartStopTimerRequest): StartTimerResponse {
        return RetrofitInstance.apiTimeTracking.startTimer(access_token, request)
    }
    suspend fun pauseTimer(access_token:String, request: PauseTimerRequest): MessageResponse {
        return RetrofitInstance.apiTimeTracking.pauseTimer(access_token, request)
    }
    suspend fun stopTimer(access_token:String, request: StartStopTimerRequest): Response<Unit> {
        return RetrofitInstance.apiTimeTracking.stopTimer(access_token, request)
    }
}