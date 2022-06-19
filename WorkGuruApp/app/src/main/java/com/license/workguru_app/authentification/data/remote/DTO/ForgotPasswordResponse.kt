package com.license.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class ForgotPasswordResponse(
    var errors: Errors? = null,
    val message: String
)