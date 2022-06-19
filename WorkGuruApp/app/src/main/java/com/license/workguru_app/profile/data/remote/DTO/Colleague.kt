package com.license.workguru_app.profile.data.remote.DTO

import com.license.workguru_app.help_request.data.remote.DTO.Skill

data class Colleague(
    val avatar: String?,
    val email: String,
    val id: Int,
    val name: String,
    val role: String,
    val languages: List<TrackedSkill>,
    val status: String?,
    val tracked: Int
)