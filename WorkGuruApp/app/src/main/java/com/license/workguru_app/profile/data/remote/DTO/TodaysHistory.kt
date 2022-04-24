package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TodaysHistory(
    val percent: String,
    val today_tracked: Int,
    val yesterday_tracked: Int
)