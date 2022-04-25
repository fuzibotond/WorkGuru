package com.license.workguru_app.profile.data.remote.DTO

data class UserProfile(
    val active_timer: Int?,
    val avatar: String?,
    val city: String?,
    val country: String?,
    val email: String,
    val id: Int,
    val name: String,
    val phone_number: String?,
    val role: String?,
    val state: String?,
    val street_address: String?,
    val zip: String?
)