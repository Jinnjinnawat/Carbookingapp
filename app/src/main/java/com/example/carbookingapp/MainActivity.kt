package com.example.carbookingapp
import CarFragment
import CarRentalDetailsFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // กำหนด Fragment เริ่มต้น
        loadFragment(LoginFragment()) //หรือจะ load home fragment ก็ได้

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.home -> CarFragment()
                R.id.search -> BookingDetailFragment()
                R.id.profile -> LoginFragment()
                R.id.register_button -> RegisterFragment()
                else -> null
            }

            selectedFragment?.let {
                loadFragment(it)
                true
            } ?: false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // ใช้ Fra
            .commit()
    }
}