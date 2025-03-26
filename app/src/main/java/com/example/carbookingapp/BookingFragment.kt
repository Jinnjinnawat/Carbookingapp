package com.example.carbookingapp

import CarFragment
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
    private var pricePerDay: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        firestore = FirebaseFirestore.getInstance()

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

        val model = arguments?.getString("model")
        val brand = arguments?.getString("brand")
        val licensePlate = arguments?.getString("licensePlate")
        pricePerDay = arguments?.getString("price_per_day")?.toDoubleOrNull() ?: 0.0
        Log.d("BookingFragment", "pricePerDay: $pricePerDay")

        carModelEditText.setText("$brand $model")
        carIdEditText.setText(licensePlate)
        setupDateTimePickers()
        calculateTotalCost()

        startDateEditText.doOnTextChanged { _, _, _, _ -> calculateTotalCost() }
        startTimeEditText.doOnTextChanged { _, _, _, _ -> calculateTotalCost() }
        endDateEditText.doOnTextChanged { _, _, _, _ -> calculateTotalCost() }
        endTimeEditText.doOnTextChanged { _, _, _, _ -> calculateTotalCost() }

        bookButton.setOnClickListener {
            saveBookingToFirestore()
        }

        return view
    }

    private fun setupDateTimePickers() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        startDateEditText.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                startDateEditText.setText(dateFormat.format(calendar.time))
                calculateTotalCost()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        startTimeEditText.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                startTimeEditText.setText(timeFormat.format(calendar.time))
                calculateTotalCost()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        endDateEditText.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                endDateEditText.setText(dateFormat.format(calendar.time))
                calculateTotalCost()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        endTimeEditText.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                endTimeEditText.setText(timeFormat.format(calendar.time))
                calculateTotalCost()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
    }

    private fun calculateTotalCost() {
        val startDateStr = startDateEditText.text.toString()
        val startTimeStr = startTimeEditText.text.toString()
        val endDateStr = endDateEditText.text.toString()
        val endTimeStr = endTimeEditText.text.toString()

        if (startDateStr.isNotEmpty() && startTimeStr.isNotEmpty() && endDateStr.isNotEmpty() && endTimeStr.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val startDate = dateFormat.parse("$startDateStr $startTimeStr")
                val endDate = dateFormat.parse("$endDateStr $endTimeStr")

                if (startDate != null && endDate != null && pricePerDay > 0) {
                    val diffInMs = endDate.time - startDate.time
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs) + 1
                    val totalCost = diffInDays * pricePerDay
                    totalCostEditText.setText(totalCost.toString())
                }
            } catch (e: Exception) {
                totalCostEditText.setText("Error")
            }
        }
    }

    private fun saveBookingToFirestore() {
        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val startDate = startDateEditText.text.toString().trim()
        val startTime = startTimeEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val endDate = endDateEditText.text.toString().trim()
        val endTime = endTimeEditText.text.toString().trim()
        val carModel = carModelEditText.text.toString().trim()
        val carId = carIdEditText.text.toString().trim()
        val totalCost = totalCostEditText.text.toString().trim()

        if (name.isEmpty() || surname.isEmpty() || startDate.isEmpty() || startTime.isEmpty() ||
            phone.isEmpty() || endDate.isEmpty() || endTime.isEmpty() || carModel.isEmpty() ||
            carId.isEmpty() || totalCost.isEmpty()
        ) {
            Toast.makeText(requireContext(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            return
        }

        try {
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
                "totalCost" to totalCost.toDouble()
            )

            firestore.collection("bookings")
                .add(booking)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "บันทึกการจองสำเร็จ", Toast.LENGTH_SHORT).show()
                    navigateToCarFragment()
                    clearInputs()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "เกิดข้อผิดพลาดในการบันทึก: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "ค่าเช่าทั้งหมดต้องเป็นตัวเลข", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {



        nameEditText.text.clear()



        surnameEditText.text.clear()



        startDateEditText.text.clear()



        startTimeEditText.text.clear()



        phoneEditText.text.clear()



        endDateEditText.text.clear()



        endTimeEditText.text.clear()



        carModelEditText.text.clear()



        carIdEditText.text.clear()



        totalCostEditText.text.clear()



    }

    private fun navigateToCarFragment() {

        val carFragment = CarFragment()

        parentFragmentManager.beginTransaction()

            .replace(R.id.fragment_container, carFragment)

            .addToBackStack(null)

            .commit()

    }

}