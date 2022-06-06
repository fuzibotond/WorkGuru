package com.license.workguru_app.authentification.data.repository

import com.license.workguru_app.authentification.data.remote.DTO.*
import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.MessageResponse

class AuthRepository {
    suspend fun login(request: LoginRequest): LoginResponse {
        return RetrofitInstance.api.login(request)
    }
    suspend fun register(request: RegisterRequest): RegisterResponse {
        return RetrofitInstance.api.register(request)
    }

    suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        return RetrofitInstance.api.resetPassword(request)
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): ForgotPasswordResponse {
        return RetrofitInstance.api.forgotPassword(request)
    }

    suspend fun googleLogin(request: GoogleRequest): GoogleResponse {
        return RetrofitInstance.api.googleLogin(request)
    }

    suspend fun googleRegister(request: GoogleRequest): GoogleResponse {
        return RetrofitInstance.api.googleRegister(request)
    }

    suspend fun loginWithFace(request: LoginWithFaceIdRequest): MessageResponse {
        return RetrofitInstance.api.loginWithFace(request)
    }

    suspend fun logout(access_token:String): LogoutResponse {
        return RetrofitInstance.api.logout(  access_token)
    }

}