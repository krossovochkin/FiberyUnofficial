package com.krossovochkin.fiberyunofficial.di.auth

import android.content.Context
import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.AuthStorageImpl
import dagger.Module
import dagger.Provides

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
