package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class HospitalDashboardActivity : AppCompatActivity() {

    private lateinit var cardViewHistory: CardView
    private lateinit var cardViewOrganList: CardView
    private lateinit var cardViewLogout: CardView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_dashboard)

        auth = FirebaseAuth.getInstance()

        // 🔹 Setup cards
        cardViewHistory = findViewById(R.id.cardViewDonations)
        cardViewOrganList = findViewById(R.id.cardViewOrgans)
        cardViewLogout = findViewById(R.id.cardLogout)

        cardViewHistory.setOnClickListener {
            startActivity(Intent(this, AllDonationsActivity::class.java))
        }

        cardViewOrganList.setOnClickListener {
            startActivity(Intent(this, AllMedicalHistoryActivity::class.java))
        }

        cardViewLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 🔹 Setup Image Slider
        val imageSlider: ViewPager2 = findViewById(R.id.imageSliderHospital)
        val dotsIndicator: DotsIndicator = findViewById(R.id.dotsIndicatorHospital)

        val hospitalImages = listOf(
            R.drawable.donor_banner,
            R.drawable.donar_banner1,
            R.drawable.donar_banner2,
            R.drawable.donar_banner3,
            R.drawable.donar_banner4,
            R.drawable.donar_banner5,
        )

        // Inline adapter (no separate file)
        imageSlider.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                return object : RecyclerView.ViewHolder(imageView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as ImageView).setImageResource(hospitalImages[position])
            }

            override fun getItemCount(): Int = hospitalImages.size
        }

        dotsIndicator.attachTo(imageSlider)

        // 🔹 Auto-scroll slider
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (imageSlider.currentItem + 1) % hospitalImages.size
                imageSlider.currentItem = nextItem
                handler.postDelayed(this, 3000) // 3 sec
            }
        }
        handler.postDelayed(runnable, 3000)
    }
}
