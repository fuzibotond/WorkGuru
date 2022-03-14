package com.example.workguru_app.authentification.domain.model

data class User(
    var client_id: String="",
    var client_secret: String="",
    var email: String="",
    var grant_type: String="",
    var password: String="",
    var remember_me: Boolean=false
)