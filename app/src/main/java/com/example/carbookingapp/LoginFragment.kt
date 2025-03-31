package com.example.carbookingapp

import CarFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var regButton: Button
    private val db = FirebaseFirestore.getInstance() // เชื่อม Firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEditText = view.findViewById(R.id.username_edit_text)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        loginButton = view.findViewById(R.id.login_button)
        regButton = view.findViewById(R.id.register_button)

        loginButton.setOnClickListener {
            validateAndLogin()
        }

        regButton.setOnClickListener {
            val fragment = RegisterFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun validateAndLogin() {
        val email = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty()) {
            usernameEditText.error = "กรุณากรอกอีเมล"
            usernameEditText.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "กรุณากรอกรหัสผ่าน"
            passwordEditText.requestFocus()
            return
        }

        checkUserInFirestore(email, password)
    }

    private fun checkUserInFirestore(email: String, password: String) {
        db.collection("customers")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    navigateToHomeFragment()
                } else {
                    Toast.makeText(requireContext(), "อีเมลหรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "เกิดข้อผิดพลาด กรุณาลองใหม่", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHomeFragment() {
        val fragment = CarFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
