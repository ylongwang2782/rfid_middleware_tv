package com.example.rfid_tv_webview

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // 设置布局
        setContentView(R.layout.activity_main)

        // webView configure
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.setWebViewClient(WebViewClient()) // Prevent opening links in external browser

        val defaultUrl = "http://192.168.0.100:8083" // 默认网址
        webView.loadUrl(defaultUrl)
        webView.visibility = View.VISIBLE

//        // 新定义的变量存储设置的网址，并通过intent从外部读取
//        var savedUrl: String? = intent.getStringExtra("url")
//        val sharedPreferences: SharedPreferences = getPreferences(MODE_PRIVATE)
//        if (savedUrl != null) {
//            // 不为空则直接加载新网址
//            webView.loadUrl(savedUrl)
//            // 然后存入库中，以实现刷新默认网址变量
//            saveUrl(savedUrl)
//        } else {
//            // 默认的savedUrl为空则赋值上次存储的网址
//            savedUrl = sharedPreferences.getString("url", "")
//            if (savedUrl != null) {
//                webView.loadUrl(savedUrl)
//            } else {
//                webView.loadUrl("http://192.168.0.100:8083")
//            }
//        }
    }

    // 自定义WebViewClient，用于处理网页加载
    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?, request: WebResourceRequest?
        ): Boolean {
            // 在WebView内加载网页，而不是使用外部浏览器
            view?.loadUrl(request?.url.toString())
            return true
        }
    }

    // 保存网址函数
    private fun saveUrl(url: String) {
        val sharedPreferences: SharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("url", url)
        editor.apply()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}