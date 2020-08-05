package com.example.notfallapp

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ButtonbarTest {

    private lateinit var testContext: Context

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun buttonBarHomeItemClickedTest(){
        onView(withId(R.id.btnHome))
            .perform(click())
        onView(withId(R.id.tvStatusbracelet))
            .check(matches(isDisplayed()))
    }

    @Test
    fun buttonBarAlarmItemClickedTest(){
        onView(withId(R.id.btnAlarms))
            .perform(click())
        onView(withId(R.id.rvAlarms))
            .check(matches(isDisplayed()))
    }

    @Test
    fun buttonBarContactItemClickedTest(){
        onView(withId(R.id.btnContact))
            .perform(click())
        onView(withId(R.id.rvContacts))
            .check(matches(isDisplayed()))
    }

    @Test
    fun buttonBarSettingsItemClickedTest(){
        onView(withId(R.id.btnSettings))
            .perform(click())
        onView(withId(R.id.tvName))
            .check(matches(isDisplayed()))
    }
}