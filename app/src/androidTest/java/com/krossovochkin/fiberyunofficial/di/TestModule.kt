package com.krossovochkin.fiberyunofficial.di

import androidx.test.espresso.IdlingResource
import com.krossovochkin.fiberyunofficial.idlingresource.OkHttpIdlingResource
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
object TestModule {

    @JvmStatic
    @Provides
    fun okHttpIdlingResource(
        okHttpClient: OkHttpClient
    ): IdlingResource {
        return OkHttpIdlingResource(okHttpClient.dispatcher)
    }
}
