package com.license.workguru_app.admin.data.repository

import com.license.workguru_app.admin.data.remote.DTO.InviteUserRequest
import com.license.workguru_app.admin.data.remote.DTO.InviteUserResponse
import com.license.workguru_app.admin.data.remote.DTO.NewSkillRequest
import com.license.workguru_app.admin.data.remote.DTO.UsersToBeAcceptedResponse
import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.timetracking.data.remote.DTO.MessageResponse

class AdminRepository {
    suspend fun inviteUser(access_token:String, request: InviteUserRequest): InviteUserResponse {
        return RetrofitInstance.apiAdmin.inviteUser(access_token, request)
    }

    suspend fun createNewSkill(access_token:String, request: NewSkillRequest): MessageResponse {
        return RetrofitInstance.apiAdmin.createNewSkill(access_token, request)
    }

    suspend fun acceptUser(access_token:String, user_id: Int): MessageResponse {
        return RetrofitInstance.apiAdmin.acceptUser(access_token, user_id)
    }

    suspend fun listUsersWhoAreWaiting(access_token:String, page: Int): UsersToBeAcceptedResponse {
        return RetrofitInstance.apiAdmin.getUsersWhoAreWaiting(access_token, page)
    }
}