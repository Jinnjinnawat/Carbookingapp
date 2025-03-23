import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carbookingapp.R


class CarFragment : Fragment() {

    private lateinit var carRecyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var carList: MutableList<Car>
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.car_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carRecyclerView = view.findViewById(R.id.carRecyclerView)
        carRecyclerView.layoutManager = LinearLayoutManager(context)

        carList = mutableListOf()
        carAdapter = CarAdapter(carList)
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
}