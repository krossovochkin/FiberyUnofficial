package by.krossovochkin.fiberyunofficial.entitylist.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitylist.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListFilterInteractor
import by.krossovochkin.fiberyunofficial.entitylist.domain.SetEntityListSortInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityListPresentationModule {

    @JvmStatic
    @Provides
    fun entityListArgs(
        fragment: Fragment,
        entityListArgsProvider: EntityListFragment.ArgsProvider
    ): EntityListFragment.Args {
        return entityListArgsProvider.getEntityListArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityListViewModel(
        fragment: Fragment,
        entityListViewModelFactory: EntityListViewModelFactory
    ): EntityListViewModel {
        return ViewModelProvider(fragment, entityListViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityListViewModelFactory(
        getEntityListInteractor: GetEntityListInteractor,
        setEntityListFilterInteractor: SetEntityListFilterInteractor,
        setEntityListSortInteractor: SetEntityListSortInteractor,
        entityListArgs: EntityListFragment.Args
    ): EntityListViewModelFactory {
        return EntityListViewModelFactory(
            getEntityListInteractor,
            setEntityListFilterInteractor,
            setEntityListSortInteractor,
            entityListArgs
        )
    }
}
