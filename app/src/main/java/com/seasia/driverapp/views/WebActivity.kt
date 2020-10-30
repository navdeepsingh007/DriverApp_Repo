package com.seasia.driverapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil.setContentView
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.ActivityWebBinding
import com.seasia.driverapp.utils.BaseActivity

class WebActivity : BaseActivity() {

var binding:ActivityWebBinding?=null
    override fun initViews() {
        binding=viewDataBinding as ActivityWebBinding
        val title = intent.extras!!.get("title").toString()
        binding!!.commonToolBar.imgToolbarText.setText(title)
        binding!!.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        if (title.equals("Terms and Conditions")) {
            binding!!.webView.loadUrl("https://www.cerebruminfotech.com/")
        }
        else if (title.equals("Privacy Policy")) {
            binding!!.webView.loadUrl("https://www.cerebruminfotech.com/")
        }
        else {
            binding!!.webView.loadUrl("https://www.cerebruminfotech.com/")
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_web
    }
}