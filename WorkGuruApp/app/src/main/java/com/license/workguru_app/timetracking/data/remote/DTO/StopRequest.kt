package com.license.workguru_app.timetracking.data.remote.DTO

data class StopRequest(
    val automatic: Boolean,
    val project_id: String
)