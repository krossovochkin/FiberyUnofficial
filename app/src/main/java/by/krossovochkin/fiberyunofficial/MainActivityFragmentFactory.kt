package by.krossovochkin.fiberyunofficial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.entitycreate.presentation.EntityCreateFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitypicker.presentation.EntityPickerFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragment

class MainActivityFragmentFactory(
    private val mainActivityComponent: MainActivityComponent
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            LoginFragment::class.java.canonicalName -> LoginFragment(mainActivityComponent)
            AppListFragment::class.java.canonicalName -> AppListFragment(mainActivityComponent)
            EntityTypeListFragment::class.java.canonicalName -> EntityTypeListFragment(
                mainActivityComponent
            )
            EntityListFragment::class.java.canonicalName -> EntityListFragment(mainActivityComponent)
            EntityDetailsFragment::class.java.canonicalName -> EntityDetailsFragment(
                mainActivityComponent
            )
            EntityCreateFragment::class.java.canonicalName -> EntityCreateFragment(
                mainActivityComponent
            )
            EntityPickerFragment::class.java.canonicalName -> EntityPickerFragment(
                mainActivityComponent
            )
            else -> return super.instantiate(classLoader, className)
        }
    }
}
