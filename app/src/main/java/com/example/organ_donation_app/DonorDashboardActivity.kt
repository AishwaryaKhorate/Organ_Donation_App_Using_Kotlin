package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlin.math.abs

class DonorDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_dashboard)

        auth = FirebaseAuth.getInstance()

        // --- Views ---
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val dotsIndicator: DotsIndicator = findViewById(R.id.dots_indicator)

        // --- Pages for ViewPager2 ---
        val pages = listOf(
            R.layout.page_dashboard_cards, // Page 1 → all feature cards
            R.layout.activity_organ_fact   // Page 2 → info/stats
        )

        // --- Adapter Setup ---
        viewPager.adapter = object : RecyclerView.Adapter<PagerVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
                val view = LayoutInflater.from(parent.context).inflate(pages[viewType], parent, false)
                return PagerVH(view)
            }

            override fun getItemCount(): Int = pages.size

            override fun onBindViewHolder(holder: PagerVH, position: Int) {
                // Logic for the first page (Dashboard Cards)
                if (position == 0) {
                    val cardDonate: CardView = holder.itemView.findViewById(R.id.cardDonate)
                    val cardMedical: CardView = holder.itemView.findViewById(R.id.cardMedical)
                    val cardLogout: CardView = holder.itemView.findViewById(R.id.cardLogout)
                    val cardProfile: CardView = holder.itemView.findViewById(R.id.cardProfile)

                    // Set OnClick Listeners for each card
                    cardDonate.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, OrganDonationActivity::class.java))
                    }
                    cardMedical.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, MedicalHistoryActivity::class.java))
                    }
                    cardProfile.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, DonorProfileActivity::class.java))
                    }
                    cardLogout.setOnClickListener {
                        auth.signOut()
                        Toast.makeText(this@DonorDashboardActivity, "Logged out", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@DonorDashboardActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }

            override fun getItemViewType(position: Int): Int = position
        }

        // --- Attach Indicator and Transformer ---
        dotsIndicator.attachTo(viewPager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    // ViewHolder class for the ViewPager
    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Swipe animation effect class
    class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        private val MIN_SCALE = 0.9f
        private val MIN_ALPHA = 0.6f

        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> { // [-Infinity,-1)
                    view.alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                    val vertMargin = view.height * (1 - scaleFactor) / 2
                    val horzMargin = view.width * (1 - scaleFactor) / 2
                    if (position < 0) {
                        view.translationX = horzMargin - vertMargin / 2
                    } else {
                        view.translationX = -horzMargin + vertMargin / 2
                    }
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                    view.alpha = (MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    view.alpha = 0f
                }
            }
        }
    }
}