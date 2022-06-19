package com.license.workguru_app.timetracking.data.remote.DTO

data class PauseTimerRequest(
    val automatic: Boolean,
    val project_id: String,
    val description: String
)