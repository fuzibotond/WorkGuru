package com.license.workguru_app.timetracking.data.remote.DTO

data class TimerResponse(
    val project: Project,
    val timers: List<Timer>
)