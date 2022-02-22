package com.example.workguru_app.authentification.data.remote

import com.example.workguru_app.authentification.data.remote.DTO.*
import com.example.workguru_app.utils.Constants
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthentificationApi {
    @POST(Constants.LOGIN_URL)
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST(Constants.REGISTER_URL)
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST(Constants.RESET_PASSWORD_URL)
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse
}