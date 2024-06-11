package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikacjatestowa.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.MealInfo

class AddMealActivity : AppCompatActivity() {
    private lateinit var buttonBack : Button
    private lateinit var buttonAddMeal : Button
    private lateinit var meal : EditText
    private lateinit var numberWW : EditText

    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        buttonBack = findViewById(R.id.button_backToMainFromMeal)
        buttonBack.setOnClickListener { openActivityMain() }

        buttonAddMeal = findViewById(R.id.button_addmeal)
        meal = findViewById(R.id.editText_meal)
        numberWW = findViewById(R.id.editTextNumber_WW)

        buttonAddMeal.setOnClickListener {
            addMeal()
        }

        userId = intent.getStringExtra("uID") ?: ""

    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun addMeal() {
        val mealName = meal.text.toString()
        val carbohydrates = numberWW.text.toString().toDoubleOrNull() ?: 0.0

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val mealId = dbOperations.generateId(db, "meal_info") // Generate a unique meal ID
                val mealInfo = MealInfo(
                    name = mealName,
                    date = getCurrentDate(),
                    carbohydrates = carbohydrates,
                    mealId = mealId
                )
                dbOperations.addMealInfo(userId, mealInfo)
                Toast.makeText(this@AddMealActivity, "Meal added successfully", Toast.LENGTH_SHORT).show()
                openActivityMain()
            } catch (e: Exception) {
                Toast.makeText(this@AddMealActivity, "Failed to add meal", Toast.LENGTH_SHORT).show()
                // Handle the exception
            }
        }
    }


    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}