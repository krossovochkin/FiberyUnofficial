package com.krossovochkin.fiberyunofficial.pageobjects

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher

object AppListScreen {

    val testAppItem: Matcher<View> = withText("Test App")
}
