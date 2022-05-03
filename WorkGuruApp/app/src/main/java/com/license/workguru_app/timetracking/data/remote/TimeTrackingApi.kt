package com.license.workguru_app.timetracking.data.remote

import com.google.firebase.installations.remote.TokenResult
import com.license.workguru_app.timetracking.data.remote.DTO.*
import com.license.workguru_app.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface TimeTrackingApi {
    @GET(Constants.GET_PROJECT_URL)
//    suspend fun listProjects(@Header("Authorization") token:String, @Body request: ProjectRequest): ProjectResponse
    suspend fun listProjects(@Header("Authorization") token:String, @Header("automatic")automatic:Boolean, @Header("product_id") project_id:String): ProjectResponse

    @GET(Constants.GET_CATEGORIES_URL)
    suspend fun listCategories(@Header("Authorization") token:String): CategoryResponse

    @POST(Constants.CREATE_TIMER)
    suspend fun startTimer(@Header("Authorization") token:String,@Body request: StartStopTimerRequest): StartTimerResponse

    @PUT(Constants.PAUSE_TIMER)
    suspend fun pauseTimer(@Header("Authorization") token:String, @Body request: PauseTimerRequest): MessageResponse

    @PUT(Constants.STOP_TIMER)
    suspend fun stopTimer(@Header("Authorization") token:String, @Body request: StartStopTimerRequest): Response<Unit>
}