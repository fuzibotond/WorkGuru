package com.license.workguru_app


import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Root
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
class AuthorizedActivityTest{

    @Test
    fun testTimerStartingAndShowToastMessage() {
        launchActivity<AuthorizedActivity>().use {
            onView(withId(R.id.timer_launcher_float_button))
                .perform(click())
            onView(withId(R.id.project_spinner))
                .check(matches(isDisplayed()))
                .perform(typeText("Laravel(Laravel)"))
            Espresso.closeSoftKeyboard()

            onView(withId(R.id.skill_spinner))
                .check(matches(isDisplayed()))
                .perform(scrollTo())
                .perform(typeText("PHP"))
            Espresso.closeSoftKeyboard()

            onView(withId(R.id.description_text_input))
                .check(matches(isDisplayed()))
                .perform(scrollTo())
                .perform(typeText("Testing with Espresso not depresso"))
            Espresso.closeSoftKeyboard()

            onView(withId(R.id.start_tracking_btn))
                .check(matches(isDisplayed()))
                .perform(scrollTo())
                .perform(click())
            onView(withText(R.string.please_do_not_let_any_field_empty))
                .inRoot(ToastMatcher())
                .check(matches(isDisplayed()))

        }
    }

    @Test
    fun testCreatingNewCategoryReturnsTrue(){
        launchActivity<AuthorizedActivity>().use {
            onView(withId(R.id.timer_launcher_float_button))
                .perform(click())
            onView(withId(R.id.create_new_project_btn))
                .check(matches(isDisplayed()))
                .perform(click())
            onView(withId(R.id.create_new_category_btn))
                .check(matches(isDisplayed()))
                .perform(click())
            onView(withId(R.id.description_text_input_layout))
                .check(matches(isDisplayed()))
            onView(withId(R.id.category_name_text_input))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(typeText("Brand new test category1"))
            onView(withId(R.id.save_category_btn))
                .check(matches(isDisplayed()))
                .perform(click())
            onView(withText(R.string.tNewCategoryCreated))
                .inRoot(ToastMatcher())
                .check(matches(isDisplayed()))
        }
    }
    @Test
    fun testCreatingNewCategoryReturnsFalse(){
        launchActivity<AuthorizedActivity>().use {
            onView(withId(R.id.timer_launcher_float_button))
                .perform(click())
            onView(withId(R.id.create_new_project_btn))
                .check(matches(isDisplayed()))
                .perform(click())
            onView(withId(R.id.create_new_category_btn))
                .check(matches(isDisplayed()))
                .perform(click())
            onView(withId(R.id.description_text_input_layout))
                .check(matches(isDisplayed()))
            onView(withId(R.id.category_name_text_input))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(typeText("Brand new test category1"))
            onView(withId(R.id.save_category_btn))
                .check(matches(isDisplayed()))
                .perform(click())
            onView(withText(R.string.category_name_already_exist))
                .inRoot(ToastMatcher())
                .check(matches(isDisplayed()))
        }
    }
}

fun hasTextInputLayoutHintText(expectedErrorText: String): Matcher<View> = object : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description?) { }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextInputLayout) return false
        val error = item.hint ?: return false
        val hint = error.toString()
        return expectedErrorText == hint
    }
}

class ToastMatcher : TypeSafeMatcher<Root?>() {
    override fun describeTo(description: Description) {
        description.appendText("is toast")
    }

    override fun matchesSafely(root: Root?): Boolean {
        val type: Int = root!!.getWindowLayoutParams().get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken: IBinder = root.getDecorView().getWindowToken()
            val appToken: IBinder = root.getDecorView().getApplicationWindowToken()
            if (windowToken === appToken) {
                //means this window isn't contained by any other windows.
                return true
            }
        }
        return false
    }


}

