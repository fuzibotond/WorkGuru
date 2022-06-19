package com.license.workguru_app.help_request.data.remote

import com.license.workguru_app.help_request.data.remote.DTO.*
import com.license.workguru_app.timetracking.data.remote.DTO.MessageResponse
import com.license.workguru_app.utils.Constants
import retrofit2.http.*

interface HelpRequestApi {

    @GET(Constants.GET_PROGRAMMING_LANGUAGE)
    suspend fun getSkills(@Header("Authorization") token:String, @Query("page") page:Int):SkillsResponse

    @GET(Constants.GET_STATUSES)
    suspend fun getStatuses(@Header("Authorization") token:String, @Query("page") page:Int):StatusesResponse

    @PUT(Constants.CHANGE_STATUSES)
    suspend fun changeStatuses(@Header("Authorization") token:String, @Body request:ChangeStatusRequest):MessageResponse

    @POST(Constants.SEND_MESSAGES+"{id}")
    suspend fun sendMessage(@Header("Authorization") token:String, @Path("id") user_id:Int, @Body request:SendMessageRequest):MessageResponse

    @GET(Constants.GET_MESSAGES_RELATED_TO_USER+"{id}")
    suspend fun getMessages(@Header("Authorization") token:String, @Path("id") user_id:Int, @Query("page") page:Int):GetMessagesByUserIdResponse
}