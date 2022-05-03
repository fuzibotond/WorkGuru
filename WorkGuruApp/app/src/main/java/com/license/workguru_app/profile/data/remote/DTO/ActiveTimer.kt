package com.license.workguru_app.profile.data.remote.DTO

data class ActiveTimer(
    val automatic: Boolean?,
    val elapsed_seconds: Int,
    val project_id: Int,
    val project_name: String,
    val started_at: String,
    val state: String,
    val task: String?
)