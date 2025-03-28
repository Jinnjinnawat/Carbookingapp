package com.example.carbookingapp

import Booking
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class BookingDetailFragment : Fragment() {

    private lateinit var carModelTextView: TextView
    private lateinit var licensePlateTextView: TextView
    private lateinit var nameSurnameTextView: TextView
    private lateinit var totalCostTextView: TextView
    private lateinit var startDateTextView: TextView
    private lateinit var startTimeTextView: TextView
    private lateinit var endDateTextView: TextView
    private lateinit var endTimeTextView: TextView
    private lateinit var statusTextView: TextView

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking_detail, container, false)

        carModelTextView = view.findViewById(R.id.text_view_car_model)
        licensePlateTextView = view.findViewById(R.id.text_view_license_plate)
        nameSurnameTextView = view.findViewById(R.id.text_view_name_surname)
        totalCostTextView = view.findViewById(R.id.text_view_total_cost)
        startDateTextView = view.findViewById(R.id.text_view_start_date)
        startTimeTextView = view.findViewById(R.id.text_view_start_time)
        endDateTextView = view.findViewById(R.id.text_view_end_date)
        endTimeTextView = view.findViewById(R.id.text_view_end_time)
        statusTextView = view.findViewById(R.id.text_view_status)

        firestore = FirebaseFirestore.getInstance()

        return view
    }

    fun setBookingId(bookingId: String) {
        fetchBookingDetails(bookingId)
    }

    private fun fetchBookingDetails(bookingId: String) {
        firestore.collection("bookings")
            .document(bookingId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val booking = document.toObject(Booking::class.java)
                    if (booking != null) {
                        displayBookingDetails(booking)
                    } else {
                        Toast.makeText(context, "Error: Booking data is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: Booking not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching booking details: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("BookingDetailFragment", "Error fetching booking: ", exception)
            }
    }

    private fun displayBookingDetails(booking: Booking) {
        carModelTextView.text = "รุ่นรถ: ${booking.carModel}"
        licensePlateTextView.text = "ทะเบียน: ${booking.licensePlate}"
        nameSurnameTextView.text = "ชื่อ: ${booking.name} ${booking.surname}"
        totalCostTextView.text = "ค่าใช้จ่าย: ${booking.totalCost}"
        startDateTextView.text = "วันที่เริ่ม: ${booking.startDate}"
        startTimeTextView.text = "เวลาเริ่ม: ${booking.startTime}"
        endDateTextView.text = "วันที่สิ้นสุด: ${booking.endDate}"
        endTimeTextView.text = "สถานะ: ${booking.status}"
    }
}