package com.license.workguru_app.admin.data.remote

import com.license.workguru_app.admin.data.remote.DTO.InviteUserRequest
import com.license.workguru_app.admin.data.remote.DTO.InviteUserResponse
import com.license.workguru_app.admin.data.remote.DTO.NewSkillRequest
import com.license.workguru_app.admin.data.remote.DTO.UsersToBeAcceptedResponse
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginResponse
import com.license.workguru_app.help_request.data.remote.DTO.GetMessagesByUserIdResponse
import com.license.workguru_app.timetracking.data.remote.DTO.MessageResponse
import com.license.workguru_app.utils.Constants
import retrofit2.http.*

interface AdminApi {
    @POST(Constants.INVITE_USER_URL)
    suspend fun inviteUser(@Header("Authorization") token:String, @Body request: InviteUserRequest): InviteUserResponse

    @POST(Constants.CREATE_NEW_SKILL)
    suspend fun createNewSkill(@Header("Authorization") token:String, @Body request: NewSkillRequest): MessageResponse

    @GET(Constants.GET_USERS_WHO_ARE_WAITING_FOR_ACCEPTANCE)
    suspend fun getUsersWhoAreWaiting(@Header("Authorization") token:String, @Query("page") page:Int): UsersToBeAcceptedResponse

    @PUT(Constants.ACCEPT_USER+"{id}")
    suspend fun acceptUser(@Header("Authorization") token:String, @Path("id") user_id:Int): MessageResponse
}