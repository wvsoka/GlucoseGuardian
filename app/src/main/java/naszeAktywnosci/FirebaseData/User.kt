package naszeAktywnosci.FirebaseData
import com.google.firebase.firestore.PropertyName

data class User(
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("dateOfBirth") @set:PropertyName("dateOfBirth") var dateOfBirth: String = "",
    @get:PropertyName("insulinWW") @set:PropertyName("insulinWW") var insulinWW: Double = 0.00,
    @get:PropertyName("hypoglycaemia") @set:PropertyName("hypoglycaemia") var hypoglycaemia: Double = 0.00,
    @get:PropertyName("hyperglycaemia") @set:PropertyName("hyperglycaemia") var hyperglycaemia: Double = 0.00,
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""
)
