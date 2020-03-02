package by.krossovochkin.fiberyunofficial

import by.krossovochkin.fiberyunofficial.applist.AppListParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.presentation.EntityDetailsFragment
import by.krossovochkin.fiberyunofficial.entitylist.EntityListParentComponent
import by.krossovochkin.fiberyunofficial.entitylist.presentation.EntityListFragment
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListParentComponent
import by.krossovochkin.fiberyunofficial.entitytypelist.presentation.EntityTypeListFragment
import by.krossovochkin.fiberyunofficial.login.LoginParentComponent
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope

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
        fun mainActivityArgsProvider(
            mainActivityArgsProvider: MainActivityArgsProvider
        ): Builder

        fun build(): MainActivityComponent
    }
}

@Module
abstract class MainActivityModule {

    @MainActivityScope
    @Binds
    abstract fun entityTypeListArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityTypeListFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityListArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityListFragment.ArgsProvider

    @MainActivityScope
    @Binds
    abstract fun entityDetailsArgsProvider(
        mainActivityArgsProvider: MainActivityArgsProvider
    ): EntityDetailsFragment.ArgsProvider
}

@Scope
@Retention
annotation class MainActivityScope
