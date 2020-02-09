package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityDetailsPresentationModule {

    @JvmStatic
    @Provides
    fun entityDetailsViewModel(
        fragment: Fragment,
        entityDetailsViewModelFactory: EntityDetailsViewModelFactory
    ): EntityDetailsViewModel {
        return ViewModelProvider(fragment, entityDetailsViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityDetailsViewModelFactory(
        getEntityDetailsInteractor: GetEntityDetailsInteractor,
        entityDetailsParentListener: EntityDetailsViewModel.ParentListener,
        entityDetailsArgs: EntityDetailsFragment.Args
    ): EntityDetailsViewModelFactory {
        return EntityDetailsViewModelFactory(
            getEntityDetailsInteractor,
            entityDetailsParentListener,
            entityDetailsArgs
        )
    }

    @JvmStatic
    @Provides
    fun entityDetailsArgs(
        fragment: Fragment,
        entityDetailsArgsProvider: EntityDetailsFragment.ArgsProvider
    ): EntityDetailsFragment.Args {
        return entityDetailsArgsProvider.getEntityDetailsArgs(fragment.requireArguments())
    }
}
