package com.license.workguru_app.authentification.data.remote.DTO

data class LoginWithFaceIdRequest(
    val email: String,
    val face_image: String
)