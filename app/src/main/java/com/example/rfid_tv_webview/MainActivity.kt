package com.example.rfid_tv_webview

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.webkit.WebView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val handler = Handler(Looper.getMainLooper())
    private var isInputReceived = false
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        // 显示对话框并设置超时处理
        showSettingInputDialog()
    }

    // 从 SharedPreferences 加载 URL 并刷新 WebView
    private fun loadUrlFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val defaultUrl = "http://192.168.0.100:8083"
        val url = sharedPreferences.getString("webview_url", defaultUrl)
        webView.loadUrl(url ?: defaultUrl)
    }

    // 弹出一个对话框来修改 URL，带有倒计时
    private fun showSettingInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("设置应用参数")

        // 创建一个线性布局包含 EditText 和 倒计时 TextView
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val input = EditText(this)
        input.hint = "请输入新的 URL"
        layout.addView(input)

        val countdownText = TextView(this)
        layout.addView(countdownText)

        builder.setView(layout)

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

                // 标记用户已输入
                isInputReceived = true

                // 关闭对话框
                dialog.dismiss()
            } else {
                Toast.makeText(this, "URL 不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.cancel() // 取消操作，关闭对话框
            countdownTimer?.cancel()
        }

        val dialog = builder.create()
        dialog.show()

        // 添加TextWatcher监听输入
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 如果用户开始输入内容，取消倒计时
                if (!isInputReceived && s?.isNotEmpty() == true) {
                    countdownTimer?.cancel()
                    countdownText.text = "";
                    isInputReceived = true // 标记用户已输入，取消倒计时
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 设置倒计时，显示倒计时信息并在5秒后自动关闭对话框
        countdownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "倒计时: $secondsLeft 秒后将使用默认参数继续"
            }

            override fun onFinish() {
                if (!isInputReceived) {
                    dialog.dismiss() // 关闭对话框
                    loadUrlFromPreferences() // 加载默认URL
                }
            }
        }.start()
    }
}
