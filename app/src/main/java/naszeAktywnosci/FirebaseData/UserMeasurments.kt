package naszeAktywnosci.FirebaseData

import com.google.firebase.firestore.PropertyName

data class UserMeasurments(
    @get:PropertyName("measurment") @set:PropertyName("measurment") var measurment: Double = 0.00,
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("measurmentID") @set:PropertyName("measurmentID") var measurmentID: String = ""
)
