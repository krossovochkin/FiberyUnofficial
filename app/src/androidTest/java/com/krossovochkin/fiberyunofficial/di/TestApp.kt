package com.krossovochkin.fiberyunofficial.di

import com.krossovochkin.fiberyunofficial.App

class TestApp : App() {

    override val applicationComponent: TestApplicationComponent by lazy {
        DaggerTestApplicationComponent.factory()
            .create(
                context = this
            )
    }
}
