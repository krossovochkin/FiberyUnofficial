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
package by.krossovochkin.fiberyunofficial.login.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.login.DaggerLoginComponent
import by.krossovochkin.fiberyunofficial.login.LoginParentComponent
import by.krossovochkin.fiberyunofficial.login.R
import by.krossovochkin.fiberyunofficial.login.databinding.LoginFragmentBinding
import javax.inject.Inject

private const val MOCK_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; " +
    "Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36"
private const val JS_INTERFACE_NAME = "FiberyUnofficial"
private const val FIBERY_IO_WEBSITE = "https://fibery.io"
private const val JS_EXTRACT_TOKEN =
    "fetch(`https://\${window.location.host}/api/tokens`, { method: 'POST' })\n" +
        "  .then(res => res.json())\n" +
        "  .then(obj => ${JS_INTERFACE_NAME}.onTokenReceived(obj.value))"

class LoginFragment(
    private val loginParentComponent: LoginParentComponent
) : Fragment(R.layout.login_fragment) {

    @Inject
    lateinit var viewModel: LoginViewModel

    private val binding by viewBinding(LoginFragmentBinding::bind)

    private var parentListener: ParentListener? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerLoginComponent.factory()
            .create(
                loginParentComponent = loginParentComponent,
                fragment = this
            )
            .inject(this)

        viewModel.navigation.observe(viewLifecycleOwner) { event ->
            when (event.getContentIfNotHandled()) {
                is LoginNavEvent.OnLoginSuccessEvent -> {
                    parentListener?.onLoginSuccess()
                }
            }
        }

        binding.loginWebView.apply {
            webViewClient = WebViewClient()
            settings.apply {
                userAgentString = MOCK_USER_AGENT
                javaScriptEnabled = true
            }
            val callback = { token: String -> login(token) }
            addJavascriptInterface(TokenExtractor(callback), JS_INTERFACE_NAME)
            loadUrl(FIBERY_IO_WEBSITE)
        }
        binding.extractButton.setOnClickListener {
            binding.loginWebView.evaluateJavascript(JS_EXTRACT_TOKEN, null)
        }
    }

    private fun login(token: String) {
        binding.loginWebView.post {
            viewModel.login(extractAccount(binding.loginWebView.url), token)
        }
    }

    private fun extractAccount(url: String?): String {
        return url
            .orEmpty()
            .substringAfter("https://")
            .substringBefore(".fibery.io")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentListener = context as ParentListener
    }

    override fun onDetach() {
        super.onDetach()
        parentListener = null
    }

    class TokenExtractor(
        private val callback: (String) -> Unit
    ) {

        @JavascriptInterface
        fun onTokenReceived(token: String) {
            callback(token)
        }
    }

    interface ParentListener {

        fun onLoginSuccess()
    }
}
