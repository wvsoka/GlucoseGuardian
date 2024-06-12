package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacjatestowa.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.InsulinInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddInsulinActivity : AppCompatActivity() {

    private lateinit var buttonBack : Button
    private lateinit var buttonAddInsulin: Button
    private lateinit var numberInsulin : EditText

    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_insulin)

        buttonBack = findViewById(R.id.button_backToMainFromInsulin)

        buttonBack.setOnClickListener{
            openActivityMain()
        }

        buttonAddInsulin = findViewById(R.id.button_addinsulin)
        numberInsulin = findViewById(R.id.editTextNumber_insulin)

        buttonAddInsulin.setOnClickListener {
            addInsulin()
        }

        userId = intent.getStringExtra("uID") ?: ""
        if (userId.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH-mm", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun addInsulin() {
        val insulinDose = numberInsulin.text.toString().toDoubleOrNull() ?: 0.0

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val insulinId = dbOperations.generateId(db, "insulin_info")
                val insulinInfo = InsulinInfo(
                    date = getCurrentDate(),
                    time = getCurrentTime(),
                    measurment = insulinDose,
                    insulinId = insulinId
                )
                dbOperations.addInsulinInfo(userId, insulinInfo)
                Toast.makeText(this@AddInsulinActivity, "Insulin dose added successfully", Toast.LENGTH_SHORT).show()
                openActivityMain()
            } catch (e: Exception) {
                Toast.makeText(this@AddInsulinActivity, "Failed to add insulin dose", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
