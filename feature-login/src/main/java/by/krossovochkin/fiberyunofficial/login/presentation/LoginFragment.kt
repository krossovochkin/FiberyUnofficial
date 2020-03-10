package by.krossovochkin.fiberyunofficial.login.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
) : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var viewModel: LoginViewModel

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private var parentListener: ParentListener? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerLoginComponent.builder()
            .fragment(this)
            .loginGlobalDependencies(loginComponent)
            .build()
            .inject(this)

        viewModel.navigation.observe(viewLifecycleOwner, Observer { event ->
            when (event.getContentIfNotHandled()) {
                is LoginNavEvent.OnLoginSuccessEvent -> {
                    parentListener?.onLoginSuccess()
                }
            }
        })

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
