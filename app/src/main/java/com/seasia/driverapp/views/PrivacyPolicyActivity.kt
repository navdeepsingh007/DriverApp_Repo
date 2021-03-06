package com.seasia.driverapp.views

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.ActivityWebviewBinding
import com.seasia.driverapp.utils.BaseActivity

class PrivacyPolicyActivity : BaseActivity() {
    private lateinit var binding: ActivityWebviewBinding

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    override fun initViews() {
//        binding = viewDataBinding as ActivityWebviewBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        initToolbar()
        loadUrl()
    }

    private fun initToolbar() {
        binding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.privacy_policy)
        binding.commonToolBar.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
    }

    private fun loadUrl() {
//        binding.webview.loadUrl("www.google.com")

        binding.webviewGeneric.settings.setJavaScriptEnabled(true)

//        val url = "https://www.cerebruminfotech.com"
        val url = "file:///android_asset/comingsoon.html"
        binding.webviewGeneric.loadUrl(url)

        binding.webviewGeneric.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(view.url)
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url)
                stopProgressDialog()
                return true
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                stopProgressDialog()
                handler.proceed() // Ignore SSL certificate errors
            }
        })
//        binding.webviewGeneric.loadUrl("http://www.google.com")

        stopProgressDialog()
    }
}