package com.example.scoreapp
import android.view.View
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private var myScoreButton = 0
    private var myStealButton = 0
    private var myResetButton = 0
    private var myScoreTextField = 0

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initValidString() {
        // Please set your id names here.
        myScoreButton = R.id.score
        myStealButton = R.id.steal
        myResetButton = R.id.reset
        myScoreTextField = R.id.tv_score
    }

    @Test
    fun clickScoreButton3Times() {
        val scoreButton = onView(withId(myScoreButton))

        // Click the score button 3 times
        for (i in 1..3) {
            scoreButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("3"))) // Assert score is 3
    }

    @Test
    fun clickScoreAndStealButtons() {
        val scoreButton = onView(withId(myScoreButton))
        val stealButton = onView(withId(myStealButton))

        // Click score 3 times and steal once
        for (i in 1..3) {
            scoreButton.perform(click())
        }

        stealButton.perform(click())

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("2"))) // Assert score is 2
    }

    @Test
    fun testLowerLimitsOfScore() {
        val scoreButton = onView(withId(myScoreButton))
        val stealButton = onView(withId(myStealButton))

        // Click score 3 times and steal 5 times
        for (i in 1..3) {
            scoreButton.perform(click())
        }

        for (i in 1..5) {
            stealButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("0"))) // Assert score is 0
    }

    @Test
    fun testUpperLimitsOfScore() {
        val scoreButton = onView(withId(myScoreButton))

        // Click score 15 times, then 2 more times
        for (i in 1..15) {
            scoreButton.perform(click())
        }

        for (i in 1..2) {
            scoreButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("15"))) // Assert score is capped at 15
    }

    @Test
    fun testResetButton() {
        val scoreButton = onView(withId(myScoreButton))
        val resetButton = onView(withId(myResetButton))

        // Click score 3 times and reset
        for (i in 1..3) {
            scoreButton.perform(click())
        }

        resetButton.perform(click())

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("0"))) // Assert score is reset to 0
    }

    @Test
    fun testScoreOnRotation() {
        val scoreButton = onView(withId(myScoreButton))

        // Click score 3 times
        for (i in 1..3) {
            scoreButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("3"))) // Assert score is 3

        // Rotate device
        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(isRoot()).perform(waitFor(500)) // Wait for UI to stabilize

        textView.check(matches(withText("3"))) // Assert score remains 3
    }

    @Test
    fun testScoreOnRotationWithClick() {
        val scoreButton = onView(withId(myScoreButton))

        // Click score 3 times
        for (i in 1..3) {
            scoreButton.perform(click())
        }

        val textView = onView(withId(myScoreTextField))
        textView.check(matches(withText("3"))) // Assert score is 3

        // Rotate device
        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(isRoot()).perform(waitFor(500)) // Wait for UI to stabilize

        // Click score button again
        scoreButton.perform(click())

        textView.check(matches(withText("4"))) // Assert score is 4
    }

    // Utility function to add a wait period
    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "Wait for $delay milliseconds."
            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}
