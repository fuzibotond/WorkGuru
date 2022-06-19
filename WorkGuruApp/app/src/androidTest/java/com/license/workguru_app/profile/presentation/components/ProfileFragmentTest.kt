package com.license.workguru_app.profile.presentation.components

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.license.workguru_app.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest{


    lateinit var scenario: FragmentScenario<ProfileFragment>

    @Before
    fun setup(){
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_WorkGuruApp)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun testInputPhoneNumber(){
        val phoneNumber = "0712345678"
        onView(withId(R.id.phone_number_input_profile))
            .perform(scrollTo())
            .perform(typeText(phoneNumber))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.profile_save_btn))
            .perform(scrollTo())
            .perform(click())

        onView(withText("OK"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

    }
}