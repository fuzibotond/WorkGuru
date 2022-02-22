package com.example.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val client_id: String,
    val client_secret: String,
    val email: String,
    val grant_type: String,
    val password: String,
    val remember_me: Boolean
)