package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeeklyHistory(
    val last_week: Int,
    val percent: String,
    val this_week: Int
)