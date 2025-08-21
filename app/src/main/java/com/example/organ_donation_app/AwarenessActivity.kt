package com.example.organ_donation_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AwarenessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awareness)

        setupReadMore(findViewById(R.id.tvAnswer1), findViewById(R.id.tvReadMore1))
        setupReadMore(findViewById(R.id.tvAnswer2), findViewById(R.id.tvReadMore2))
        setupReadMore(findViewById(R.id.tvAnswer3), findViewById(R.id.tvReadMore3))
        setupReadMore(findViewById(R.id.tvAnswer4), findViewById(R.id.tvReadMore4))

        // Video Card
        val cardVideo: CardView = findViewById(R.id.cardVideo)
        cardVideo.setOnClickListener {
            val videoUrl = "https://www.youtube.com/watch?v=R9Xolh0h0zE" // sample awareness video
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            intent.putExtra("force_fullscreen", true)
            startActivity(intent)
        }
    }

    private fun setupReadMore(answerView: TextView, readMoreView: TextView) {
        var expanded = false
        readMoreView.setOnClickListener {
            if (expanded) {
                // Collapse back
                answerView.maxLines = 2
                answerView.ellipsize = TextUtils.TruncateAt.END
                readMoreView.text = "Read More"
                expanded = false
            } else {
                // Expand full
                answerView.maxLines = Int.MAX_VALUE
                answerView.ellipsize = null
                readMoreView.text = "Read Less"
                expanded = true
            }
        }
    }
}
