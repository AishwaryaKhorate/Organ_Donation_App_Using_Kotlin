package com.example.organ_donation_app

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class AwarenessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awareness)

        val webView: WebView = findViewById(R.id.webViewAwareness)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // 🔗 Load YouTube awareness video (replace ID with your chosen video)
        val videoId = "d6gB1yT6z6k" // Example: awareness video ID
        val html = """
            <iframe width="100%" height="100%" 
                src="https://www.youtube.com/embed/$videoId" 
                frameborder="0" allowfullscreen>
            </iframe>
        """.trimIndent()

        webView.loadData(html, "text/html", "utf-8")
    }
}
