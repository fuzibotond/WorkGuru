package com.license.workguru_app.timetracking.domain.model

data class Statement(
    var total:String="",
    var tracked_time:String="",
    var less_tracked_time:String="",
    var percentage:String=""
)