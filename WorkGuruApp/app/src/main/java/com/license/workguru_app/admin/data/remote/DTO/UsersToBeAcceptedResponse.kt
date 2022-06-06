package com.license.workguru_app.admin.data.remote.DTO

data class UsersToBeAcceptedResponse(
    val `data`: List<WaitingUser>,
    val links: Links,
    val meta: Meta
)