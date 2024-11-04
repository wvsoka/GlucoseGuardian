package naszeAktywnosci.dataActivity

import naszeAktywnosci.adapter.GlucoseMeasurementAdapter
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
import naszeAktywnosci.FirebaseData.UserMeasurments
import naszeAktywnosci.MainActivity

class GlucoseMeasurementDataActivity : AppCompatActivity() {

    private lateinit var measurementsRecyclerView: RecyclerView
    private lateinit var glucoseAdapter: GlucoseMeasurementAdapter
    private lateinit var measurementsList: ArrayList<UserMeasurments>
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
        setContentView(R.layout.activity_rv_measurements)

        buttonBack = findViewById(R.id.backFromMeasurementsRV_button)

        measurementsRecyclerView = findViewById(R.id.recyclerView_measurements)

        measurementsRecyclerView.layoutManager = LinearLayoutManager(this)
        measurementsRecyclerView.setHasFixedSize(true)

        measurementsList = arrayListOf()

        glucoseAdapter = GlucoseMeasurementAdapter(measurementsList, this, userId)
        measurementsRecyclerView.adapter = glucoseAdapter


        buttonBack.setOnClickListener {
            openActivityMain()
        }


        EventChangeListener()
    }

    private fun EventChangeListener() {
        db.collection("user_measurements").document(userId).collection("measurements").orderBy("date",
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
                            val measurement =  dc.document.toObject(UserMeasurments::class.java)
                            measurement.measurmentID = dc.document.id
                            measurementsList.add(measurement)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val updatedMeasurement = dc.document.toObject(UserMeasurments::class.java)
                            val index = measurementsList.indexOfFirst { it.measurmentID == updatedMeasurement.measurmentID }
                            if (index != -1) {
                                measurementsList[index] = updatedMeasurement
                                glucoseAdapter.notifyItemChanged(index)
                            } else {
                                Log.e("Error", "error with update")
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            val removedMeasurement = dc.document.toObject(UserMeasurments::class.java)
                            measurementsList.removeAll { it.measurmentID == removedMeasurement.measurmentID }
                        }
                    }
                }
                glucoseAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }
}
