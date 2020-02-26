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
import by.krossovochkin.fiberyunofficial.core.domain.FiberyFieldSchema
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.databinding.ActivityMainBinding
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentArgs
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragmentDirections
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
import by.krossovochkin.fiberyunofficial.login.LoginParentComponent
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragment
import by.krossovochkin.fiberyunofficial.login.presentation.LoginFragmentDirections
import by.krossovochkin.fiberyunofficial.login.presentation.LoginViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityComponent: MainActivityComponent
    private val listener: MainActivityListener = MainActivityListener()
    private val argsProvider: MainActivityArgsProvider = MainActivityArgsProvider()

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent = DaggerMainActivityComponent.builder()
            .applicationComponent((applicationContext as App).applicationComponent)
            .mainActivityListener(listener)
            .mainActivityArgsProvider(argsProvider)
            .build()

        supportFragmentManager.fragmentFactory =
            MainActivityFragmentFactory(mainActivityComponent)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    inner class MainActivityListener :
        LoginViewModel.ParentListener,
        AppListViewModel.ParentListener,
        EntityTypeListViewModel.ParentListener,
        EntityListViewModel.ParentListener,
        EntityDetailsViewModel.ParentListener {

        override fun onAppSelected(fiberyAppData: FiberyAppData) {
            binding.navHostFragment.findNavController().navigate(
                AppListFragmentDirections.actionAppListToEntityTypeList(fiberyAppData)
            )
        }

        override fun onEntityTypeSelected(entityTypeSchema: FiberyEntityTypeSchema) {
            val navController = binding.navHostFragment.findNavController()
            val directions = when (val id = navController.currentDestination?.id) {
                R.id.entityTypeList -> {
                    EntityTypeListFragmentDirections.actionEntityTypeListToEntityList(
                        entityType = entityTypeSchema,
                        entity = null,
                        field = null
                    )
                }
                else -> throw IllegalStateException("Unknown current direction: $id")
            }
            navController.navigate(directions)
        }

        override fun onEntityTypeSelected(
            entityTypeSchema: FiberyEntityTypeSchema,
            entity: FiberyEntityData,
            fieldSchema: FiberyFieldSchema
        ) {
            val navController = binding.navHostFragment.findNavController()
            val directions = when (val id = navController.currentDestination?.id) {
                R.id.entityDetails -> {
                    EntityDetailsFragmentDirections.actionEntityDetailsToEntityList(
                        entityType = entityTypeSchema,
                        entity = entity,
                        field = fieldSchema
                    )
                }
                else -> throw IllegalStateException("Unknown current direction: $id")
            }
            navController.navigate(directions)
        }

        override fun onEntitySelected(entity: FiberyEntityData) {
            val navController = binding.navHostFragment.findNavController()
            val directions = when (val id = navController.currentDestination?.id) {
                R.id.entityList -> {
                    EntityListFragmentDirections.actionEntityListToEntityDetails(entity)
                }
                R.id.entityDetails -> {
                    EntityDetailsFragmentDirections.actionEntityDetailsSelf(entity)
                }
                else -> throw IllegalStateException("Unknown current direction: $id")
            }
            navController.navigate(directions)
        }

        override fun onLoginSuccess() {
            binding.navHostFragment.findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToAppList())
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
                entityTypeSchema = args.entityType,
                entityParams = if (args.field != null && args.entity != null) {
                    args.field to args.entity
                } else {
                    null
                }
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
    modules = [MainActivityModule::class],
    dependencies = [ApplicationComponent::class]
)
interface MainActivityComponent :
    LoginParentComponent,
    AppListParentComponent,
    EntityTypeListParentComponent,
    EntityListParentComponent,
    EntityDetailsParentComponent {

    @Component.Builder
    interface Builder {

        fun applicationComponent(applicationComponent: ApplicationComponent): Builder

        @BindsInstance
        fun mainActivityListener(
            mainActivityListener: MainActivity.MainActivityListener
        ): Builder

        @BindsInstance
        fun mainActivityArgsProvider(
            mainActivityArgsProvider: MainActivity.MainActivityArgsProvider
        ): Builder

        fun build(): MainActivityComponent
    }
}

@Module
abstract class MainActivityModule {

    @MainActivityScope
    @Binds
    abstract fun loginParentListener(
        mainActivityListener: MainActivity.MainActivityListener
    ): LoginViewModel.ParentListener

    @MainActivityScope
    @Binds
    abstract fun appListParentListener(
        mainActivityListener: MainActivity.MainActivityListener
    ): AppListViewModel.ParentListener

    @MainActivityScope
    @Binds
    abstract fun entityTypeListParentListener(
        mainActivityListener: MainActivity.MainActivityListener
    ): EntityTypeListViewModel.ParentListener

    @MainActivityScope
    @Binds
    abstract fun entityTypeListArgsProvider(
        mainActivityArgsProvider: MainActivity.MainActivityArgsProvider
    ): EntityTypeListFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityListParentListener(
        mainActivityListener: MainActivity.MainActivityListener
    ): EntityListViewModel.ParentListener

    @MainActivityScope
    @Binds
    abstract fun entityListArgsProvider(
        mainActivityArgsProvider: MainActivity.MainActivityArgsProvider
    ): EntityListFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityDetailsParentListener(
        mainActivityListener: MainActivity.MainActivityListener
    ): EntityDetailsViewModel.ParentListener

    @MainActivityScope
    @Binds
    abstract fun entityDetailsArgsProvider(
        mainActivityArgsProvider: MainActivity.MainActivityArgsProvider
    ): EntityDetailsFragment.ArgsProvider

}

private class MainActivityFragmentFactory(
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
            else -> return super.instantiate(classLoader, className)
        }
    }
}
