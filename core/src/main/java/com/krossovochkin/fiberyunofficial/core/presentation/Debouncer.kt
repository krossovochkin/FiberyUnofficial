/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.core.presentation

import android.os.Handler
import android.os.Looper
import java.util.concurrent.TimeUnit

private const val MESSAGE_WHAT = 3

class Debouncer(
    private val timeout: Long,
    private val unit: TimeUnit,
    private val callback: (String) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper()) { message ->
        if (message.what != MESSAGE_WHAT) {
            return@Handler false
        }
        callback(message.obj as String)
        true
    }

    fun process(text: String) {
        handler.removeMessages(MESSAGE_WHAT)
        val message = handler.obtainMessage(
            MESSAGE_WHAT,
            text
        )
        handler.sendMessageDelayed(message, unit.toMillis(timeout))
    }
}
