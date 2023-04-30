package com.krossovochkin.fiberyunofficial.pageobjects

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher

object EntityDetailsScreen {

    val title: Matcher<View> = withText("Full parent")
}
