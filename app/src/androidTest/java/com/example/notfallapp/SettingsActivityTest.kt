package com.example.notfallapp

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.login.LoginActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsActivityTest {

    private lateinit var testContext: Context

    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun setup(){
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
        //activityRule.launchActivity(Intent(testContext, SettingsActivity::class.java))
        /*Espresso.onView(ViewMatchers.withId(R.id.input_username))
            .perform(ViewActions.typeText("sosapp"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.input_password))
            .perform(ViewActions.typeText("gTN52PoeUQ"), ViewActions.closeSoftKeyboard())*/
        /*Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .perform(click())*/
        Thread.sleep(2000)

        Espresso.onView(ViewMatchers.withId(R.id.btnSettings))
            .perform(click())
    }

    /*@Test
    fun activateSettingBackgroundStartTest(){
        activityRule.activity.supportFragmentManager.beginTransaction().add(R.id.settings,
            SettingsActivity.SettingsFragment()
        ).commit()

        Espresso.onView(ViewMatchers.withId(R.id.settings)).perform(click())
    }*/

}