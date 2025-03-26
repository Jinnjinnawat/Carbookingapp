import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.carbookingapp.R

class CarAdapter(private val carList: List<Car>, private val onRentClick: (Car) -> Unit) :
    RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandTextView: TextView = itemView.findViewById(R.id.brandTextView)
        val modelTextView: TextView = itemView.findViewById(R.id.modelTextView)
        val licensePlateTextView: TextView = itemView.findViewById(R.id.licensePlateTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val carImageView: ImageView = itemView.findViewById(R.id.carImageView)
        val rentCarButton: Button = itemView.findViewById(R.id.rentCarButton) // เพิ่มปุ่มเช่ารถ
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.car_item, parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val currentItem = carList[position]

        holder.brandTextView.text = currentItem.brand
        holder.modelTextView.text = currentItem.model
        holder.licensePlateTextView.text = currentItem.license_plate
        holder.priceTextView.text = "Price: ${currentItem.price_per_day} บาท/วัน"
        holder.statusTextView.text = "Status: ${currentItem.status}"

        // โหลดรูปภาพจาก URL โดยใช้ Picasso
        if (!currentItem.img_car.isNullOrEmpty()) {
            Picasso.get().load(currentItem.img_car)
                .into(holder.carImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        holder.carImageView.setImageResource(R.drawable.baseline_directions_car_24)
                    }
                })
        } else {
            holder.carImageView.setImageResource(R.drawable.baseline_directions_car_24)
        }

        // ตั้งค่าปุ่มเช่ารถให้เรียก onRentClick และส่งข้อมูลรถกลับไปยัง CarFragment
        holder.rentCarButton.setOnClickListener {
            onRentClick(currentItem)
        }
    }

    override fun getItemCount(): Int = carList.size
}
