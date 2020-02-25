package by.krossovochkin.fiberyunofficial.login.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import by.krossovochkin.fiberyunofficial.core.presentation.BaseFragment
import by.krossovochkin.fiberyunofficial.core.presentation.viewBinding
import by.krossovochkin.fiberyunofficial.login.DaggerLoginComponent
import by.krossovochkin.fiberyunofficial.login.LoginParentComponent
import by.krossovochkin.fiberyunofficial.login.R
import by.krossovochkin.fiberyunofficial.login.databinding.FragmentLoginBinding
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
    private val loginComponent: LoginParentComponent
) : BaseFragment(R.layout.fragment_login) {

    @Inject
    lateinit var viewModel: LoginViewModel

    private val binding by viewBinding(FragmentLoginBinding::bind)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerLoginComponent.builder()
            .fragment(this)
            .loginGlobalDependencies(loginComponent)
            .build()
            .inject(this)

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

    private fun extractAccount(url: String): String {
        return url
            .substringAfter("https://")
            .substringBefore(".fibery.io")
    }

    class TokenExtractor(
        private val callback: (String) -> Unit
    ) {

        @JavascriptInterface
        fun onTokenReceived(token: String) {
            callback(token)
        }
    }
}
