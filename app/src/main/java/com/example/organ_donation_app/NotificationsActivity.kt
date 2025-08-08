package com.example.organ_donation_app

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {

    private lateinit var notifListLayout: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private var currentCardCount = 0

    private val cardColors = listOf(
        Color.parseColor("#B3E5FC"),
        Color.parseColor("#FFB6C1"),
        Color.parseColor("#FFF9C4"),
        Color.parseColor("#C8E6C9")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        notifListLayout = findViewById(R.id.notifList)
        listenForNotifications()
    }

    private fun listenForNotifications() {
        db.collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener

                // Only process added documents (so we append latest at top)
                for (change in snapshot.documentChanges) {
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val message = change.document.getString("message") ?: "Notification"
                        addNotificationCard(message)
                    }
                }
            }
    }

    private fun addNotificationCard(message: String) {
        val card = CardView(this).apply {
            radius = 14f
            cardElevation = 6f
            setContentPadding(20, 20, 20, 20)
            setCardBackgroundColor(cardColors[currentCardCount % cardColors.size])
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            layoutParams = params
        }

        val tv = TextView(this).apply {
            text = message
            textSize = 15f
            setTextColor(Color.BLACK)
        }

        card.addView(tv)
        notifListLayout.addView(card, 0) // newest on top
        currentCardCount++
    }
}
