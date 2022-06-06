package com.license.workguru_app.timetracking.data.repository

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.timetracking.data.remote.DTO.*
import retrofit2.Response

class TimeTrackingRepository {
    suspend fun listProjects(access_token:String, category_id:String): ProjectResponse {
        return RetrofitInstance.apiTimeTracking.listProjects(access_token, category_id)
    }

    suspend fun listProjectsByPage(access_token:String, page:Int): ProjectResponse {
        return RetrofitInstance.apiTimeTracking.listProjectsByPage(access_token, page)
    }

    suspend fun listAllProjects(access_token:String,automatic:Boolean, project_id:String): ProjectResponse {
        return RetrofitInstance.apiTimeTracking.listAllProjects(access_token,automatic, project_id)
    }
    suspend fun listAllProjectsWithoutPagination(access_token:String,pagination:Boolean): AllProjectResponse {
        return RetrofitInstance.apiTimeTracking.listAllProjectWithoutPagination(access_token,pagination)
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
    suspend fun stopTimer(access_token:String, request: StopRequest): Response<Unit> {
        return RetrofitInstance.apiTimeTracking.stopTimer(access_token, request)
    }

    suspend fun newProject(access_token:String, request: ProjecttRequest): NewProjectResponse {
        return RetrofitInstance.apiTimeTracking.newProject(access_token, request)
    }

    suspend fun newCategory(access_token:String, request: CategoryRequest): NewCategoryResponse {
        return RetrofitInstance.apiTimeTracking.newCategory(access_token, request)
    }

    suspend fun updateTimer(access_token:String, request: UpdateTimerRequest): Response<Unit> {
        return RetrofitInstance.apiTimeTracking.updateTimer(access_token, request)
    }

    suspend fun updateProject(access_token:String,project_id: Int, request: UpdateProjectRequest): MessageResponse {
        return RetrofitInstance.apiTimeTracking.updateProject(access_token, project_id, request)
    }

    suspend fun getSpecificTimer(access_token:String,timer_id: Int): TimerResponse {
        return RetrofitInstance.apiTimeTracking.getSpecificTimer(access_token, timer_id)
    }
}