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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.InsulinInfo
import naszeAktywnosci.adapter.InsulinAdapter
import naszeAktywnosci.MainActivity

class InsulinDataActivity : AppCompatActivity() {

    private lateinit var insulinRecyclerView: RecyclerView
    private lateinit var insulinAdapter: InsulinAdapter
    private lateinit var insulinList: ArrayList<InsulinInfo>
    private lateinit var buttonBack : Button
    private lateinit var userId: String

    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = intent.getStringExtra("uID") ?: ""
        Log.d("InsulinDataActivity", "Received userId: $userId")
        if (userId.isEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_rv_insulin)

        insulinRecyclerView = findViewById(R.id.recyclerView_insulin)

        insulinRecyclerView.layoutManager = LinearLayoutManager(this)
        insulinRecyclerView.setHasFixedSize(true)

        insulinList = arrayListOf()

        insulinAdapter = InsulinAdapter(insulinList, this, userId)
        insulinRecyclerView.adapter = insulinAdapter


        buttonBack = findViewById(R.id.backFromInsulinRV_button)
        buttonBack.setOnClickListener {
            openActivityMain()
        }
        EventChangeListener()
    }

    private fun EventChangeListener() {
        db.collection("insulin_info").document(userId).collection("insulin").orderBy("date", Query.Direction.DESCENDING).orderBy("time",
            Query.Direction.DESCENDING).
        addSnapshotListener(object  : EventListener<QuerySnapshot> {
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
                            insulinList.add(dc.document.toObject(InsulinInfo::class.java))
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val updatedInsulin = dc.document.toObject(InsulinInfo::class.java)
                            val index = insulinList.indexOfFirst { it.insulinId == updatedInsulin.insulinId }
                            if (index != -1) {
                                insulinList[index] = updatedInsulin
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            val removedInsulin = dc.document.toObject(InsulinInfo::class.java)
                            insulinList.removeAll { it.insulinId == removedInsulin.insulinId }
                        }
                    }
                }
                insulinAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }
}
