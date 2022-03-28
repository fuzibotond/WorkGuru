package com.license.workguru_app.authentification.data.remote

import com.license.workguru_app.authentification.data.remote.DTO.*
import com.license.workguru_app.utils.Constants
import okhttp3.RequestBody
import retrofit2.http.*

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
    suspend fun googleLogin(@Body request: GoogleRequest): GoogleResponse

//    @Headers(
//
//        "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMGU1Y2E2MDUzMzdkZDVjOTMxMjA2YTYwYzljMTgyNmRjMjBhNjk3MjNlZTgyZTBlNTJjZjZkNGZmZjA0MWVjYzljMGExNzlhNGM5YmZhMWUiLCJpYXQiOjE2NDgyMDcxNzkuMDgyODU1LCJuYmYiOjE2NDgyMDcxNzkuMDgyODU5LCJleHAiOjE2NDgyOTM1NzguOTk3NTM5LCJzdWIiOiI1NCIsInNjb3BlcyI6W119.r1uhJyruOUD_rGnnZx-9ZOkyx8YCzmYOCP9yE6FjDi86kcCNLuGJTqBbaLix1SMeXPYdUu1SUjZ6cxKagwyjeFjtukvQyjnEDPZuiNTreazJeuhAoSDjwtobDBMdCd8oroOweX9oDUNf05O_po_qf6Szv3fR0lbpQzqO4LgiBTzqGGrTrs4Lo0P466yQ-8VLeyDIwVQq_tzXmRxFWH0agmxfUvHu-qSRU7erHeMEcYS9qM2Y7yo9NlT2gPxMGtZqx1WzrNgNj5YZFkcGWkZXbIXJqMJLvq2WpHX-hzyxoHvOMsfil7ZLvYGKlKqlQw_gdWFc4CIF8Frd0DIbVTJoO8FXRxEck7yiQZfqE_tAm5zfUi3hi1PCqpJPgDabTDMhMgIUtujcN4yUBjpWvPL-GkOkd0reQMIru71HrzDgpFjYyff8T04okLi-DplywFt2zjYeMcESumvrjCevoUXbxsoV5MFg1Z1HYRRqtchR32b9dpCySsuQwqo3SLFZst9yLiJKBPKM4JoNAzV223_4_9vAMED18RyA54e8vy32QrNCmFlfXbxT5MiWw7QAnwu20rPw2yXeFI3ABoITXX3KsHzKAdaYSGfZTubbmEFEk_oMMSk_wK2RycISFH_lxbAIyP3WfJvcNVJrTSQLZ3xOO9gkHfKlRLV_D7gW0N51ddA"
//    )
    @POST(Constants.LOGOUT_URL)
    suspend fun logout(@Header("Authorization") token:String): LogoutResponse


}