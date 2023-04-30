package com.krossovochkin.fiberyunofficial.pageobjects

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher

object EntityTypeListScreen {

    val parentItem: Matcher<View> = withText("Parent")
}
