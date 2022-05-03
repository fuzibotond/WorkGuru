package com.license.workguru_app.timetracking.data.remote.DTO

data class StartStopTimerRequest(
    val automatic: Boolean,
    val project_id: String
)