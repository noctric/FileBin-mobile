package de.michael.filebinmobile

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileInputStream
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private lateinit var properties: Properties

    @Before
    fun setup() {
        val systemResourceAsStream = FileInputStream("test-server.properties")
        properties = Properties()
        properties.load(systemResourceAsStream)
    }

    @Test
    fun userAddsServerAndUploadsText() {

        onView(withText("Welcome!")).check(matches(isDisplayed()))

        onView(withText("Yes, take me there!")).perform(click())

        onView(withId(R.id.serverSettingsFragmentRootId))
            .check(matches(isDisplayed()))

        onView(withId(R.id.fbaAddServer))
            .perform(click())

        onView(withId(R.id.cslServerInfo))
            .check(matches(isDisplayed()))

        onView(withId(R.id.edtEditName))
            .perform(typeText("test server name"))

        onView(withId(R.id.edtEditAddress))
            .perform(typeText(properties.getProperty("user.name")))

        onView(withId(R.id.edtEditAddress))
            .perform(typeText(properties.getProperty("user.password")))

        onView(withId(R.id.edtEditAddress))
            .perform(typeText(properties.getProperty("server.address")))

        onView(withText("OK"))
            .perform(click())

    }
}
