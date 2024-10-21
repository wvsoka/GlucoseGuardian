package naszeAktywnosci.dataActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.MealInfo
import naszeAktywnosci.MainActivity
import naszeAktywnosci.adapter.MealAdapter

class MealDataActivity : AppCompatActivity() {

    private lateinit var mealRecyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter
    private lateinit var mealList : ArrayList<MealInfo>
    private lateinit var buttonBack : Button
    private lateinit var userId: String

    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv_meals)

        buttonBack = findViewById(R.id.backFromMealsRV_button)

        mealRecyclerView = findViewById(R.id.recyclerView_meals)

        mealRecyclerView.layoutManager = LinearLayoutManager(this)
        mealRecyclerView.setHasFixedSize(true)

        mealList = arrayListOf()

        mealAdapter = MealAdapter(mealList)
        mealRecyclerView.adapter = mealAdapter


        buttonBack.setOnClickListener {
            openActivityMain()
        }
        userId = intent.getStringExtra("uID") ?: ""
        if (userId.isEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        EventChangeListener()
    }

    private fun EventChangeListener() {
        db.collection("meal_info").document(userId).collection("meals").
                addSnapshotListener(object  : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore error", error.message.toString())
                            return
                        }
                        for (dc : DocumentChange in value?.documentChanges!!){
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    mealList.add(dc.document.toObject(MealInfo::class.java))
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val updatedMeal = dc.document.toObject(MealInfo::class.java)
                                    val index = mealList.indexOfFirst { it.mealId == updatedMeal.mealId }
                                    if (index != -1) {
                                        mealList[index] = updatedMeal
                                    }
                                }
                                DocumentChange.Type.REMOVED -> {
                                    val removedMeal = dc.document.toObject(MealInfo::class.java)
                                    mealList.removeAll { it.mealId == removedMeal.mealId }
                                }
                            }
                        }
                        mealAdapter.notifyDataSetChanged()
                    }
                })
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }
}
