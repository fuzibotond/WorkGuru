package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
class HistoryResponse(
    val data:List<ProjectHistory>,
    val todaysHistory: TodaysHistory,
    val weeklyHistory: WeeklyHistory,
    val monthlyHistory: MonthlyHistory
)

@JsonClass(generateAdapter = true)
class HistoryResponseJson(
    val data:List<ProjectHistory> = listOf(),
    val todaysHistory: TodaysHistory?=null,
    val weeklyHistory: WeeklyHistory?=null,
    val monthlyHistory: MonthlyHistory?=null
)
class CustomObjAdapter {
    @ToJson
    fun toJson(obj: HistoryResponse): HistoryResponse {
        return obj
    }

    @FromJson
    fun fromJson(customObjJson: HistoryResponseJson): HistoryResponse {
        var at1 = customObjJson.data
        // TODO: find a way to convert customObjJson.at2 of type String to Date here
        var at2 = customObjJson.todaysHistory
        var at3 = customObjJson.weeklyHistory
        var at4 = customObjJson.monthlyHistory

        return HistoryResponse(at1, at2!!, at3!! , at4!!)
    }
}