package by.krossovochkin.fiberyunofficial.core.presentation

import android.content.res.ColorStateList
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.R

abstract class BaseFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    @Suppress("LongParameterList")
    protected inline fun initToolbar(
        toolbar: Toolbar,
        title: String,
        @ColorInt bgColorInt: Int,
        hasBackButton: Boolean = false,
        crossinline onBackPressed: () -> Unit = {},
        @MenuRes
        menuResId: Int? = null,
        crossinline onMenuItemClicked: (MenuItem) -> Boolean = { false }
    ) {
        val backgroundColor = ColorUtils.getDesaturatedColorIfNeeded(requireContext(), bgColorInt)
        val contrastColor = ColorUtils.getContrastColor(requireContext(), backgroundColor)

        requireActivity().window.statusBarColor = ColorUtils.getDarkenColor(backgroundColor)
        toolbar.title = title
        toolbar.setTitleTextColor(contrastColor)
        toolbar.setBackgroundColor(backgroundColor)

        if (hasBackButton) {
            toolbar.navigationIcon = ContextCompat
                .getDrawable(
                    requireContext(),
                    R.drawable.ic_arrow_back_white_24dp
                )
                ?.mutate()
                ?.apply { setTint(contrastColor) }
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }

        if (menuResId != null) {
            requireActivity().menuInflater
                .inflate(menuResId, toolbar.menu)
            toolbar.setOnMenuItemClickListener { item -> onMenuItemClicked(item) }
            toolbar.menu.iterator().forEach { item ->
                MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(contrastColor))
            }
        }
    }
}
