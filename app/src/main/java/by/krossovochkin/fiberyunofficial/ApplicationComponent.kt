package by.krossovochkin.fiberyunofficial

import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.data.api.ApiModule
import by.krossovochkin.fiberyunofficial.core.data.network.ApiAccount
import by.krossovochkin.fiberyunofficial.core.data.network.ApiToken
import by.krossovochkin.fiberyunofficial.core.data.network.NetworkModule
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ApiModule::class, SecretsModule::class])
interface ApplicationComponent : GlobalDependencies

@Module
object SecretsModule {

    @ApiAccount
    @JvmStatic
    @Provides
    fun apiAccount(): String = Secrets.API_ACCOUNT

    @ApiToken
    @JvmStatic
    @Provides
    fun apiToken(): String = Secrets.API_TOKEN
}