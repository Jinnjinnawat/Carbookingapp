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
import kotlin.compareTo
import kotlin.toString

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

        // รับข้อมูลวันและเวลาจาก arguments
        val startDate = arguments?.getString("startDate")
        val startTime = arguments?.getString("startTime")
        val endDate = arguments?.getString("endDate")
        val endTime = arguments?.getString("endTime")

        startDate?.let { startDateEditText.setText(it) }
        startTime?.let { startTimeEditText.setText(it) }
        endDate?.let { endDateEditText.setText(it) }
        endTime?.let { endTimeEditText.setText(it) }

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
    private fun calculateTotalCost() {
        val startDateStr = startDateEditText.text.toString()
        val startTimeStr = startTimeEditText.text.toString()
        val endDateStr = endDateEditText.text.toString()
        val endTimeStr = endTimeEditText.text.toString()

        // ตรวจสอบว่ามีข้อมูลครบถ้วนไหม
        if (startDateStr.isNotEmpty() && startTimeStr.isNotEmpty() && endDateStr.isNotEmpty() && endTimeStr.isNotEmpty()) {
            try {
                // กำหนดรูปแบบวันที่และเวลา
                val dateFormat = SimpleDateFormat("d/M/yyyy HH:mm", Locale.getDefault())

                // รวมวันที่และเวลาเพื่อแปลงเป็น Date
                val startDateTimeStr = "$startDateStr $startTimeStr"
                val endDateTimeStr = "$endDateStr $endTimeStr"

                // แปลงข้อมูลที่ได้เป็น Date
                val startDate = dateFormat.parse(startDateTimeStr)
                val endDate = dateFormat.parse(endDateTimeStr)

                // เช็คว่าแปลงได้สำเร็จหรือไม่
                if (startDate != null && endDate != null && pricePerDay > 0) {
                    // คำนวณความต่างของเวลาใน millisecond
                    val diffInMs = endDate.time - startDate.time

                    // เช็คความต่างของเวลาว่ามากกว่าศูนย์ (ไม่ให้เลือกเวลาสิ้นสุดก่อนเวลาเริ่มต้น)
                    if (diffInMs <= 0) {
                        totalCostEditText.setText("Invalid date range")
                        return
                    }

                    // แปลงจาก millisecond เป็นวัน
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs) + 1

                    // คำนวณค่าใช้จ่ายรวม
                    val totalCost = diffInDays * pricePerDay

                    // แสดงผลค่าใช้จ่าย
                    totalCostEditText.setText(totalCost.toString())
                } else {
                    totalCostEditText.setText("Error")
                }
            } catch (e: Exception) {
                // หากเกิดข้อผิดพลาดในการคำนวณหรือแปลงวันเวลา
                totalCostEditText.setText("Error")
                Log.e("BookingFragment", "Error calculating total cost: ${e.message}")
            }
        } else {
            // หากข้อมูลไม่ครบถ้วน
            totalCostEditText.setText("Incomplete data")
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

        // ตรวจสอบว่ามีการกรอกข้อมูลครบถ้วนหรือไม่
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

            // บันทึกข้อมูลลง Firestore
            firestore.collection("bookings")
                .add(booking)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "บันทึกการจองสำเร็จ", Toast.LENGTH_SHORT).show()
                    navigateToCarFragment() // ไปยัง CarFragment
                    clearInputs() // ล้างข้อมูลที่กรอก
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "เกิดข้อผิดพลาดในการบันทึก: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "ค่าเช่าทั้งหมดต้องเป็นตัวเลข", Toast.LENGTH_SHORT).show()
        }
    }
    private fun navigateToCarFragment() {
        // สร้างตัว fragment ใหม่สำหรับ CarFragment
        val carFragment = CarFragment()

        // เปลี่ยน fragment โดยการใช้ fragment transaction
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, carFragment) // เปลี่ยน fragment ใน container
            .addToBackStack(null) // เพิ่ม fragment นี้ไปยัง back stack
            .commit() // ทำการ commit transaction
    }
    private fun clearInputs() {
        // ล้างค่าจาก EditText ทั้งหมด
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
}
