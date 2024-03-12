import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(val latitude: Double = 0.0, val longitude: Double = 0.0):Parcelable

