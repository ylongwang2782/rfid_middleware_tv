package com.example.rfid_tv_webview

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var username: String
    private lateinit var password: String
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
        webView.clearCache(true);

        // 使用WebViewClient让WebView加载网页，而不跳转到默认浏览器
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // 在页面加载完成后，自动输入账号密码并提交表单
                val username = "admin"
                val password = "123456"

                val savedUsername = sharedPreferences.getString("username", "")
                val savedPassword = sharedPreferences.getString("password", "")

                val js = """
                    
        setTimeout(function () {
            const accountInput = document.querySelector('#account');
            accountInput.value = '$savedUsername';
            accountInput.dispatchEvent(new Event('input'));

            const passwordInput = document.querySelector('#password');
            passwordInput.value = '$savedPassword';
            passwordInput.dispatchEvent(new Event('input'));
 
            document.querySelector('button').click();
        }, 1000);
                    
                """.trimIndent()

                webView.evaluateJavascript(js, null)
            }
        }
        webView.loadUrl(url ?: defaultUrl)
    }

    // 弹出一个对话框来修改 URL，用户名和密码，带有倒计时
    private fun showSettingInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("设置应用参数")

        // 创建一个线性布局包含 EditText 和 倒计时 TextView
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // 从 SharedPreferences 加载保存的 URL、用户名和密码
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val savedUrl = sharedPreferences.getString("webview_url", "")
        val savedUsername = sharedPreferences.getString("username", "")
        val savedPassword = sharedPreferences.getString("password", "")

        // 创建用于输入 URL 的 EditText
        val inputUrl = EditText(this)
        inputUrl.hint = "请输入新的 URL"
        inputUrl.setText(savedUrl)  // 设置默认显示的 URL
        layout.addView(inputUrl)

        // 创建用于输入用户名的 EditText
        val inputUsername = EditText(this)
        inputUsername.hint = "请输入用户名"
        inputUsername.setText(savedUsername)  // 设置默认显示的用户名
        layout.addView(inputUsername)

        // 创建用于输入密码的 EditText
        val inputPassword = EditText(this)
        inputPassword.hint = "请输入密码"
        inputPassword.setText(savedPassword)  // 设置默认显示的密码
        inputPassword.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(inputPassword)

        // 倒计时 TextView
        val countdownText = TextView(this)
        layout.addView(countdownText)

        builder.setView(layout)

        // 设置对话框的按钮
        builder.setPositiveButton("保存") { dialog, _ ->
            val newUrl = inputUrl.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            if (newUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                // 保存到 SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("webview_url", newUrl)
                editor.putString("username", username)
                editor.putString("password", password)
                editor.apply()

                // 重新加载新的 URL
                webView.clearCache(true);
                webView.loadUrl(newUrl)

                // 标记用户已输入
                isInputReceived = true

                // 关闭对话框
                dialog.dismiss()
            } else {
                Toast.makeText(this, "URL、用户名和密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.cancel() // 取消操作，关闭对话框
            countdownTimer?.cancel()
        }

        val dialog = builder.create()
        dialog.show()

        // 添加 TextWatcher 监听输入
        inputUrl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isInputReceived && s?.isNotEmpty() == true) {
                    countdownTimer?.cancel()
                    countdownText.text = ""
                    isInputReceived = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 设置倒计时
        countdownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "倒计时: $secondsLeft 秒后将使用默认参数"
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
