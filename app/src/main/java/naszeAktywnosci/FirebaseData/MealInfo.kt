package naszeAktywnosci.FirebaseData

import com.google.firebase.firestore.PropertyName

data class MealInfo(
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("carbohydrates") @set:PropertyName("carbohydrates") var carbohydrates: Double = 0.00,
    @get:PropertyName("mealId") @set:PropertyName("mealId") var mealId: String = ""
)
