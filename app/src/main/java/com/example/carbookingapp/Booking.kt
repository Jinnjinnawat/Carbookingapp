import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Booking(
    var carModel: String =  "",
    var licensePlate: String = "",
    var name: String = "",
    var surname: String = "",
    var totalCost:String = "",
    var startDate: String = "",
    var startTime: String = "",
    var endDate: String = "",
    var endTime: String = "",
    var status: String = "",
    var phone: String = "",
    val documentId: String// เพิ่ม phone เข้ามา
): Parcelable // ทำให้ object นี้ส่งผ่าน Intent ได้