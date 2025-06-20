package com.krossovochkin.fiberyunofficial.di.auth

import android.content.Context
import com.krossovochkin.auth.AuthStorage
import com.krossovochkin.fiberyunofficial.AuthStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AuthModule {

    @Singleton
    @JvmStatic
    @Provides
    fun authStorage(
        context: Context
    ): AuthStorage {
        return AuthStorageImpl(context.applicationContext)
    }
}
