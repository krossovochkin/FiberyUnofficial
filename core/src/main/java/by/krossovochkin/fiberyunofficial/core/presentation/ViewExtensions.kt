/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package by.krossovochkin.fiberyunofficial.core.presentation

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.core.view.iterator
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import by.krossovochkin.fiberyunofficial.core.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit

const val SEARCH_QUERY_DEBOUNCE_MILLIS = 300L

inline fun FloatingActionButton.initFab(
    context: Context,
    state: FabViewState,
    crossinline onClick: () -> Unit
) {
    this.backgroundTintList = ColorStateList.valueOf(
        ColorUtils.getDesaturatedColorIfNeeded(context, state.bgColorInt)
    )
    this.imageTintList = ColorStateList.valueOf(ColorUtils.getContrastColor(state.bgColorInt))

    this.setOnClickListener { onClick() }
}

@Suppress("LongParameterList")
inline fun Toolbar.initToolbar(
    activity: FragmentActivity,
    state: ToolbarViewState,
    crossinline onBackPressed: () -> Unit = {},
    crossinline onMenuItemClicked: (MenuItem) -> Boolean = { false },
    crossinline onSearchQueryChanged: (String) -> Unit = {}
) {
    updateInsetMargins(activity, top = true)

    val backgroundColor = ColorUtils.getDesaturatedColorIfNeeded(activity, state.bgColorInt)
    val contrastColor = ColorUtils.getContrastColor(backgroundColor)

    activity.setupSystemBars(
        backgroundColor = backgroundColor,
        contrastColor = contrastColor
    )

    this.title = state.title
    this.setTitleTextColor(contrastColor)
    this.setBackgroundColor(backgroundColor)

    if (state.hasBackButton) {
        this.navigationIcon = ContextCompat
            .getDrawable(
                activity,
                R.drawable.ic_arrow_back_white_24dp
            )
            ?.mutate()
            ?.apply { setTint(contrastColor) }
        this.setNavigationOnClickListener { onBackPressed() }
    }

    state.menuResId?.let { menuResId ->
        activity.menuInflater
            .inflate(menuResId, this.menu)
        this.setOnMenuItemClickListener { item -> onMenuItemClicked(item) }
        this.menu.iterator().forEach { item ->
            MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(contrastColor))
        }

        state.searchActionItemId?.let { searchActionItemId ->
            val searchView =
                this.menu.children.find { it.itemId == searchActionItemId }?.actionView as SearchView
            val debouncer = Debouncer(SEARCH_QUERY_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS) { text ->
                onSearchQueryChanged(text)
            }
            searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false

                    override fun onQueryTextChange(newText: String?): Boolean {
                        debouncer.process(newText.orEmpty())
                        return true
                    }
                }
            )
        }
    }
}

@Suppress("DEPRECATION")
@PublishedApi
internal fun Activity.setupSystemBars(
    @ColorInt
    backgroundColor: Int,
    @ColorInt
    contrastColor: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
    } else {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    this.window.navigationBarColor = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Color.TRANSPARENT
        ColorUtils.isDarkMode(this) || Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
            Color.parseColor("#B3000000")
        }
        else -> Color.parseColor("#B3FFFFFF")
    }

    if (ColorUtils.isDarkMode(this)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_NAVIGATION_BARS,
                    APPEARANCE_LIGHT_NAVIGATION_BARS
                )
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    this.window.statusBarColor = ColorUtils.getDarkenColor(backgroundColor)

    if (contrastColor == Color.WHITE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
        }
    }
}

fun View.updateInsetMargins(
    activity: Activity,
    top: Boolean = false,
    bottom: Boolean = false
) {
    doOnPreDraw {
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
            ?.getInsets(WindowInsetsCompat.Type.systemBars())
        this.updateLayoutParams<ConstraintLayout.LayoutParams> {
            if (top) {
                updateMargins(top = insets?.top ?: 0)
            }
            if (bottom) {
                updateMargins(bottom = insets?.bottom ?: 0)
            }
        }
    }
}

fun View.updateInsetPaddings(
    bottom: Boolean = false
) {
    val originalPaddingBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        if (bottom) {
            updatePadding(bottom = originalPaddingBottom + systemInsets.bottom)
        }

        insets
    }
    requestApplyInsets()
}
