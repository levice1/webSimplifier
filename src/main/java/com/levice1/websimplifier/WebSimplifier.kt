package com.levice1.websimplifier
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.*
import androidx.core.view.WindowInsetsAnimationCompat.Callback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object WebSimplifier {
    val TAG = "ArticleSimplifier"
    enum class ReturnType {
        HTML,
        JSON,
        PLAIN
    }

    /**
     * Simplifies a web page (via Readability.js and WebView) and returns
     *  - HTML (<html><head><title>'I see hundreds of child sex abuse images a week for my job'</title></head> <body><div id=...</body></html>)
     *  - JSON ( {"title": "News title", "content": "News content"} )
     *  - Plain text ("Clear article plain text")
     */

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun simplifyPage(
        context: Context,
        link: String,
        returnType: ReturnType,
        onError: (String) -> Unit = {}
    ): String = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowContentAccess = true
            }

            webView.addJavascriptInterface(object {
                @JavascriptInterface
                fun onParsed(result: String) {
                    if (continuation.isActive) {
                        continuation.resume(result)
                    }
                }

                @JavascriptInterface
                fun onError(error: String) {
                    Log.e(TAG, "Error: $error")
                    onError(error)
                    if (continuation.isActive) {
                        continuation.resume("")
                    }
                }
            }, "AndroidInterface")

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val typeString = when (returnType) {
                        ReturnType.HTML -> "html"
                        ReturnType.JSON -> "json"
                        ReturnType.PLAIN -> "plain"
                    }
                    val jsCode = "parseArticle('${link.replace("'", "\\'")}', '$typeString');"
                    webView.evaluateJavascript(jsCode, null)
                }
            }

            webView.loadUrl("file:///android_asset/localParserBridge.html")

            continuation.invokeOnCancellation {
                webView.destroy()
            }
        }
    }
}