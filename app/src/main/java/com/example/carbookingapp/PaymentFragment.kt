import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carbookingapp.R

class PaymentFragment : Fragment() {

    private var carModel: String = ""
    private var customerName: String = ""
    private var startDate: String = ""
    private var endDate: String = ""
    private var totalCost: Double = 0.0

    private lateinit var textCarModel: TextView
    private lateinit var textCustomerName: TextView
    private lateinit var textStartDate: TextView
    private lateinit var textEndDate: TextView
    private lateinit var textTotalCost: TextView
    private lateinit var radioGroupPayment: RadioGroup
    private lateinit var buttonPaymentConfirm: Button

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        textCarModel = view.findViewById(R.id.text_car_model)
        textCustomerName = view.findViewById(R.id.text_customer_name)
        textStartDate = view.findViewById(R.id.text_start_date)
        textEndDate = view.findViewById(R.id.text_end_date)
        textTotalCost = view.findViewById(R.id.text_total_cost)
        radioGroupPayment = view.findViewById(R.id.radio_group_payment)
        buttonPaymentConfirm = view.findViewById(R.id.button_payment_confirm)

        db = FirebaseFirestore.getInstance()

        arguments?.let {
            carModel = it.getString("carModel", "")
            customerName = it.getString("customerName", "")
            startDate = it.getString("startDate", "")
            endDate = it.getString("endDate", "")
            totalCost = it.getDouble("totalCost", 0.0)
        }

        textCarModel.text = "Car Model: $carModel"
        textCustomerName.text = "Customer Name: $customerName"
        textStartDate.text = "Start Date: $startDate"
        textEndDate.text = "End Date: $endDate"
        textTotalCost.text = "Total Cost: $totalCost"

        buttonPaymentConfirm.setOnClickListener {
            val selectedPaymentMethodId = radioGroupPayment.checkedRadioButtonId
            val selectedPaymentMethod = view.findViewById<RadioButton>(selectedPaymentMethodId)?.text.toString()

            val paymentData = hashMapOf(
                "carModel" to carModel,
                "customerName" to customerName,
                "startDate" to startDate,
                "endDate" to endDate,
                "totalCost" to totalCost,
                "paymentMethod" to selectedPaymentMethod
            )

            db.collection("payments").add(paymentData)
                .addOnSuccessListener {
                    // Start Google Maps
                    openGoogleMaps()
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }

        return view
    }

    private fun openGoogleMaps() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=ร้านรถเช่า ศรีราชา ชลบุรี") // Replace with your desired location
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    companion object {
        fun newInstance(
            carModel: String,
            customerName: String,
            startDate: String,
            endDate: String,
            totalCost: Double
        ): PaymentFragment {
            val fragment = PaymentFragment()
            val args = Bundle()
            args.putString("carModel", carModel)
            args.putString("customerName", customerName)
            args.putString("startDate", startDate)
            args.putString("endDate", endDate)
            args.putDouble("totalCost", totalCost)
            fragment.arguments = args
            return fragment
        }
    }
}