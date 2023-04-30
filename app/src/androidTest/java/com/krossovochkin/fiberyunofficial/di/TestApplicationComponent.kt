package com.krossovochkin.fiberyunofficial.di

import android.content.Context
import androidx.test.espresso.IdlingResource
import com.krossovochkin.fiberyunofficial.ApplicationComponent
import com.krossovochkin.fiberyunofficial.di.api.ApiModule
import com.krossovochkin.fiberyunofficial.di.auth.AuthModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, AuthModule::class, TestModule::class])
interface TestApplicationComponent : ApplicationComponent {

    fun okHttpIdlingResource(): IdlingResource

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context
        ): TestApplicationComponent
    }
}
