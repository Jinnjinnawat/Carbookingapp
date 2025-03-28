import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    var carId: String? = null,
    var carModel: String? = null,
    var licensePlate: String? = null,
    var name: String? = null,
    var surname: String? = null,
    var totalCost: String? = null,
    var startDate: String? = null,
    var startTime: String? = null,
    var endDate: String? = null,
    var endTime: String? = null,
    var status: String? = null,
    var phone: String? = null,
    var documentId: String? = null
) : Parcelable
