package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class MonthlyHistory(
    @Json(name = "this_month")val this_month: String,
    @Json(name = "last_month")val last_month: String,
    @Json(name = "percent")val percent: Double,
)
@JsonClass(generateAdapter = true)

data class MonthlyHistoryShort(
    @Json(name = "this_month")val this_month: String,
    @Json(name = "last_month")val last_month: String,
)