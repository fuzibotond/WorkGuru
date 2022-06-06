package com.license.workguru_app.timetracking.data.remote

import com.google.firebase.installations.remote.TokenResult
import com.license.workguru_app.timetracking.data.remote.DTO.*
import com.license.workguru_app.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface TimeTrackingApi {
    @GET(Constants.GET_PROJECT_URL)
    suspend fun listProjects(@Header("Authorization") token:String, @Query("category_id") category_id:String): ProjectResponse

    @GET(Constants.GET_PROJECT_URL)
    suspend fun listProjectsByPage(@Header("Authorization") token:String, @Query("page") page:Int): ProjectResponse

    @GET(Constants.GET_PROJECT_URL)
    suspend fun listAllProjectWithoutPagination(@Header("Authorization") token:String, @Query("paginate") pagination:Boolean): AllProjectResponse

    @GET(Constants.GET_PROJECT_URL)
    suspend fun listAllProjects(@Header("Authorization") token:String,@Header("automatic") automatic:Boolean, @Header("category_id") project_id:String): ProjectResponse

    @GET(Constants.GET_CATEGORIES_URL)
    suspend fun listCategories(@Header("Authorization") token:String): CategoryResponse

    @POST(Constants.CREATE_TIMER)
    suspend fun startTimer(@Header("Authorization") token:String, @Body request: StartStopTimerRequest): StartTimerResponse

    @PUT(Constants.PAUSE_TIMER)
    suspend fun pauseTimer(@Header("Authorization") token:String, @Body request: PauseTimerRequest): MessageResponse

    @PUT(Constants.STOP_TIMER)
    suspend fun stopTimer(@Header("Authorization") token:String, @Body request: StopRequest): Response<Unit>

    @POST(Constants.CREATE_PROJECT)
    suspend fun newProject(@Header("Authorization") token:String,@Body request: ProjecttRequest): NewProjectResponse

    @POST(Constants.CREATE_CATEGORY)
    suspend fun newCategory(@Header("Authorization") token:String,@Body request: CategoryRequest): NewCategoryResponse

    @PUT(Constants.CREATE_TIMER)
    suspend fun updateTimer(@Header("Authorization") token:String, @Body request: UpdateTimerRequest): Response<Unit>

    @PUT(Constants.UPDATE_PROJECT+"{id}")
    suspend fun updateProject(@Header("Authorization") token:String, @Path("id") project_id:Int, @Body request: UpdateProjectRequest): MessageResponse

    @GET(Constants.GET_A_SPECIFIC_TIMER+"{id}")
    suspend fun getSpecificTimer(@Header("Authorization") token:String, @Path("id") timer_id:Int): TimerResponse
}