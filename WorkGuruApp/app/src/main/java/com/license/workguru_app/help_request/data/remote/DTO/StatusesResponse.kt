package com.license.workguru_app.help_request.data.remote.DTO

data class StatusesResponse(
    val `data`: List<Skill>,
    val links: Links,
    val meta: Meta
)