# WebView URL 配置安卓应用

这是一个使用 `WebView` 加载网页的安卓应用，用户可以通过点击 `Menu` 键弹出对话框，更改 `WebView` 加载的网页 URL。该 URL 会保存到 `SharedPreferences` 中，并在下次启动时自动加载。

## 功能特性

- 使用 `WebView` 显示网页内容
- 用户可通过 `Menu` 键弹出对话框并更改 `WebView` 加载的 URL
- 支持 URL 的持久化存储，应用重启后可加载上次保存的 URL
- 支持 JavaScript 开启

## 技术栈

- **编程语言**: Kotlin
- **Android SDK**: Android 电视、手机等设备
- **主要组件**:
    - `WebView`: 用于在应用中加载和显示网页
    - `AlertDialog`: 用于弹出对话框，让用户输入和修改 URL
    - `SharedPreferences`: 用于持久化存储 URL，确保应用下次打开时加载用户上次输入的网页地址

## 使用方法

### 1. 启动应用
- 打开应用后，`WebView` 会加载默认的网页 URL（例如 `https://www.example.com`）。
- 如果用户之前修改过 URL，`WebView` 会加载保存的 URL。

### 2. 修改网页地址
- 按下遥控器或设备的 `Menu` 键，弹出修改 URL 的对话框。
- 输入新的网页 URL 后，点击 **“保存”** 按钮。
- `WebView` 将自动刷新并加载新的网页内容。

### 3. 取消操作
- 如果不想更改 URL，点击对话框中的 **“取消”** 按钮，关闭对话框。

## 代码结构

- **MainActivity.kt**:
    - 主界面逻辑，包含 `WebView` 的初始化和 URL 加载功能。
    - 监听 `Menu` 键，弹出 `AlertDialog`，用户可以在对话框中输入新的 URL。
    - 使用 `SharedPreferences` 保存和读取 URL。

- **activity_main.xml**:
    - 布局文件，包含 `WebView` 元素，用于显示网页。

## 主要代码示例

### `MainActivity.kt`
```kotlin
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
        val defaultUrl = "https://www.example.com"
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
```
`activity_main.xml`
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <WebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```
## 运行环境
- 最低支持版本: Android 5.0 (API Level 21) 或更高版本
- 设备支持: 支持 Android 电视、手机和平板设备
## 如何构建
1. 克隆项目代码到本地：
```bash
git clone https://gitlab.ylongwang.top:8888/ylongwang/rfid_tv_webview.git
```
2. 使用 Android Studio 打开项目。

3. 连接安卓设备或模拟器，点击 运行 按钮将应用部署到设备上。

## 作者
开发者: ylongwang (https://github.com/ylongwang)
邮箱: ylongwang@icloud.com