package com.krossovochkin.fiberyunofficial.idlingresource

import androidx.test.espresso.IdlingResource
import okhttp3.Dispatcher

class OkHttpIdlingResource(
    private val dispatcher: Dispatcher,
) : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null

    init {
        dispatcher.idleCallback = Runnable {
            callback?.onTransitionToIdle()
        }
    }

    override fun getName(): String = "OkHttp"

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    override fun isIdleNow(): Boolean = dispatcher.runningCallsCount() == 0
}
