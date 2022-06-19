package com.license.workguru_app.profile.data.remote.DTO

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)


data class ProfileRequest(
    val city:String?,
    val street_address:String?,
    val country:String?,
    val avatar:String?,
    val _method:String?,
    val state:String?,
    val delete_avatar:Boolean?
)