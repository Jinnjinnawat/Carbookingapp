package com.example.carbookingapp
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carbookingapp.MainActivity
import com.example.carbookingapp.R
import com.example.carbookingapp.RegisterFragment
import com.example.carbookingapp.BookingFragment

class LoginFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var regButton: Button
    private lateinit var rentCar: Button
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
        rentCar = view.findViewById(R.id.rentCarButton)
        loginButton.setOnClickListener {
            validateAndLogin()
        }

        regButton.setOnClickListener {
            val fragment = RegisterFragment()
            parentFragmentManager.beginTransaction() // แก้ไขตรงนี้
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

    }

    private fun validateAndLogin() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isEmpty()) {
            usernameEditText.error = "กรุณากรอกชื่อผู้ใช้"
            usernameEditText.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "กรุณากรอกรหัสผ่าน"
            passwordEditText.requestFocus()
            return
        }

        // แทนที่ด้วยการตรวจสอบชื่อผู้ใช้และรหัสผ่านจริง (เช่น จากฐานข้อมูล)
        if (username == "user" && password == "password") {
            navigateToMainActivity()
        } else {
            Toast.makeText(requireContext(), "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // ปิด Activity หลักหลังจากเริ่ม MainActivity
    }
}