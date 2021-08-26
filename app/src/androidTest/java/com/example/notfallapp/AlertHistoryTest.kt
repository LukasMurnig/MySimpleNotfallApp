package com.example.notfallapp

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.adapter.AlertsListAdapter
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.menubar.alert.AlarmsActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AlertHistoryTest {
    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun clickOnAlertTest(){
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.btnAlarms))
            .perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.rvAlarms))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(3000)

        Espresso.onView(ViewMatchers.withId(R.id.rvAlarms))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlertsListAdapter.AlertsViewHolder>(
                    0, click()
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.tvDetailDeviceId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}