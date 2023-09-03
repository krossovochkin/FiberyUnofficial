package com.krossovochkin.fiberyunofficial

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.krossovochkin.fiberyunofficial.di.TestApp
import com.krossovochkin.fiberyunofficial.pageobjects.AppListScreen
import com.krossovochkin.fiberyunofficial.pageobjects.EntityDetailsScreen
import com.krossovochkin.fiberyunofficial.pageobjects.EntityListScreen
import com.krossovochkin.fiberyunofficial.pageobjects.EntityTypeListScreen
import com.krossovochkin.fiberyunofficial.pageobjects.LoginScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun goldenPathTest() {
        val accountName = "krossovochkin"
        val token = System.getenv()["FIBERY_API_TOKEN"]!!

        IdlingRegistry.getInstance().register(
            (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp)
                .applicationComponent.okHttpIdlingResource()
        )

        // login
        onView(LoginScreen.accountTextField).perform(replaceText(accountName))
        onView(LoginScreen.tokenTextField).perform(replaceText(token))
        closeSoftKeyboard()
        onView(LoginScreen.signInButton).perform(click())

        // open test app
        onView(AppListScreen.testAppItem).perform(click())

        // open entity type
        onView(EntityTypeListScreen.parentItem).perform(click())

        // open entity
        onView(EntityListScreen.fullParentItem).perform(click())

        // verify entity details displayed
        onView(EntityDetailsScreen.title).check(matches(isDisplayed()))
    }
}
