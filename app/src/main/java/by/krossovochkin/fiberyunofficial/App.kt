package by.krossovochkin.fiberyunofficial

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .context(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
