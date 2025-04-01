import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carbookingapp.BookingFragment
import com.example.carbookingapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class CarFragment : Fragment() {

    private lateinit var carRecyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var carList: MutableList<Car>
    private lateinit var db: FirebaseFirestore

    private lateinit var startDateButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endDateButton: Button
    private lateinit var endTimeButton: Button
    private val startDate = Calendar.getInstance()
    private val endDate = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.car_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startDateButton = view.findViewById(R.id.startDateButton)
        startTimeButton = view.findViewById(R.id.startTimeButton)
        endDateButton = view.findViewById(R.id.endDateButton)
        endTimeButton = view.findViewById(R.id.endTimeButton)

        startDateButton.setOnClickListener { showDatePicker(startDate, true) }
        startTimeButton.setOnClickListener { showTimePicker(startDate, true) }
        endDateButton.setOnClickListener { showDatePicker(endDate, false) }
        endTimeButton.setOnClickListener { showTimePicker(endDate, false) }

        carRecyclerView = view.findViewById(R.id.carRecyclerView)
        carRecyclerView.layoutManager = LinearLayoutManager(context)

        carList = mutableListOf()
        carAdapter = CarAdapter(carList) { selectedCar ->
            openBookingFragment(selectedCar)
        }
        carRecyclerView.adapter = carAdapter

        db = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()
        fetchAvailableCars()
    }

    private fun fetchAvailableCars() {
        val startDateTimestamp = getTimestampFromDateTime(startDateButton.text.toString(), startTimeButton.text.toString())
        val endDateTimestamp = getTimestampFromDateTime(endDateButton.text.toString(), endTimeButton.text.toString())

        if (startDateTimestamp == null || endDateTimestamp == null) {
            Toast.makeText(requireContext(), "กรุณาเลือกวันเวลาเริ่มต้นและสิ้นสุด", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("CarFragment", "startDateTimestamp: $startDateTimestamp")
        Log.d("CarFragment", "endDateTimestamp: $endDateTimestamp")

        db.collection("cars").get()
            .addOnSuccessListener { result ->
                val allCars = result.toObjects(Car::class.java)
                val availableCars = mutableListOf<Car>()

                allCars.forEach { car ->
                    db.collection("bookings")
                        .whereEqualTo("carModel", car.model)
                        .whereLessThan("startDate", endDateTimestamp) // ตรวจสอบช่วงทับซ้อน
                        .whereGreaterThan("endDate", startDateTimestamp) // ตรวจสอบช่วงทับซ้อน
                        .get()
                        .addOnSuccessListener { bookingResult ->
                            // เช็คสถานะการจอง
                            val isBooked = bookingResult.documents.isNotEmpty()
                            val status = when {
                                isBooked -> "approve"
                                else -> "Not approved"
                            }
                            car.status = status

                            if (!isBooked) {
                                availableCars.add(car)
                            }

                            if (allCars.size == availableCars.size + bookingResult.size()) {
                                carList.clear()
                                carList.addAll(availableCars)
                                carAdapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("CarFragment", "Error checking booking", exception)
                            if (allCars.size == availableCars.size) {
                                carList.clear()
                                carList.addAll(availableCars)
                                carAdapter.notifyDataSetChanged()
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CarFragment", "Error fetching cars", exception)
            }
    }

    private fun showDatePicker(calendar: Calendar, isStart: Boolean) {
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateText = "$dayOfMonth/${month + 1}/$year"
                if (isStart) {
                    startDateButton.text = dateText
                } else {
                    endDateButton.text = dateText
                    // ตรวจสอบว่าวันที่สิ้นสุดไม่น้อยกว่าวันที่เริ่มต้น
                    if (endDate.timeInMillis < startDate.timeInMillis) {
                        Toast.makeText(requireContext(), "วันที่สิ้นสุดต้องไม่น้อยกว่าวันที่เริ่มต้น", Toast.LENGTH_SHORT).show()
                        endDateButton.text = startDateButton.text // ตั้งค่าวันที่สิ้นสุดเป็นวันที่เริ่มต้น
                    }
                }
                fetchAvailableCars()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        if (isStart) {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        } else {
            // กำหนดวันที่ต่ำสุดเป็นวันที่เริ่มต้น
            datePickerDialog.datePicker.minDate = startDate.timeInMillis
        }

        datePickerDialog.show()
    }

    private fun showTimePicker(calendar: Calendar, isStart: Boolean) {
        val timePickerDialog = TimePickerDialog(requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val timeText = String.format("%02d:%02d", hourOfDay, minute)
                if (isStart) {
                    startTimeButton.text = timeText
                } else {
                    endTimeButton.text = timeText
                    // ตรวจสอบว่าเวลาสิ้นสุดไม่น้อยกว่าเวลาเริ่มต้น (หากวันที่เท่ากัน)
                    val endTime = Calendar.getInstance()
                    endTime.timeInMillis = calendar.timeInMillis
                    val startTime = Calendar.getInstance()
                    startTime.timeInMillis = startDate.timeInMillis
                    if (endDate.timeInMillis == startDate.timeInMillis && endTime.timeInMillis < startTime.timeInMillis) {
                        Toast.makeText(requireContext(), "เวลาสิ้นสุดต้องไม่น้อยกว่าเวลาเริ่มต้น", Toast.LENGTH_SHORT).show()
                        endTimeButton.text = startTimeButton.text // ตั้งค่าเวลาสิ้นสุดเป็นเวลาเริ่มต้น
                    }
                    // ตรวจสอบว่าวันเวลาสิ้นสุดไม่น้อยกว่าวันเวลาเริ่มต้น
                    else if (endDate.timeInMillis < startDate.timeInMillis) {
                        Toast.makeText(requireContext(), "วันเวลาสิ้นสุดต้องไม่น้อยกว่าวันเวลาเริ่มต้น", Toast.LENGTH_SHORT).show()
                        endTimeButton.text = startTimeButton.text // ตั้งค่าเวลาสิ้นสุดเป็นเวลาเริ่มต้น
                    }
                }
                fetchAvailableCars()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun openBookingFragment(car: Car) {
        val fragment = BookingFragment().apply {
            arguments = Bundle().apply {
                putString("model", car.model)
                putString("brand", car.brand)
                putString("licensePlate", car.license_plate)
                putString("price_per_day", car.price_per_day)
                putString("startDate", startDateButton.text.toString())
                putString("startTime", startTimeButton.text.toString())
                putString("endDate", endDateButton.text.toString())
                putString("endTime", endTimeButton.text.toString())
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getTimestampFromDateTime(date: String, time: String): Timestamp? {
        if (date.isEmpty() || time.isEmpty()) {
            return null
        }
        return try {
            val dateTimeString = "$date $time"
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val parsedDate = sdf.parse(dateTimeString)

            if (parsedDate != null) {
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.time = parsedDate

                if (startDateButton.text.isNotEmpty() && startDateButton.text.toString() == SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)) {
                    val currentCalendar = Calendar.getInstance()
                    if (selectedCalendar.timeInMillis < currentCalendar.timeInMillis) {
                        Toast.makeText(requireContext(), "ไม่สามารถเลือกเวลาที่ผ่านมาได้", Toast.LENGTH_SHORT).show()
                        return null
                    }
                }
                // ตรวจสอบว่าวันเวลาสิ้นสุดไม่น้อยกว่าวันเวลาเริ่มต้น
                val startDateCalendar = Calendar.getInstance()
                val startDateString = startDateButton.text.toString() + " " + startTimeButton.text.toString()
                val startDateParsedDate = sdf.parse(startDateString)
                if (startDateParsedDate != null && selectedCalendar.timeInMillis < startDateParsedDate.time) {
                    Toast.makeText(requireContext(), "วันเวลาสิ้นสุดต้องไม่น้อยกว่าวันเวลาเริ่มต้น", Toast.LENGTH_SHORT).show()
                    return null
                }
                return Timestamp(parsedDate)
            } else {
                return null
            }

        } catch (e: Exception) {
            Log.e("CarFragment", "Error parsing date time", e)
            return null
        }
    }
}