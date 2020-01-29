package by.krossovochkin.fiberyunofficial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.findNavController
import by.krossovochkin.fiberyunofficial.applist.AppListParentComponent
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragment
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListFragmentDirections
import by.krossovochkin.fiberyunofficial.applist.presentation.AppListViewModel
import by.krossovochkin.fiberyunofficial.core.domain.FiberyAppData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityTypeSchema
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentArgs
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsViewModel
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentArgs
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragmentDirections
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListViewModel
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentArgs
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragmentDirections
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListViewModel
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
            .entityDetailsParentListener(listener)
            .entityDetailsArgsProvider(argsProvider)
            .build()

        supportFragmentManager.fragmentFactory =
            MainActivityFragmentFactory(mainActivityComponent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    inner class MainActivityListener :
        AppListViewModel.ParentListener,
        EntityTypeListViewModel.ParentListener,
        EntityListViewModel.ParentListener,
        EntityDetailsViewModel.ParentListener {

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
        EntityTypeListFragment.ArgsProvider,
        EntityListFragment.ArgsProvider,
        EntityDetailsFragment.ArgsProvider {

        override fun getEntityTypeListArgs(arguments: Bundle): EntityTypeListFragment.Args {
            val args = EntityTypeListFragmentArgs.fromBundle(arguments)
            return EntityTypeListFragment.Args(
                fiberyAppData = args.fiberyApp
            )
        }

        override fun getEntityListArgs(arguments: Bundle): EntityListFragment.Args {
            val args = EntityListFragmentArgs.fromBundle(arguments)
            return EntityListFragment.Args(
                entityTypeSchema = args.entityType
            )
        }

        override fun getEntityDetailsArgs(arguments: Bundle): EntityDetailsFragment.Args {
            val args = EntityDetailsFragmentArgs.fromBundle(arguments)
            return EntityDetailsFragment.Args(
                entityData = args.entity
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
    AppListParentComponent,
    EntityTypeListParentComponent,
    EntityListParentComponent,
    EntityDetailsParentComponent {

    @Component.Builder
    interface Builder {

        fun applicationComponent(applicationComponent: ApplicationComponent): Builder

        @BindsInstance
        fun appListParentListener(appListParentListener: AppListViewModel.ParentListener): Builder

        @BindsInstance
        fun entityTypeListParentListener(entityTypeListParentListener: EntityTypeListViewModel.ParentListener): Builder

        @BindsInstance
        fun entityTypeListArgsProvider(entityTypeListArgsProvider: EntityTypeListFragment.ArgsProvider): Builder

        @BindsInstance
        fun entityListParentListener(entityListParentListener: EntityListViewModel.ParentListener): Builder

        @BindsInstance
        fun entityListArgsProvider(entityListArgsProvider: EntityListFragment.ArgsProvider): Builder

        @BindsInstance
        fun entityDetailsParentListener(entityDetailsParentListener: EntityDetailsViewModel.ParentListener): Builder

        @BindsInstance
        fun entityDetailsArgsProvider(entityDetailsArgsProvider: EntityDetailsFragment.ArgsProvider): Builder

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
            EntityDetailsFragment::class.java.canonicalName -> EntityDetailsFragment(
                mainActivityComponent
            )
            else -> return super.instantiate(classLoader, className)
        }
    }
}
