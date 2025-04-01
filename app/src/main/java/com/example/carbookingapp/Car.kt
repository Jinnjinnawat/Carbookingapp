import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val brand: String = "",
    val model: String = "",
    val license_plate: String = "",
    val price_per_day: String = "",
    var status: String = "",
    val img_car: String = "",
    // เพิ่ม year กลับมา
) : Parcelable