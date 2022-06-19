package com.license.workguru_app.timetracking.data.remote.DTO

data class StartTimerResponse(
    val project_id: String,
    val project_name: String?,
    val started_at: String?,
    val timer_description: String?,
    val timer_id: Int?
)