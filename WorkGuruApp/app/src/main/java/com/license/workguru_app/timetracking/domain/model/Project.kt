package com.license.workguru_app.timetracking.domain.model

data class Project(
    val category_name: String,
    val id: Int,
    val members: Int,
    val name: String,
    val start_date: String,
    val tasks: Int,
    val tracked: Int
)