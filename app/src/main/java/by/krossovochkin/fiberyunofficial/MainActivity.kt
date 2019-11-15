package by.krossovochkin.fiberyunofficial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.findNavController
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.applist.AppListGlobalDependencies
import by.krossovochkin.fiberyunofficial.applist.AppListParentListener
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragmentDirections
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitylist.EntityListArgs
import by.krossovochkin.fiberyunofficial.entitylist.EntityListArgsProvider
import by.krossovochkin.fiberyunofficial.entitylist.EntityListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentListener
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentArgs
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentDirections
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListArgsProvider
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentListener
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentDirections
import dagger.BindsInstance
import dagger.Component
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Scope

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityComponent: MainActivityComponent
    private val listener: MainActivityListener = MainActivityListener()
    private val argsProvider: MainActivityArgsProvider = MainActivityArgsProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent = DaggerMainActivityComponent.builder()
            .applicationComponent((applicationContext as App).applicationComponent)
            .appListParentListener(listener)
            .entityTypeListParentListener(listener)
            .entityTypeListArgsProvider(argsProvider)
            .entityListParentListener(listener)
            .entityListArgsProvider(argsProvider)
            .build()

        supportFragmentManager.fragmentFactory =
            MainActivityFragmentFactory(mainActivityComponent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    inner class MainActivityListener :
        AppListParentListener,
        EntityTypeListParentListener,
        EntityListParentListener {
        override fun onAppSelected(fiberyAppData: FiberyAppData) {
            navHostFragment.findNavController().navigate(
                AppListFragmentDirections.actionAppListToEntityTypeList(fiberyAppData)
            )
        }

        override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema) {
            navHostFragment.findNavController().navigate(
                EntityTypeListFragmentDirections.actionEntityTypeListToEntityList(entityTypeSchema)
            )
        }

        override fun onEntitySelected(entity: FiberyEntityData) {
            navHostFragment.findNavController().navigate(
                EntityListFragmentDirections.actionEntityListToEntityDetails(entity)
            )
        }
    }

    inner class MainActivityArgsProvider :
        EntityTypeListArgsProvider,
        EntityListArgsProvider {

        override fun getEntityTypeListArgs(arguments: Bundle): EntityTypeListArgs {
            val args = EntityTypeListFragmentArgs.fromBundle(arguments)
            return EntityTypeListArgs(
                fiberyAppData = args.fiberyApp
            )
        }

        override fun getEntityListArgs(arguments: Bundle): EntityListArgs {
            val args = EntityListFragmentArgs.fromBundle(arguments)
            return EntityListArgs(
                entityTypeSchema = args.entityType
            )
        }
    }
}

@Scope
@Retention
annotation class MainActivityScope

@MainActivityScope
@Component(
    dependencies = [ApplicationComponent::class]
)
interface MainActivityComponent :
    AppListGlobalDependencies,
    EntityTypeListGlobalDependencies,
    EntityListGlobalDependencies {

    @Component.Builder
    interface Builder {

        fun applicationComponent(applicationComponent: ApplicationComponent): Builder

        @BindsInstance
        fun appListParentListener(appListParentListener: AppListParentListener): Builder

        @BindsInstance
        fun entityTypeListParentListener(entityTypeListParentListener: EntityTypeListParentListener): Builder

        @BindsInstance
        fun entityTypeListArgsProvider(entityTypeListArgsProvider: EntityTypeListArgsProvider): Builder

        @BindsInstance
        fun entityListParentListener(entityListParentListener: EntityListParentListener): Builder

        @BindsInstance
        fun entityListArgsProvider(entityListArgsProvider: EntityListArgsProvider): Builder

        fun build(): MainActivityComponent
    }
}

private class MainActivityFragmentFactory(
    private val mainActivityComponent: MainActivityComponent
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            AppListFragment::class.java.canonicalName -> AppListFragment(mainActivityComponent)
            EntityTypeListFragment::class.java.canonicalName -> EntityTypeListFragment(
                mainActivityComponent
            )
            EntityListFragment::class.java.canonicalName -> EntityListFragment(mainActivityComponent)
            else -> return super.instantiate(classLoader, className)
        }
    }
}
