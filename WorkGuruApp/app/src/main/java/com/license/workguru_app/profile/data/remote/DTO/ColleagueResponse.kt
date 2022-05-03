package com.license.workguru_app.profile.data.remote.DTO

data class ColleagueResponse(
    val `data`: List<Colleague>,
    val links: Links,
    val meta: Meta
)