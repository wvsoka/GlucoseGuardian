package naszeAktywnosci.FirebaseData

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MealDataActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mealAdapter: MealInfoAdapter
    private lateinit var firestoreHandler: FirestoreHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        firestoreHandler = FirestoreHandler(FirebaseFirestore.getInstance())

        mealAdapter = MealInfoAdapter(emptyList())
        recyclerView.adapter = mealAdapter

        fetchMealInfo()
    }

    private fun fetchMealInfo() {
        lifecycleScope.launch {
            try {
                val userId = "exampleUserId" // Użyj rzeczywistego ID użytkownika
                val mealList = firestoreHandler.getMealInfo(userId)
                mealAdapter.updateMealList(mealList)
            } catch (e: Exception) {
                // Obsługa błędów
                e.printStackTrace()
            }
        }
    }
}
