package naszeAktywnosci.FirebaseData

import com.google.firebase.firestore.PropertyName

data class InsulinInfo(
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("time") @set:PropertyName("time") var time: String = "",
    @get:PropertyName("measurment") @set:PropertyName("measurment") var measurment: Double = 0.00,
    @get:PropertyName("insulinId") @set:PropertyName("insulinId") var insulinId: String = ""
)
