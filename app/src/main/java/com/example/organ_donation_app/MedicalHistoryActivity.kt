package com.example.organ_donation_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicalHistoryActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextContact: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextBloodGroup: EditText
    private lateinit var editTextDiseases: EditText
    private lateinit var editTextMedications: EditText
    private lateinit var editTextAllergies: EditText
    private lateinit var buttonSubmit: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        editTextName = findViewById(R.id.editTextName)
        editTextContact = findViewById(R.id.editTextContact)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextBloodGroup = findViewById(R.id.editTextBloodGroup)
        editTextDiseases = findViewById(R.id.editTextDiseases)
        editTextMedications = findViewById(R.id.editTextMedications)
        editTextAllergies = findViewById(R.id.editTextAllergies)
        buttonSubmit = findViewById(R.id.buttonSubmitMedical)

        buttonSubmit.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val contact = editTextContact.text.toString().trim()
            val address = editTextAddress.text.toString().trim()
            val bloodGroup = editTextBloodGroup.text.toString().trim()
            val diseases = editTextDiseases.text.toString().trim()
            val medications = editTextMedications.text.toString().trim()
            val allergies = editTextAllergies.text.toString().trim()

            if (name.isEmpty() || contact.isEmpty() || bloodGroup.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val history = hashMapOf(
                "uid" to uid,
                "name" to name,
                "contact" to contact,
                "address" to address,
                "bloodGroup" to bloodGroup,
                "diseases" to diseases,
                "medications" to medications,
                "allergies" to allergies,
                "timestamp" to Timestamp.now()
            )

            firestore.collection("medical_history").document(uid)
                .set(history)
                .addOnSuccessListener {
                    Toast.makeText(this, "Medical history saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save medical history", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
