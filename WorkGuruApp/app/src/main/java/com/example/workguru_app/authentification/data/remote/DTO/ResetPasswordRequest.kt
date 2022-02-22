package com.example.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class ResetPasswordRequest(
    val password: String,
    val password_confirmation: String,
    val token: String
)