package com.krossovochkin.fiberyunofficial

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import okhttp3.OkHttpClient

private var networkPlugin: NetworkFlipperPlugin? = null

internal fun OkHttpClient.Builder.addDebugNetworkInterceptor(): OkHttpClient.Builder {
    return this
        .addNetworkInterceptor(FlipperOkhttpInterceptor(networkPlugin))
}

fun Application.initDebugTools() {
    SoLoader.init(this, false)
    if (FlipperUtils.shouldEnableFlipper(this)) {
        with(AndroidFlipperClient.getInstance(this)) {
            networkPlugin = NetworkFlipperPlugin()
            addPlugin(networkPlugin)
            start()
        }
    }
}
