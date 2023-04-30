package com.krossovochkin.fiberyunofficial.pageobjects

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher

object LoginScreen {

    val accountTextField: Matcher<View> =
        withId(com.krossovochkin.fiberyunofficial.login.R.id.accountEditText)
    val tokenTextField: Matcher<View> =
        withId(com.krossovochkin.fiberyunofficial.login.R.id.tokenEditText)
    val signInButton: Matcher<View> =
        withId(com.krossovochkin.fiberyunofficial.login.R.id.loginButton)
}
