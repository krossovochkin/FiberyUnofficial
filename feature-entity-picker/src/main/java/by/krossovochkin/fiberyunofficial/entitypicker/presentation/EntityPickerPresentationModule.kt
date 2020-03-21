package by.krossovochkin.fiberyunofficial.entitypicker.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityListInteractor
import by.krossovochkin.fiberyunofficial.entitypicker.domain.GetEntityTypeSchemaInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityPickerPresentationModule {

    @JvmStatic
    @Provides
    fun entityPickerArgs(
        fragment: Fragment,
        entityPickerArgsProvider: EntityPickerFragment.ArgsProvider
    ): EntityPickerFragment.Args {
        return entityPickerArgsProvider.getEntityPickerArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityPickerViewModel(
        fragment: Fragment,
        entityPickerViewModelFactory: EntityPickerViewModelFactory
    ): EntityPickerViewModel {
        return ViewModelProvider(fragment, entityPickerViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityPickerViewModelFactory(
        getEntityTypeSchemaInteractor: GetEntityTypeSchemaInteractor,
        getEntityListInteractor: GetEntityListInteractor,
        entityPickerArgs: EntityPickerFragment.Args
    ): EntityPickerViewModelFactory {
        return EntityPickerViewModelFactory(
            getEntityTypeSchemaInteractor,
            getEntityListInteractor,
            entityPickerArgs
        )
    }
}
