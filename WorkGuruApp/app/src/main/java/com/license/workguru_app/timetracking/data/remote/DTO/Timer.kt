package com.license.workguru_app.timetracking.data.remote.DTO

data class Timer(
    val auto_ended_at: Any,
    val auto_started_at: Any,
    val continued_from_id: Any,
    val created_at: String,
    val description: String,
    val ended_at: String,
    val id: Int,
    val paused: Any,
    val project_id: Int,
    val started_at: String,
    val updated_at: String,
    val user_id: Int
)