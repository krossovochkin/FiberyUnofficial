package by.krossovochkin.fiberyunofficial.entitytypelist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitytypelist.domain.GetEntityTypeListInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityTypeListPresentationModule {

    @JvmStatic
    @Provides
    fun entityTypeListArgs(
        fragment: Fragment,
        entityTypeListArgsProvider: EntityTypeListFragment.ArgsProvider
    ): EntityTypeListFragment.Args {
        return entityTypeListArgsProvider.getEntityTypeListArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityTypeListViewModel(
        fragment: Fragment,
        entityTypeListViewModelFactory: EntityTypeListViewModelFactory
    ): EntityTypeListViewModel {
        return ViewModelProvider(fragment, entityTypeListViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityTypeListViewModelFactory(
        getEntityTypeListInteractor: GetEntityTypeListInteractor,
        args: EntityTypeListFragment.Args
    ): EntityTypeListViewModelFactory {
        return EntityTypeListViewModelFactory(
            getEntityTypeListInteractor,
            args
        )
    }
}
