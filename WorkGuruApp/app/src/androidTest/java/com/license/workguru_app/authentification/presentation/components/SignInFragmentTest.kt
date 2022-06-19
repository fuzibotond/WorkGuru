package com.license.workguru_app.authentification.presentation.components

import android.view.Gravity
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.license.workguru_app.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInFragmentTest{
    lateinit var scenario: FragmentScenario<SignInFragment>

    @Before
    fun setup(){
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_WorkGuruApp)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun testEmailAndPasswordInput(){
        val email = "bob123@gmail.com"
        val password = "password"

        onView(withId(R.id.email_address_input))
            .perform(typeText(email))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.password_input))
            .perform(scrollTo())
            .perform(replaceText(password))

        Espresso.closeSoftKeyboard()

//        onView(withId(R.id.sign_in_btn))
//            .perform(click())

    }

    @Test
    fun testLoginWithGoogle(){
        val email = "bob123@gmail.com"
        val password = "password"

        onView(withId(R.id.email_address_input))
            .perform(typeText(email))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.password_input))
            .perform(scrollTo())
            .perform(replaceText(password))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.sign_in_button))
            .perform(scrollTo())
            .perform(click())

        pressBack()
    }
    @Test
    fun testNavigateToSignUpPage(){

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        onView(withId(R.id.linearLayout))
            .check(matches(isDisplayed()))

        scenario.onFragment { fragment ->

            navController.setGraph(R.navigation.nav_graph)

            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.sign_up))
            .check(matches(isDisplayed()))
            .perform(click())

    }
}