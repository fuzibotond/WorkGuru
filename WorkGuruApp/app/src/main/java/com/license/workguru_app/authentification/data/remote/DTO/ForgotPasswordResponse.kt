package com.example.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class ForgotPasswordResponse(
    val errors: Errors,
    val message: String
)