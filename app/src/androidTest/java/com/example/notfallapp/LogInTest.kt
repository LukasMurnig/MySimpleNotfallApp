package com.example.notfallapp

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.login.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LogInTest {
    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun LoginShouldFailTest(){
        Espresso.onView(ViewMatchers.withId(R.id.input_username))
            .perform(typeText("not in the system"), closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.input_password))
            .perform(typeText("not in the system"), closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.input_username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}