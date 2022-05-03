package com.license.workguru_app.timetracking.data.remote.DTO

import com.license.workguru_app.timetracking.domain.model.Project

data class ProjectResponse(
    val `data`: List<Project>,
    val links: Links,
    val meta: Meta
)