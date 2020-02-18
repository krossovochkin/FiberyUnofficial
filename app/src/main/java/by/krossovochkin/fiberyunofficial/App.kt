package by.krossovochkin.fiberyunofficial

import android.app.Application

class App : Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
            .context(this)
            .build()
    }
}
