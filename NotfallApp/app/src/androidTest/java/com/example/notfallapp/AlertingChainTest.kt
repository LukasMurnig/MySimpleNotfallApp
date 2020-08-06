package com.example.notfallapp

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.notfallapp.adapter.AlertingChainListAdapter
import com.example.notfallapp.menubar.contact.ContactActivity
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class AlertingChainTest {
    @get:Rule
    var activityRule: ActivityTestRule<ContactActivity>
            = ActivityTestRule(ContactActivity::class.java)

    @Test
    fun deactivateAlertingChainMemberTest(){
        Espresso.onView(ViewMatchers.withId(R.id.rvContacts))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<AlertingChainListAdapter.AlertingChainMembersViewHolder>(
                    0,
                    MyViewAction.clickChildViewWithId(R.id.iBtnContactMenu)
                )
            )
        Espresso.onView(ViewMatchers.withText(R.string.activate_deaktivate))
            .inRoot(isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.Yes))
            .inRoot(isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())
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