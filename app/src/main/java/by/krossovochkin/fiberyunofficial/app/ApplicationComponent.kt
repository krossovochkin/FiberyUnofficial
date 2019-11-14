package by.krossovochkin.fiberyunofficial.app

import by.krossovochkin.fiberyunofficial.applist.AppListGlobalDependencies
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.core.data.api.ApiModule
import by.krossovochkin.fiberyunofficial.core.data.network.NetworkModule
import by.krossovochkin.fiberyunofficial.entitylist.EntityListGlobalDependencies
import by.krossovochkin.fiberyunofficial.entitytypelist.EntityTypeListGlobalDependencies
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ApiModule::class])
interface ApplicationComponent :
    AppListGlobalDependencies,
    EntityTypeListGlobalDependencies,
    EntityListGlobalDependencies

interface GlobalDependencies {

    fun fiberyServiceApi(): FiberyServiceApi
}