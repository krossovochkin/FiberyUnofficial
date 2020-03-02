package by.krossovochkin.fiberyunofficial.entitydetails.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitydetails.domain.GetEntityDetailsInteractor
import by.krossovochkin.fiberyunofficial.entitydetails.domain.UpdateEntitySingleSelectInteractor
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
        updateEntitySingleSelectInteractor: UpdateEntitySingleSelectInteractor,
        entityDetailsArgs: EntityDetailsFragment.Args
    ): EntityDetailsViewModelFactory {
        return EntityDetailsViewModelFactory(
            getEntityDetailsInteractor,
            updateEntitySingleSelectInteractor,
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
