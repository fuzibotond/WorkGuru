package com.license.workguru_app.admin.data.remote

import com.license.workguru_app.admin.data.remote.DTO.InviteUserRequest
import com.license.workguru_app.admin.data.remote.DTO.InviteUserResponse
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginResponse
import com.license.workguru_app.utils.Constants
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AdminApi {
    @POST(Constants.INVITE_USER_URL)
    suspend fun inviteUser(@Header("Authorization") token:String, @Body request: InviteUserRequest): InviteUserResponse
}