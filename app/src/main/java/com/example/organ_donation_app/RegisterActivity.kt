package com.example.organ_donation_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var loginRedirect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        nameEditText = findViewById(R.id.editTextName)
        roleSpinner = findViewById(R.id.roleSpinner)
        registerButton = findViewById(R.id.buttonRegister)
        loginRedirect = findViewById(R.id.textLogin)

        // Spinner items
        val roles = arrayOf("Donor", "Hospital")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    val userMap = hashMapOf(
                        "uid" to uid,
                        "email" to email,
                        "name" to name,
                        "role" to role
                    )

                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registered as $role", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to store user data", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Registration Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
