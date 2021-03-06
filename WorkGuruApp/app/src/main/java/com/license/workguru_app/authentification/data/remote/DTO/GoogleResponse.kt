package com.license.workguru_app.authentification.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleResponse(
    val access_token:String,
    val expires_at:String
)