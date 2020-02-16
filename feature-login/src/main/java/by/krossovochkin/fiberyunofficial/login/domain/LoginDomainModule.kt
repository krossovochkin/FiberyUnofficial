package by.krossovochkin.fiberyunofficial.login.domain

import dagger.Module
import dagger.Provides

@Module
object LoginDomainModule {

    @JvmStatic
    @Provides
    fun loginInteractor(
        loginRepository: LoginRepository
    ): LoginInteractor {
        return LoginInteractorImpl(
            loginRepository
        )
    }
}
