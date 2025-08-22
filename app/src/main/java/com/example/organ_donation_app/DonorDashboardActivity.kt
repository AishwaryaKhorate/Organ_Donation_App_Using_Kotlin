package com.example.organ_donation_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.organapp.EventsActivity
import com.example.organdonation.ImpactTrackerActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlin.math.abs

class DonorDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_dashboard)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // --- ActionBarDrawerToggle for hamburger icon ---
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // Make hamburger icon white (optional)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.holo_purple)

        // --- Navigation Drawer Actions ---
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_profile -> startActivity(Intent(this, DonorProfileActivity::class.java))
                R.id.nav_emergency -> {
                    val phoneNumber = "9822983208"
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    startActivity(dialIntent)
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // --- Header Image Slider ---
        val imageSlider: ViewPager2 = findViewById(R.id.imageSlider)
        val dotsIndicatorHeader: DotsIndicator = findViewById(R.id.dotsIndicatorHeader)
        val headerImages = listOf(
            R.drawable.donor_banner,
            R.drawable.donar_banner1,
            R.drawable.donar_banner2,
            R.drawable.donar_banner3,
            R.drawable.donar_banner4,
            R.drawable.donar_banner5
        )

        imageSlider.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val imageView = android.widget.ImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                }
                return object : RecyclerView.ViewHolder(imageView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as android.widget.ImageView).setImageResource(headerImages[position])
            }

            override fun getItemCount(): Int = headerImages.size
        }
        dotsIndicatorHeader.attachTo(imageSlider)

        // Auto-scroll
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (imageSlider.currentItem + 1) % headerImages.size
                imageSlider.currentItem = nextItem
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 3000)

        // --- Emergency FAB ---

        auth = FirebaseAuth.getInstance()

        // --- Dashboard Pages ---
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val dotsIndicator: DotsIndicator = findViewById(R.id.dots_indicator)
        val pages = listOf(
            R.layout.page_dashboard_cards,
            R.layout.activity_organ_fact
        )

        viewPager.adapter = object : RecyclerView.Adapter<PagerVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
                val view = LayoutInflater.from(parent.context).inflate(pages[viewType], parent, false)
                return PagerVH(view)
            }

            override fun getItemCount(): Int = pages.size

            override fun onBindViewHolder(holder: PagerVH, position: Int) {
                if (position == 0) {
                    val cardDonate: CardView = holder.itemView.findViewById(R.id.cardDonate)
                    val cardMedical: CardView = holder.itemView.findViewById(R.id.cardMedical)
                    val cardImpactTracker: CardView = holder.itemView.findViewById(R.id.cardTracker)
                    val cardEvents: CardView = holder.itemView.findViewById(R.id.cardEvents)
                    val cardAwareness: CardView = holder.itemView.findViewById(R.id.cardAwareness)

                    cardAwareness.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, AwarenessActivity::class.java))
                    }
                    cardDonate.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, OrganDonationActivity::class.java))
                    }
                    cardMedical.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, MedicalHistoryActivity::class.java))
                    }
                    cardImpactTracker.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, ImpactTrackerActivity::class.java))
                    }
                    cardEvents.setOnClickListener {
                        startActivity(Intent(this@DonorDashboardActivity, EventsActivity::class.java))
                    }
                }
            }

            override fun getItemViewType(position: Int): Int = position
        }
        dotsIndicator.attachTo(viewPager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        private val MIN_SCALE = 0.9f
        private val MIN_ALPHA = 0.6f
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> view.alpha = 0f
                position <= 1 -> {
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
                else -> view.alpha = 0f
            }
        }
    }
}
