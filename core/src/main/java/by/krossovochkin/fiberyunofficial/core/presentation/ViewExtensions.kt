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

import android.content.Context
import android.content.res.ColorStateList
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.children
import androidx.core.view.iterator
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
    val backgroundColor = ColorUtils.getDesaturatedColorIfNeeded(activity, state.bgColorInt)
    val contrastColor = ColorUtils.getContrastColor(backgroundColor)

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

        state.searchActionItemId?.let { searchActionItemId ->
            val searchView =
                this.menu.children.find { it.itemId == searchActionItemId }?.actionView as SearchView
            val debouncer = Debouncer(SEARCH_QUERY_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS) { text ->
                onSearchQueryChanged(text)
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    debouncer.process(newText.orEmpty())
                    return true
                }
            })
        }
    }
}
