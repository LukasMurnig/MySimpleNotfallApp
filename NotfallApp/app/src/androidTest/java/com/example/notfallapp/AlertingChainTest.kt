package com.example.notfallapp

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.adapter.AlertingChainListAdapter
import com.example.notfallapp.login.LoginActivity
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AlertingChainTest {
    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun setup(){
        Espresso.onView(ViewMatchers.withId(R.id.input_username))
            .perform(ViewActions.typeText("sosapp"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.input_password))
            .perform(ViewActions.typeText("gTN52PoeUQ"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .perform(click())
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.btnContact))
            .perform(click())
    }

    @Test
    fun deactivateAlertingChainMemberTest(){
        Espresso.onView(ViewMatchers.withId(R.id.rvContacts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlertingChainListAdapter.AlertingChainMembersViewHolder>(
                    0,
                    MyViewAction.clickChildViewWithId(R.id.iBtnContactMenu)
                )
            )

        Thread.sleep(2000)

        Espresso.onView(ViewMatchers.withText(R.string.activate_deaktivate))
            .inRoot(isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Thread.sleep(2000)

        try {
            Espresso.onView(ViewMatchers.withText(R.string.Yes))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(click())
        }catch (ex: Exception){

        }
    }

    object MyViewAction {
        fun clickChildViewWithId(id: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View>? {
                    return null
                }

                override fun getDescription(): String {
                    return "Click on a child view with specified id."
                }

                override fun perform(uiController: UiController?, view: View) {
                    val v: View = view.findViewById(id)

                    v.performClick()
                }
            }
        }
    }
}