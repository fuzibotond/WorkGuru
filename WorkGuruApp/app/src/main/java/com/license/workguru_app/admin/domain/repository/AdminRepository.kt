package com.license.workguru_app.admin.domain.repository

import com.license.workguru_app.admin.data.remote.DTO.InviteUserRequest
import com.license.workguru_app.admin.data.remote.DTO.InviteUserResponse
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginResponse
import com.license.workguru_app.di.RetrofitInstance

class AdminRepository {
    suspend fun inviteUser(access_token:String, request: InviteUserRequest): InviteUserResponse {
        return RetrofitInstance.apiAdmin.inviteUser(access_token, request)
    }
}