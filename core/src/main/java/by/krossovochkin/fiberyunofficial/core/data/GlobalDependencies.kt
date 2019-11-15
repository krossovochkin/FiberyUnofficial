package by.krossovochkin.fiberyunofficial.core.data

import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi

interface GlobalDependencies {

    fun fiberyServiceApi(): FiberyServiceApi
}