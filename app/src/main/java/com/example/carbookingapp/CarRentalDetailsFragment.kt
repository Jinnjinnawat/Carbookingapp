import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.carbookingapp.R
class CarRentalDetailsFragment : Fragment() {

    private lateinit var tvCarModel: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var tvLicensePlate: TextView
    private lateinit var tvName: TextView
    private lateinit var tvSurname: TextView
    private lateinit var tvStatus: TextView

    private var rentalId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rentalId = it.getString("rentalId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car_rental_details, container, false)

        // Initialize views
        tvCarModel = view.findViewById(R.id.tvCarModel)
        tvStartDate = view.findViewById(R.id.tvStartDate)
        tvStartTime = view.findViewById(R.id.tvStartTime)
        tvEndDate = view.findViewById(R.id.tvEndDate)
        tvEndTime = view.findViewById(R.id.tvEndTime)
        tvLicensePlate = view.findViewById(R.id.tvLicensePlate)
        tvName = view.findViewById(R.id.tvName)
        tvSurname = view.findViewById(R.id.tvSurname)
        tvStatus = view.findViewById(R.id.tvStatus)

        // Load rental details from Firebase
        loadRentalDetails()

        return view
    }

    private fun loadRentalDetails() {
        rentalId?.let {
            FirebaseDatabase.getInstance().getReference("bookings").child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.let {
                            val carModel = it.child("carModel").getValue(String::class.java)
                            val startDate = it.child("startDate").getValue(String::class.java)
                            val startTime = it.child("startTime").getValue(String::class.java)
                            val endDate = it.child("endDate").getValue(String::class.java)
                            val endTime = it.child("endTime").getValue(String::class.java)
                            val licensePlate = it.child("licensePlate").getValue(String::class.java)
                            val name = it.child("name").getValue(String::class.java)
                            val surname = it.child("surname").getValue(String::class.java)
                            val status = it.child("status").getValue(String::class.java)

                            // Set data to TextViews
                            tvCarModel.text = "Car Model: $carModel"
                            tvStartDate.text = "Start Date: $startDate"
                            tvStartTime.text = "Start Time: $startTime"
                            tvEndDate.text = "End Date: $endDate"
                            tvEndTime.text = "End Time: $endTime"
                            tvLicensePlate.text = "License Plate: $licensePlate"
                            tvName.text = "Name: $name"
                            tvSurname.text = "Surname: $surname"
                            tvStatus.text = "Status: $status"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle errors
                    }
                })
        }
    }

    companion object {
        fun newInstance(rentalId: String): CarRentalDetailsFragment {
            return CarRentalDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("rentalId", rentalId)
                }
            }
        }
    }
}