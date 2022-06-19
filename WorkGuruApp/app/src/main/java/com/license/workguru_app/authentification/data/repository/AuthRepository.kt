package com.license.workguru_app.authentification.data.repository

import com.license.workguru_app.authentification.data.remote.DTO.*
import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AuthRepository {
    suspend fun login(request: LoginRequest): LoginResponse {
        return RetrofitInstance.API.login(request)
    }
    suspend fun register(request: RegisterRequest): RegisterResponse {
        return RetrofitInstance.API.register(request)
    }

    suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        return RetrofitInstance.API.resetPassword(request)
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): ForgotPasswordResponse {
        return RetrofitInstance.API.forgotPassword(request)
    }

    suspend fun googleLogin(request: GoogleRequest): GoogleResponse {
        return RetrofitInstance.API.googleLogin(request)
    }

    suspend fun googleRegister(request: GoogleRequest): GoogleResponse {
        return RetrofitInstance.API.googleRegister(request)
    }

    suspend fun loginWithFace(request: LoginWithFaceIdRequest): MessageResponse {
        return RetrofitInstance.API.loginWithFace(request)
    }

    suspend fun loginWithFace(request: MultipartBody.Part?, email: RequestBody): LoginWithFaceResponse {
        return RetrofitInstance.API.loginWithFace(request, email)
    }

    suspend fun logout(access_token:String): LogoutResponse {
        return RetrofitInstance.API.logout(  access_token)
    }

}