package com.example.notfallapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.alarm.TimerHandler
import com.example.notfallapp.login.LoginActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SosTest {

    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun setup(){
        Thread.sleep(3000)
    }

    @Test
    fun clickSosAndCancelSosButtonTest(){
        onView(ViewMatchers.withId(R.id.btn_sos))
            .perform(click())
        onView(ViewMatchers.withId(R.id.tvAlarm))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvBattery))
            .check(ViewAssertions.matches(withText(R.string.notConnected)))

        onView(ViewMatchers.withId(R.id.btn_cancel_alarm))
            .perform(click())
        onView(ViewMatchers.withId(R.id.tvCanceledAlarm))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickSosAndSuccessfulSosButtonTest(){
        onView(ViewMatchers.withId(R.id.btn_sos))
            .perform(click())
        onView(ViewMatchers.withId(R.id.tvAlarm))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tvBattery))
            .check(ViewAssertions.matches(withText(R.string.notConnected)))

        Thread.sleep(TimerHandler.timerAfterSosWillSend + 500)

        onView(ViewMatchers.withId(R.id.tvAlarm))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.btn_alarm_succesful_ok))
            .perform(click())
        onView(ViewMatchers.withId(R.id.tvStatusbracelet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}