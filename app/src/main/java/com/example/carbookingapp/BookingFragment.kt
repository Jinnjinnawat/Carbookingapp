package com.example.carbookingapp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carbookingapp.R

class BookingFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var carModelEditText: EditText
    private lateinit var carIdEditText: EditText
    private lateinit var totalCostEditText: EditText
    private lateinit var bookButton: Button

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        nameEditText = view.findViewById(R.id.nameEditText)
        surnameEditText = view.findViewById(R.id.surnameEditText)
        startDateEditText = view.findViewById(R.id.startDateEditText)
        startTimeEditText = view.findViewById(R.id.startTimeEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        endDateEditText = view.findViewById(R.id.endDateEditText)
        endTimeEditText = view.findViewById(R.id.endTimeEditText)
        carModelEditText = view.findViewById(R.id.carModelEditText)
        carIdEditText = view.findViewById(R.id.carIdEditText)
        totalCostEditText = view.findViewById(R.id.totalCostEditText)
        bookButton = view.findViewById(R.id.bookButton)
        // Set click listener for the book button
        bookButton.setOnClickListener {
            saveBookingToFirestore()
        }

        return view
    }

    private fun saveBookingToFirestore() {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val startDate = startDateEditText.text.toString()
        val startTime = startTimeEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val endDate = endDateEditText.text.toString()
        val endTime = endTimeEditText.text.toString()
        val carModel = carModelEditText.text.toString()
        val carId = carIdEditText.text.toString()
        val totalCost = totalCostEditText.text.toString().toDouble()

        val booking = hashMapOf(
            "name" to name,
            "surname" to surname,
            "startDate" to startDate,
            "startTime" to startTime,
            "phone" to phone,
            "endDate" to endDate,
            "endTime" to endTime,
            "carModel" to carModel,
            "carId" to carId,
            "totalCost" to totalCost
        )

        firestore.collection("bookings")
            .add(booking)
            .addOnSuccessListener {
                // Booking saved successfully
                // You can add code to show a success message here
            }
            .addOnFailureListener { e ->
                // Handle errors
                // You can add code to show an error message here
            }
    }
}