package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ProjectHistory(
    @Json(name = "project_id") val project_id: Int,
    @Json(name = "result") val result: String,
    @Json(name = "name") val name: String,
)