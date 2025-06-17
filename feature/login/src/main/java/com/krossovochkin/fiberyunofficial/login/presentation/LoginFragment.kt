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
package com.krossovochkin.fiberyunofficial.login.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krossovochkin.core.presentation.flow.collect
import com.krossovochkin.core.presentation.result.parentListener
import com.krossovochkin.core.presentation.viewbinding.viewBinding
import com.krossovochkin.fiberyunofficial.login.R
import com.krossovochkin.fiberyunofficial.login.databinding.LoginFragmentBinding
import dagger.Lazy

class LoginFragment(
    viewModelFactory : Lazy<LoginViewModelFactory>
) : Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels { viewModelFactory.get() }

    private val binding by viewBinding(LoginFragmentBinding::bind)

    private val parentListener: ParentListener by parentListener()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigation.collect(this) { event ->
            when (event) {
                is LoginNavEvent.OnLoginSuccessEvent -> {
                    parentListener.onLoginSuccess()
                }
            }
        }

        binding.loginButton.setOnClickListener {
            viewModel.login(
                binding.accountTextInput.editText?.text?.toString() ?: "",
                binding.tokenTextInput.editText?.text?.toString() ?: "",
            )
        }
    }

    interface ParentListener {

        fun onLoginSuccess()
    }
}
