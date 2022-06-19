package com.license.workguru_app.timetracking.data.remote.DTO

data class NewCategoryResponse(
    val category_name: String,
    val created_at: String,
    val id: Int
)