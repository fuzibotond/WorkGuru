package com.license.workguru_app.timetracking.data.remote.DTO

data class ProjectResponse(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)