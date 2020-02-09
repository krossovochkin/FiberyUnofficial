package by.krossovochkin.fiberyunofficial.core.presentation

import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BaseFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    protected fun initToolbar(
        toolbar: Toolbar,
        title: String,
        @ColorInt bgColorInt: Int
    ) {
        activity!!.window?.statusBarColor = ColorUtils.getDarkenColor(bgColorInt)
        toolbar.title = title
        toolbar.setTitleTextColor(
            ContextCompat.getColor(
                context!!,
                if (ColorUtils.isDarkColor(bgColorInt)) {
                    android.R.color.white
                } else {
                    android.R.color.black
                }
            )
        )
        toolbar.setBackgroundColor(bgColorInt)
    }
}
