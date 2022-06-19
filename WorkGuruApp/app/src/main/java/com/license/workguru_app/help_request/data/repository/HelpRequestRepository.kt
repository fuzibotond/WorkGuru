package com.license.workguru_app.help_request.data.repository

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.help_request.data.remote.DTO.*
import com.license.workguru_app.timetracking.data.remote.DTO.MessageResponse

class HelpRequestRepository {

    suspend fun getSkills(access_token:String, page: Int):SkillsResponse {
        return RetrofitInstance.helpRequest.getSkills(access_token, page)
    }

    suspend fun getStatuses(access_token:String, page: Int):StatusesResponse {
        return RetrofitInstance.helpRequest.getStatuses(access_token, page)
    }

    suspend fun changeStatuses(access_token:String, request:ChangeStatusRequest ):MessageResponse {
        return RetrofitInstance.helpRequest.changeStatuses(access_token, request)
    }

    suspend fun sendMessage(access_token:String, user_id:Int, request:SendMessageRequest ):MessageResponse {
        return RetrofitInstance.helpRequest.sendMessage(access_token, user_id, request)
    }

    suspend fun getMessages(access_token:String, user_id:Int, page:Int ):GetMessagesByUserIdResponse{
        return RetrofitInstance.helpRequest.getMessages(access_token, user_id, page)
    }
}