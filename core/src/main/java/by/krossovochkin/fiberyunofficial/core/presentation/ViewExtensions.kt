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

import android.content.res.ColorStateList
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.iterator
import androidx.fragment.app.FragmentActivity
import by.krossovochkin.fiberyunofficial.core.R

@Suppress("LongParameterList")
inline fun Toolbar.initToolbar(
    activity: FragmentActivity,
    state: ToolbarViewState,
    crossinline onBackPressed: () -> Unit = {},
    crossinline onMenuItemClicked: (MenuItem) -> Boolean = { false }
) {
    val backgroundColor = ColorUtils.getDesaturatedColorIfNeeded(activity, state.bgColorInt)
    val contrastColor = ColorUtils.getContrastColor(activity, backgroundColor)

    activity.window.statusBarColor = ColorUtils.getDarkenColor(backgroundColor)
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
    }
}
