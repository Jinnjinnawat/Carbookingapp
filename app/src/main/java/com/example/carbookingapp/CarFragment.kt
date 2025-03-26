import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carbookingapp.R
import com.example.carbookingapp.BookingFragment

class CarFragment : Fragment() {

    private lateinit var carRecyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var carList: MutableList<Car>
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.car_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carRecyclerView = view.findViewById(R.id.carRecyclerView)
        carRecyclerView.layoutManager = LinearLayoutManager(context)

        carList = mutableListOf()
        carAdapter = CarAdapter(carList) { selectedCar ->
            openBookingFragment(selectedCar) // เรียกฟังก์ชันเปิด BookingFragment พร้อมข้อมูลรถ
        }
        carRecyclerView.adapter = carAdapter

        db = FirebaseFirestore.getInstance()
        db.collection("cars").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val car = document.toObject(Car::class.java)
                    carList.add(car)
                }
                carAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // จัดการ Error
            }
    }

    private fun openBookingFragment(car: Car) {
        val fragment = BookingFragment().apply {
            arguments = Bundle().apply {
                putParcelable("selectedCar", car) // ส่งข้อมูลรถไปยัง BookingFragment
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


}
