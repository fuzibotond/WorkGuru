package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeeklyHistory(
    @Json(name = "this_week")val this_week: String,
    @Json(name = "last_week")val last_week: String,
    @Json(name = "percent")val percent: Double

)

@JsonClass(generateAdapter = true)
data class WeeklyHistoryShort(
    @Json(name = "this_week")val this_week: String,
    @Json(name = "last_week")val last_week: String,

)