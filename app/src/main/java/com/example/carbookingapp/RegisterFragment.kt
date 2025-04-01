package com.example.carbookingapp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carbookingapp.R

class RegisterFragment : Fragment() {

    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        firstNameEditText = view.findViewById(R.id.register_first_name_edit_text)
        lastNameEditText = view.findViewById(R.id.register_last_name_edit_text)
        emailEditText = view.findViewById(R.id.register_email_edit_text)
        passwordEditText = view.findViewById(R.id.register_password_edit_text)
        addressEditText = view.findViewById(R.id.register_address_edit_text)
        phoneEditText = view.findViewById(R.id.register_phone_edit_text)
        registerButton = view.findViewById(R.id.register_confirm_button)

        db = FirebaseFirestore.getInstance()

        registerButton.setOnClickListener {
            registerUser()
            val fragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }

    private fun registerUser() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            return
        }

        val user = hashMapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "email" to email,
            "password" to password,
            "address" to address,
            "phone" to phone,
            "status" to "1"
        )

        db.collection("customers")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(context, "ลงทะเบียนสำเร็จ", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ย้ายฟังก์ชันนี้เข้าไปใน RegisterFragment

}
