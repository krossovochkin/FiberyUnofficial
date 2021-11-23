package com.krossovochkin.fiberyfunofficial.test.domain

import com.krossovochkin.fiberyunofficial.domain.FiberyAppData

class FiberyAppDataBuilder {

    var name: String = DEFAULT_APP_NAME

    fun build(): FiberyAppData {
        return FiberyAppData(
            name = name
        )
    }

    companion object {
        private const val DEFAULT_APP_NAME = "Test App"
    }
}
