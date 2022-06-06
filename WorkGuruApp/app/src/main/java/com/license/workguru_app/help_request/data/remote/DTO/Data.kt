package com.license.workguru_app.help_request.data.remote.DTO

data class Data(
    val created_at: String,
    val id: Int,
    val message: String,
    val receiver_id: Int,
    val sender_id: Int
)