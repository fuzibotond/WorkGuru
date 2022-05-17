package com.license.workguru_app.timetracking.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class ShortProject(
    val project_id: Int,
    val project_name: String,
    val category_name: String

)