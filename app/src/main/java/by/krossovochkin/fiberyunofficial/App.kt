package by.krossovochkin.fiberyunofficial

import android.app.Application
import com.facebook.stetho.Stetho

class App : Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        applicationComponent = DaggerApplicationComponent.builder()
            .context(this)
            .build()
    }
}
