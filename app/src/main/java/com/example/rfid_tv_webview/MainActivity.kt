package com.example.rfid_tv_webview

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        // 加载保存的 URL 或默认的 URL
        loadUrlFromPreferences()
    }

    // 监听 Menu 键，弹出对话框修改 URL
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_MENU) {
            showUrlInputDialog()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    // 从 SharedPreferences 加载 URL 并刷新 WebView
    private fun loadUrlFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val defaultUrl = "http://192.168.0.100:8083"
        val url = sharedPreferences.getString("webview_url", defaultUrl)
        webView.loadUrl(url ?: defaultUrl)
    }

    // 弹出一个对话框来修改 URL
    private fun showUrlInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("修改网页地址")

        // 创建一个 EditText 用于输入新的 URL
        val input = EditText(this)
        input.hint = "请输入新的 URL"
        builder.setView(input)

        // 设置对话框的按钮
        builder.setPositiveButton("保存") { dialog, _ ->
            val newUrl = input.text.toString()

            if (newUrl.isNotEmpty()) {
                // 保存到 SharedPreferences
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("webview_url", newUrl)
                editor.apply()

                // 重新加载新的 URL
                webView.loadUrl(newUrl)

                // 关闭对话框
                dialog.dismiss()
            } else {
                Toast.makeText(this, "URL 不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.cancel() // 取消操作，关闭对话框
        }

        // 显示对话框
        builder.show()
    }
}
