package by.krossovochkin.fiberyunofficial

import android.app.Application

class App : Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .context(this)
            .build()
    }
}
