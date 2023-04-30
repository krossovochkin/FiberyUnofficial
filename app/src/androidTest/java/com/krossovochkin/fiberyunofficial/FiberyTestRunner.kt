package com.krossovochkin.fiberyunofficial

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.krossovochkin.fiberyunofficial.di.TestApp

class FiberyTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
