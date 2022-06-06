package com.license.workguru_app.timetracking.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class StartStopTimerRequest(
    val automatic: Boolean?,
    val project_id: String?,
    val description: String?,
    val language_ids: List<String>?,
)