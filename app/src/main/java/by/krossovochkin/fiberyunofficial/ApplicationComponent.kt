package by.krossovochkin.fiberyunofficial

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.GlobalDependencies
import by.krossovochkin.fiberyunofficial.core.data.api.ApiModule
import by.krossovochkin.fiberyunofficial.core.data.auth.AuthStorage
import by.krossovochkin.fiberyunofficial.core.data.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ApiModule::class, AuthModule::class])
interface ApplicationComponent : GlobalDependencies {

    fun authStorage(): AuthStorage

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }

}

@Module
object AuthModule {

    @JvmStatic
    @Provides
    fun authStorage(
        context: Context
    ): AuthStorage {
        return AuthStorageImpl(context.applicationContext)
    }
}
