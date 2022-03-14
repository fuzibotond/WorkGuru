package com.example.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass
import java.sql.Date


@JsonClass(generateAdapter = true)
data class LoginResponse(
    val access_token: String,
    val expires_at: String
)