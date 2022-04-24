package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ProjectHistory(
    val name: String,
    val project_id: Int,
    val result: String
)