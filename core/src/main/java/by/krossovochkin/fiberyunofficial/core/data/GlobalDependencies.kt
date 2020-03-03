package by.krossovochkin.fiberyunofficial.core.data

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi

interface GlobalDependencies {

    fun context(): Context

    fun fiberyServiceApi(): FiberyServiceApi
}
