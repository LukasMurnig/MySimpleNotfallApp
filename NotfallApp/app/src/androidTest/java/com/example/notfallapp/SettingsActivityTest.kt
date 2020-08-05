package com.example.notfallapp

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.menubar.settings.SettingsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsActivityTest {

    private lateinit var testContext: Context

    @get:Rule
    var activityRule: ActivityTestRule<SettingsActivity>
            = ActivityTestRule(SettingsActivity::class.java, false, false)

    @Before
    fun setup(){
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
        activityRule.launchActivity(Intent(testContext, SettingsActivity::class.java))
    }

    /*@Test
    fun activateSettingBackgroundStartTest(){
        activityRule.activity.supportFragmentManager.beginTransaction().add(R.id.settings,
            SettingsActivity.SettingsFragment()
        ).commit()

        Espresso.onView(ViewMatchers.withId(R.id.settings)).perform(click())
    }*/

    @Test
    fun clickOnLogoutButtonTest(){
        Espresso.onView(ViewMatchers.withId(R.id.btnLogOut))
            .perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}