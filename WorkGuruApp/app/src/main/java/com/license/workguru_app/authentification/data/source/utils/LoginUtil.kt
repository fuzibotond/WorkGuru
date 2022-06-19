package com.license.workguru_app.authentification.data.source.utils

import android.util.Patterns
import java.util.regex.Pattern

object LoginUtil {

    /**
     * the input is bot valid if ...
     * ...the email/password is empty
     * ...the email has not got email format
     * ...the password too short
     */
    fun validateLoginInput(
        email:String,
        password:String
    ): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (email.isEmpty() || password.isEmpty()){
            return false
        }
        if (password.length < 3){
            return false
        }
        if (!email.matches(emailPattern.toRegex())){
            return false
        }

        return true
    }
}