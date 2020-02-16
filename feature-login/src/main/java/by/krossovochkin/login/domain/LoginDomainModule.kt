package by.krossovochkin.login.domain

import dagger.Module
import dagger.Provides

@Module
object LoginDomainModule {

    @JvmStatic
    @Provides
    fun loginInteractor(
        LoginRepository: LoginRepository
    ): LoginInteractor {
        return LoginInteractorImpl(
            LoginRepository
        )
    }
}
