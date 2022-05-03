package com.license.workguru_app.timetracking.data.remote.DTO

data class NewProjectResponse(
    val category_name: String,
    val id: Int,
    val project_name: String,
    val start_date: String
)