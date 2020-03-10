package by.krossovochkin.fiberyunofficial.core.presentation

import android.content.res.ColorStateList
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.iterator
import androidx.fragment.app.FragmentActivity
import by.krossovochkin.fiberyunofficial.core.R

@Suppress("LongParameterList")
inline fun Toolbar.initToolbar(
    activity: FragmentActivity,
    title: String,
    @ColorInt bgColorInt: Int,
    hasBackButton: Boolean = false,
    crossinline onBackPressed: () -> Unit = {},
    @MenuRes
    menuResId: Int? = null,
    crossinline onMenuItemClicked: (MenuItem) -> Boolean = { false }
) {
    val backgroundColor = ColorUtils.getDesaturatedColorIfNeeded(activity, bgColorInt)
    val contrastColor = ColorUtils.getContrastColor(activity, backgroundColor)

    activity.window.statusBarColor = ColorUtils.getDarkenColor(backgroundColor)
    this.title = title
    this.setTitleTextColor(contrastColor)
    this.setBackgroundColor(backgroundColor)

    if (hasBackButton) {
        this.navigationIcon = ContextCompat
            .getDrawable(
                activity,
                R.drawable.ic_arrow_back_white_24dp
            )
            ?.mutate()
            ?.apply { setTint(contrastColor) }
        this.setNavigationOnClickListener { onBackPressed() }
    }

    if (menuResId != null) {
        activity.menuInflater
            .inflate(menuResId, this.menu)
        this.setOnMenuItemClickListener { item -> onMenuItemClicked(item) }
        this.menu.iterator().forEach { item ->
            MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(contrastColor))
        }
    }
}
