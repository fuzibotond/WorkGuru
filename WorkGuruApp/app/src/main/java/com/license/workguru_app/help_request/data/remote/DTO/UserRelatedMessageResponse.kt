package com.license.workguru_app.help_request.data.remote.DTO

data class UserRelatedMessageResponse(
    val `data`: List<Message>,
    val links: Links,
    val meta: Meta
)