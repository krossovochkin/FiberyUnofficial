package by.krossovochkin.fiberyunofficial.core

import android.app.Application
import okhttp3.OkHttpClient

internal fun OkHttpClient.Builder.addDebugNetworkInterceptor(): OkHttpClient.Builder {
    return this
}

fun Application.initDebugTools() = Unit