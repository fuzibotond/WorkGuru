package com.license.workguru_app.authentification.data.remote

import com.license.workguru_app.authentification.data.remote.DTO.*
import com.license.workguru_app.profile.data.remote.DTO.MessageResponse
import com.license.workguru_app.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthenticationApi {
    @POST(Constants.LOGIN_URL)
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST(Constants.LOGIN_WITH_FACE)
    suspend fun loginWithFace(@Body request: LoginWithFaceIdRequest): MessageResponse

    @Multipart
    @POST(Constants.LOGIN_WITH_FACE)
    suspend fun loginWithFace(@Part avatar: MultipartBody.Part?, @Part("email") email:RequestBody) : LoginWithFaceResponse

    @POST(Constants.REGISTER_URL)
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST(Constants.RESET_PASSWORD_URL)
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    @POST(Constants.FORGOT_PASSWORD_URL)
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @POST(Constants.GOOGLE_LOGIN_URL)
    suspend fun googleLogin(@Body request: GoogleRequest): GoogleResponse

    @POST(Constants.GOOGLE_REGISTER_URL)
    suspend fun googleRegister(@Body request: GoogleRequest): GoogleResponse

    @POST(Constants.LOGOUT_URL)
    suspend fun logout(@Header("Authorization") token:String): LogoutResponse

}