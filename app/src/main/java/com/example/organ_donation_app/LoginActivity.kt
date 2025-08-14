package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var radioGroupRole: RadioGroup
    private lateinit var buttonLogin: Button
    private lateinit var textViewRegister: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val HOSPITAL_SECRET_CODE = "hospital123" // 🔹 Change as needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        radioGroupRole = findViewById(R.id.radioGroupRole)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewRegister = findViewById(R.id.textViewRegister)

        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val selectedRole = if (radioGroupRole.checkedRadioButtonId == R.id.radioDonor) "Donor" else "Hospital"

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedRole == "Hospital") {
                val input = EditText(this)
                input.hint = "Enter Hospital Code"

                AlertDialog.Builder(this)
                    .setTitle("Hospital Verification")
                    .setView(input)
                    .setPositiveButton("Verify") { dialog, _ ->
                        if (input.text.toString() == HOSPITAL_SECRET_CODE) {
                            loginUser(email, password, selectedRole)
                        } else {
                            Toast.makeText(this, "Invalid hospital code", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            } else {
                loginUser(email, password, selectedRole)
            }
        }
    }

    private fun loginUser(email: String, password: String, selectedRole: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid ?: return@addOnSuccessListener

                firestore.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val storedRole = doc.getString("role")
                        if (storedRole == selectedRole) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = if (storedRole == "Donor") {
                                Intent(this, DonorDashboardActivity::class.java)
                            } else {
                                Intent(this, HospitalDashboardActivity::class.java)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Role mismatch. Please select correct role.", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "User role not found", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
