package com.example.organ_donation_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DonorProfileActivity : AppCompatActivity() {

    // --- UI Elements ---
    // CHANGED: Use EditText for editable fields
    private lateinit var etName: EditText
    private lateinit var etBloodGroup: EditText
    private lateinit var etPhone: EditText
    private lateinit var tvEmail: TextView // Email remains a TextView
    private lateinit var btnSaveProfile: Button // NEW: Button to save data
    private lateinit var profileCircle: View

    // --- Firebase ---
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        // Firebase Initialization
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // UI Initialization
        profileCircle = findViewById(R.id.profileCircle)
        etName = findViewById(R.id.etName)
        tvEmail = findViewById(R.id.tvEmail)
        etBloodGroup = findViewById(R.id.etBloodGroup)
        etPhone = findViewById(R.id.etPhone)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        // Set up listener to reliably get user status
        setupAuthListener()

        // Set up listener for the save button
        btnSaveProfile.setOnClickListener {
            saveProfileData()
        }

        // Add a watcher to change circle color in real-time
        setupBloodGroupWatcher()
    }

    private fun setupAuthListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in, load their profile
                loadProfile(user.uid, user.email)
            } else {
                // User is signed out, close the activity
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun loadProfile(userId: String, userEmail: String?) {
        tvEmail.text = "Email: ${userEmail ?: "N/A"}"

        db.collection("donors").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: ""
                    val bloodGroup = document.getString("bloodGroup") ?: ""
                    val phone = document.getString("phone") ?: ""

                    // Set text in the EditText fields
                    etName.setText(name)
                    etBloodGroup.setText(bloodGroup)
                    etPhone.setText(phone)

                    setProfileCircleColor(bloodGroup)
                } else {
                    Toast.makeText(this, "No profile data found. Please save your info.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * NEW: Gathers data from EditTexts and saves it to Firestore.
     */
    private fun saveProfileData() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "You must be logged in to save.", Toast.LENGTH_SHORT).show()
            return
        }

        // Get text from EditTexts and trim whitespace
        val name = etName.text.toString().trim()
        val bloodGroup = etBloodGroup.text.toString().trim().uppercase()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || bloodGroup.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map of the data to be saved
        val donorData = hashMapOf(
            "name" to name,
            "bloodGroup" to bloodGroup,
            "phone" to phone,
            "email" to user.email // Also save the email for consistency
        )

        // Use .set() with merge to update fields without overwriting the whole document
        db.collection("donors").document(user.uid)
            .set(donorData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupBloodGroupWatcher() {
        etBloodGroup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setProfileCircleColor(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setProfileCircleColor(bloodGroup: String) {
        val colorRes = when (bloodGroup.uppercase()) {
            "O+" -> R.color.light_blue_100
            "O-" -> R.color.light_pink
            "A+" -> R.color.light_yellow
            "A-" -> R.color.light_green
            "B+" -> R.color.light_purple
            "B-" -> R.color.light_blue_100
            "AB+" -> R.color.light_pink
            "AB-" -> R.color.light_green
            else -> R.color.light_purple
        }
        profileCircle.background.setTint(ContextCompat.getColor(this, colorRes))
    }
}