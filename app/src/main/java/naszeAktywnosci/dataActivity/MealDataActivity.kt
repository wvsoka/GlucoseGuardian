package naszeAktywnosci.dataActivity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.MealInfo
import naszeAktywnosci.MainActivity
import naszeAktywnosci.adapter.MealAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        userId = intent.getStringExtra("uID") ?: ""
        if (userId.isEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_rv_meals)

        buttonBack = findViewById(R.id.backFromMealsRV_button)

        mealRecyclerView = findViewById(R.id.recyclerView_meals)

        mealRecyclerView.layoutManager = LinearLayoutManager(this)
        mealRecyclerView.setHasFixedSize(true)

        mealList = arrayListOf()


        mealAdapter = MealAdapter(mealList, this, userId)
        mealRecyclerView.adapter = mealAdapter


        buttonBack.setOnClickListener {
            openActivityMain()
        }


        EventChangeListener()
    }

    private fun EventChangeListener() {
        db.collection("meal_info").document(userId).collection("meals").orderBy("date",Query.Direction.DESCENDING).
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
                                    val meal = dc.document.toObject(MealInfo::class.java)
                                    meal.mealId = dc.document.id
                                    mealList.add(meal)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val updatedMeal = dc.document.toObject(MealInfo::class.java)
                                    val index = mealList.indexOfFirst { it.mealId == updatedMeal.mealId }
                                    if (index != -1) {
                                        mealList[index] = updatedMeal
                                        mealAdapter.notifyItemChanged(index)
                                    } else {
                                        Log.e("Error", "error with update")
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
