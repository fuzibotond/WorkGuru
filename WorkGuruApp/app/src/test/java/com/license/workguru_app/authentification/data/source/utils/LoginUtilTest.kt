package com.license.workguru_app.authentification.data.source.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginUtilTest{

    @Test
    fun `empty email returns false`(){
        val result = LoginUtil.validateLoginInput(
            "",
            "password"
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`(){
        val result = LoginUtil.validateLoginInput(
            "joe.biden@presidentmail.com",
            ""
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `valid email and password returns true`(){
        val result = LoginUtil.validateLoginInput(
            "joe.biden@presidentmail.com",
            "Awesome_president21"
        )

        assertThat(result).isTrue()
    }

    @Test
    fun `too short password returns false`(){
        val result = LoginUtil.validateLoginInput(
            "joe.biden@presidentmail.com",
            "12"
        )

        assertThat(result).isFalse()
    }
}