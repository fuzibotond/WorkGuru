package com.license.workguru_app.timetracking.data.remote.DTO

data class Data(
    val category_name: String,
    val id: Int,
    val members: Int,
    val name: String,
    val start_date: String,
    val tasks: Int,
    val tracked: Int
)