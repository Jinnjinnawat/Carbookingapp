import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.carbookingapp.R
class CarAdapter(private val carList: List<Car>) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandTextView: TextView = itemView.findViewById(R.id.brandTextView)
        val modelTextView: TextView = itemView.findViewById(R.id.modelTextView)
        val licensePlateTextView: TextView = itemView.findViewById(R.id.licensePlateTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val carImageView: ImageView = itemView.findViewById(R.id.carImageView)
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

        // ตรวจสอบว่า img_car ไม่ว่างเปล่าและไม่ใช่ null
        if (!currentItem.img_car.isNullOrEmpty()) {
            Picasso.get().load(currentItem.img_car)
                .into(holder.carImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        // รูปภาพถูกโหลดสำเร็จ
                    }

                    override fun onError(e: Exception?) {
                        // เกิดข้อผิดพลาดในการโหลดรูปภาพ, แสดงรูปภาพ placeholder แทน
                        holder.carImageView.setImageResource(R.drawable.baseline_directions_car_24) // ใช้รูป placeholder ที่มีใน drawable
                    }
                })

        } else {
            // หาก URL ว่างเปล่าหรือ null ให้แสดงรูปภาพ placeholder หรือซ่อน ImageView
            holder.carImageView.setImageResource(R.drawable.baseline_directions_car_24) // ใช้รูป placeholder ที่มีใน drawable
        }
    }

    override fun getItemCount() = carList.size
}