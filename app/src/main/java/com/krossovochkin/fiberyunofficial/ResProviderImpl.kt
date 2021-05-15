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
package com.krossovochkin.fiberyunofficial

import android.content.Context
import androidx.core.content.ContextCompat
import com.krossovochkin.fiberyunofficial.core.presentation.ColorUtils
import com.krossovochkin.fiberyunofficial.core.presentation.ResProvider

class ResProviderImpl(
    private val context: Context
) : ResProvider {

    override fun getString(stringResId: Int): String {
        return context.getString(stringResId)
    }

    override fun getString(stringResId: Int, vararg params: Any): String {
        return context.getString(stringResId, *params)
    }

    override fun getColor(colorResId: Int): Int {
        return ContextCompat.getColor(context, colorResId)
    }

    override fun getColorAttr(themedContext: Context, colorAttrResId: Int): Int {
        return ColorUtils.getColor(themedContext, colorAttrResId)
    }
}
