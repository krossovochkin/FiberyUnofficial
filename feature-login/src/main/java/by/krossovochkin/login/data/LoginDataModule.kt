package by.krossovochkin.login.data

import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import by.krossovochkin.login.domain.LoginRepository
import dagger.Module
import dagger.Provides

@Module
object LoginDataModule {

    @JvmStatic
    @Provides
    fun loginRepository(
        authStorage: AuthStorage
    ): LoginRepository {
        return LoginRepositoryImpl(authStorage)
    }
}
