package com.license.workguru_app.profile.data.remote.DTO

data class Colleague(
    val avatar: String?,
    val email: String,
    val id: Int,
    val name: String,
    val role: String,
    val tracked: Int
)