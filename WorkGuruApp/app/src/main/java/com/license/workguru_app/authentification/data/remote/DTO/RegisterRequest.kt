package com.license.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)