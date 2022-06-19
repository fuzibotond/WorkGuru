package com.license.workguru_app.authentification.data.source.utils

import com.google.common.truth.Truth
import org.junit.Assert.*
import org.junit.Test

class RegisterUtilTest{
    @Test
    fun `empty email returns false`(){
        val result = RegisterUtil.validateRegisterInput(
            "",
            "Joe Biden",
            "Awesome_president21"
        )

        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`(){
        val result = RegisterUtil.validateRegisterInput(
            "joe.biden@presidentmail.com",
            "Joe Biden",
            ""
        )

        Truth.assertThat(result).isFalse()
    }
    @Test
    fun `empty fullName returns false`(){
        val result = RegisterUtil.validateRegisterInput(
            "joe.biden@presidentmail.com",
            "",
            "Awesome_president21"
        )

        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `valid email and password returns true`(){
        val result = RegisterUtil.validateRegisterInput(
            "joe.biden@presidentmail.com",
            "Joe Biden",
            "Awesome_president21",
        )

        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `too short password returns false`(){
        val result = RegisterUtil.validateRegisterInput(
            "joe.biden@presidentmail.com",
            "Joe Biden",
            "12"
        )

        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `too short fullName returns false`(){
        val result = RegisterUtil.validateRegisterInput(
            "joe.biden@presidentmail.com",
            "Jo",
            "12"
        )

        Truth.assertThat(result).isFalse()
    }
}