package naszeAktywnosci.FirebaseData

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import naszeAktywnosci.LoginActivity
import naszeAktywnosci.MainActivity

class MealDataActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mealAdapter: MealInfoAdapter
    private lateinit var firestoreHandler: FirestoreHandler
    private lateinit var buttonBack : Button

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        buttonBack = findViewById(R.id.back_button)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        firestoreHandler = FirestoreHandler(FirebaseFirestore.getInstance())

        mealAdapter = MealInfoAdapter(emptyList())
        recyclerView.adapter = mealAdapter

        buttonBack.setOnClickListener {
            openActivityMain()
        }

        userId = intent.getStringExtra("uID") ?: ""
        if (userId.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        fetchMealInfo()
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
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
