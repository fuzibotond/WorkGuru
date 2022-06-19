package com.license.workguru_app.timetracking.data.remote.DTO

data class Project(
    val category_id: Int,
    val created_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val updated_at: String
)