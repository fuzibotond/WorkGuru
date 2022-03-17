package com.license.workguru_app.authentification.data.remote

import com.license.workguru_app.authentification.data.remote.DTO.*
import com.license.workguru_app.utils.Constants
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthentificationApi {
    @POST(Constants.LOGIN_URL)
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST(Constants.REGISTER_URL)
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST(Constants.RESET_PASSWORD_URL)
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    @POST(Constants.FORGOT_PASSWORD_URL)
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @POST(Constants.GOOGLE_LOGIN_URL)
    suspend fun googleLogin(@Body request: GoogleLoginRequest): GoogleLoginResponse
}