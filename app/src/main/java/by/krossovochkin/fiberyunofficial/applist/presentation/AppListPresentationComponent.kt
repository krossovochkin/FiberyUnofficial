package by.krossovochkin.fiberyunofficial.applist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.krossovochkin.fiberyunofficial.applist.AppListGlobalDependencies
import by.krossovochkin.fiberyunofficial.applist.domain.AppListDomainComponent
import by.krossovochkin.fiberyunofficial.applist.domain.GetAppListInteractor
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    modules = [AppListPresentationModule::class],
    dependencies = [AppListDomainComponent::class, AppListGlobalDependencies::class]
)
interface AppListPresentationComponent {

    fun appListViewModel(): AppListViewModel

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        fun appListDomainComponent(appListDomainComponent: AppListDomainComponent): Builder

        fun appListPresentationModule(appListPresentationModule: AppListPresentationModule): Builder

        fun appListGlobalDependencies(appListGlobalDependencies: AppListGlobalDependencies): Builder

        fun build(): AppListPresentationComponent
    }
}

@Module
object AppListPresentationModule {

    @JvmStatic
    @Provides
    fun appListViewModel(
        fragment: Fragment,
        appListViewModelFactory: AppListViewModelFactory
    ): AppListViewModel {
        return ViewModelProviders
            .of(fragment, appListViewModelFactory)
            .get()
    }

    @JvmStatic
    @Provides
    fun appListViewModelFactory(
        navController: NavController,
        getAppListInteractor: GetAppListInteractor
    ): AppListViewModelFactory {
        return AppListViewModelFactory(
            navController,
            getAppListInteractor
        )
    }

    @JvmStatic
    @Provides
    fun navController(fragment: Fragment) = fragment.findNavController()
}

object AppListPresentationComponentFactory {

    fun create(
        fragment: Fragment,
        appListDomainComponent: AppListDomainComponent,
        appListGlobalDependencies: AppListGlobalDependencies
    ): AppListPresentationComponent {
        return DaggerAppListPresentationComponent.builder()
            .appListDomainComponent(appListDomainComponent)
            .appListPresentationModule(AppListPresentationModule)
            .appListGlobalDependencies(appListGlobalDependencies)
            .fragment(fragment)
            .build()
    }
}
