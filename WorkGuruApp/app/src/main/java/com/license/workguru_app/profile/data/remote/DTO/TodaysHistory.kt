package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TodaysHistory(
    @Json(name = "today_tracked") val today_tracked: String,
    @Json(name = "yesterday_tracked") val yesterday_tracked: String,
    @Json(name = "percent") val percent: Double,
)
@JsonClass(generateAdapter = true)
data class TodaysHistoryShort(
    @Json(name = "today_tracked") val today_tracked: String,
    @Json(name = "yesterday_tracked") val yesterday_tracked: String,
)