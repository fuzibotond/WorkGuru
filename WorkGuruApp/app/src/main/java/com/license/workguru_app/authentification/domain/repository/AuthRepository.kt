package com.license.workguru_app.authentification.domain.repository

import com.license.workguru_app.authentification.data.remote.DTO.*
import com.license.workguru_app.di.RetrofitInstance

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

    suspend fun googleLogin(request: GoogleLoginRequest): GoogleLoginResponse {
        return RetrofitInstance.api.googleLogin(request)
    }
}