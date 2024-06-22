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
import naszeAktywnosci.FirebaseData.UserMeasurments
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddGlucoseMeasurmentActivity : AppCompatActivity() {

    private lateinit var buttonBack : Button
    private lateinit var buttonAddInsert : Button
    //private lateinit var buttonAddFile : Button
    private lateinit var numberInsert : EditText

    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_glucose_measurment)

        buttonBack = findViewById(R.id.button_backToMainFromGlucose)
        buttonBack.setOnClickListener { openActivityMain() }

        //buttonAddFile = findViewById(R.id.button_addfile)
        buttonAddInsert = findViewById(R.id.button_addinsert)
        numberInsert = findViewById(R.id.editTextNumber_glucose)

        val intent = intent
        userId = intent.getStringExtra("uID") ?: ""

        buttonAddInsert.setOnClickListener { addMeasurement() }
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }


    // https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html - tutaj formaty dat i godzin
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH-mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun addMeasurement() {
        val measurementValue = numberInsert.text.toString().toDoubleOrNull() ?: 0.0

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val measurementId = dbOperations.generateId(db, "user_measurements")
                val measurement = UserMeasurments(
                    measurment = measurementValue,
                    date = getCurrentDate(),
                    time = getCurrentTime(),
                    measurmentID = measurementId
                )
                dbOperations.addMeasurements(userId, measurement)
                Toast.makeText(this@AddGlucoseMeasurmentActivity, "Measurement added successfully", Toast.LENGTH_SHORT).show()
                openActivityMain()
            } catch (e: Exception) {
                Toast.makeText(this@AddGlucoseMeasurmentActivity, "Failed to add measurement", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
