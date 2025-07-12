package com.krossovochkin.fiberyunofficial

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.krossovochkin.fiberyunofficial.idlingresource.OkHttpIdlingResource
import com.krossovochkin.fiberyunofficial.pageobjects.AppListScreen
import com.krossovochkin.fiberyunofficial.pageobjects.EntityDetailsScreen
import com.krossovochkin.fiberyunofficial.pageobjects.EntityListScreen
import com.krossovochkin.fiberyunofficial.pageobjects.EntityTypeListScreen
import com.krossovochkin.fiberyunofficial.pageobjects.LoginScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AppTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private val okHttpIdlingResource: IdlingResource by lazy {
        OkHttpIdlingResource(okHttpClient.dispatcher)
    }

    @Before
    fun setUp() {
        hiltRule.inject()

        IdlingRegistry.getInstance().register(okHttpIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttpIdlingResource)
    }

    @Test
    fun goldenPathTest() {
        val accountName = "krossovochkin"
        val token = System.getenv()["FIBERY_API_TOKEN"]!!

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
