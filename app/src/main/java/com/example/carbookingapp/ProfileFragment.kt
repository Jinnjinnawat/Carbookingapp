package com.example.carbookingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var licenseNoTextView: TextView
    private val db = FirebaseFirestore.getInstance() // เชื่อม Firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // เชื่อม UI กับตัวแปร
        firstNameTextView = view.findViewById(R.id.first_name_text)
        lastNameTextView = view.findViewById(R.id.last_name_text)
        phoneTextView = view.findViewById(R.id.phone_text)
        addressTextView = view.findViewById(R.id.address_text)
        licenseNoTextView = view.findViewById(R.id.license_no_text)

        // ดึง user ID จาก SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            fetchUserData(userId)
        } else {
            Toast.makeText(requireContext(), "ไม่พบข้อมูลผู้ใช้", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData(userId: String) {
        db.collection("customers").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    firstNameTextView.text = document.getString("first_name")
                    lastNameTextView.text = document.getString("last_name")
                    phoneTextView.text = document.getString("phone")
                    addressTextView.text = document.getString("address")
                    licenseNoTextView.text = document.getString("license_no")
                } else {
                    Toast.makeText(requireContext(), "ไม่พบข้อมูลผู้ใช้", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "เกิดข้อผิดพลาด กรุณาลองใหม่", Toast.LENGTH_SHORT).show()
            }
    }
}
