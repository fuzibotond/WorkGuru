package com.license.workguru_app.authentification.data.source.utils

object RegisterUtil {
    /**
     * the input is not valid if ...
     * ...the email/password/fullname is empty
     * ...the email has not got email format
     * ...the password/fullname too short
     */
    fun validateRegisterInput(
        email:String,
        fullName:String,
        password:String
    ):Boolean{
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()){
            return false
        }
        if (password.length < 3 || fullName.length < 3){
            return false
        }
        if (!email.matches(emailPattern.toRegex())){
            return false
        }

        return true
    }
}