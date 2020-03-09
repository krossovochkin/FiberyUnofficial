package by.krossovochkin.fiberyunofficial.entitycreate.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import by.krossovochkin.fiberyunofficial.entitycreate.domain.EntityCreateInteractor
import dagger.Module
import dagger.Provides

@Module
object EntityCreatePresentationModule {

    @JvmStatic
    @Provides
    fun entityCreateArgs(
        fragment: Fragment,
        entityCreateArgsProvider: EntityCreateFragment.ArgsProvider
    ): EntityCreateFragment.Args {
        return entityCreateArgsProvider.getEntityCreateArgs(fragment.requireArguments())
    }

    @JvmStatic
    @Provides
    fun entityCreateViewModel(
        fragment: Fragment,
        entityCreateViewModelFactory: EntityCreateViewModelFactory
    ): EntityCreateViewModel {
        return ViewModelProvider(fragment, entityCreateViewModelFactory).get()
    }

    @JvmStatic
    @Provides
    fun entityCreateViewModelFactory(
        entityCreateArgs: EntityCreateFragment.Args,
        entityCreateInteractor: EntityCreateInteractor
    ): EntityCreateViewModelFactory {
        return EntityCreateViewModelFactory(
            entityCreateArgs,
            entityCreateInteractor
        )
    }
}
