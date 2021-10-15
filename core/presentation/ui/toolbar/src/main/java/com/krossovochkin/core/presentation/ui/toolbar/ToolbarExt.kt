package com.krossovochkin.core.presentation.ui.toolbar

import android.content.res.ColorStateList
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.children
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.krossovochkin.core.presentation.color.ColorUtils
import com.krossovochkin.core.presentation.flow.collect
import com.krossovochkin.core.presentation.system.setupSystemBars
import com.krossovochkin.core.presentation.system.updateInsetMargins
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

const val SEARCH_QUERY_DEBOUNCE_MILLIS = 300L

inline fun Fragment.initToolbar(
    toolbar: Toolbar,
    toolbarData: Flow<ToolbarViewState>,
    crossinline onBackPressed: () -> Unit = {},
    crossinline onMenuItemClicked: (MenuItem) -> Boolean = { false },
    crossinline onSearchQueryChanged: (String) -> Unit = {},
    crossinline onToolbarUpdated: () -> Unit = {}
) {
    toolbarData.collect(this) {
        toolbar.initToolbar(
            activity = requireActivity(),
            state = it,
            onBackPressed = onBackPressed,
            onMenuItemClicked = onMenuItemClicked,
            onSearchQueryChanged = onSearchQueryChanged
        )
        onToolbarUpdated()
    }
}

@Suppress("LongParameterList")
@PublishedApi
internal inline fun Toolbar.initToolbar(
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
