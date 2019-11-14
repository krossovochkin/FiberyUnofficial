package by.krossovochkin.fiberyunofficial.core.presentation

import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.utils.presentation.ColorUtils

abstract class BaseFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    protected fun initToolbar(
        toolbar: Toolbar,
        title: String,
        @ColorInt bgColor: Int
    ) {
        activity!!.window?.statusBarColor =
            ColorUtils.getDarkenColor(bgColor)
        toolbar.title = title
        toolbar.setTitleTextColor(
            ContextCompat.getColor(
                context!!,
                if (ColorUtils.isDarkColor(bgColor)) {
                    android.R.color.white
                } else {
                    android.R.color.black
                }
            )
        )
        toolbar.setBackgroundColor(bgColor)
    }
}