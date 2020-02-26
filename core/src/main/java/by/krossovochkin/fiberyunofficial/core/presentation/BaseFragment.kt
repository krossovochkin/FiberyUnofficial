package by.krossovochkin.fiberyunofficial.core.presentation

import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.R

abstract class BaseFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    protected inline fun initToolbar(
        toolbar: Toolbar,
        title: String,
        @ColorInt bgColorInt: Int,
        hasBackButton: Boolean = false,
        crossinline onBackPressed: () -> Unit = {}
    ) {
        val contrastColor = ContextCompat.getColor(
            requireContext(),
            if (ColorUtils.isDarkColor(bgColorInt)) {
                android.R.color.white
            } else {
                android.R.color.black
            }
        )

        requireActivity().window.statusBarColor = ColorUtils.getDarkenColor(bgColorInt)
        toolbar.title = title
        toolbar.setTitleTextColor(contrastColor)
        toolbar.setBackgroundColor(bgColorInt)

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
    }
}
