package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacjatestowa.R
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.User

class UserInfoActivity : AppCompatActivity() {

    private lateinit var buttonBack: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextDate: EditText
    private lateinit var weight : EditText
    private lateinit var height : EditText
    private lateinit var activity : SeekBar
    private lateinit var pregnancy : Switch
    private lateinit var breastfeeding : Switch
    private lateinit var iw : EditText
    private lateinit var hypoglycemia : EditText
    private lateinit var hyperglycemia : EditText
    private lateinit var buttonSave: Button


    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        buttonBack = findViewById(R.id.button_backToMain)

        buttonBack.setOnClickListener {
            openActivityMain()
        }

        editTextName = findViewById(R.id.editText_name)
        editTextDate = findViewById(R.id.editText_date)
        weight = findViewById(R.id.editTextNumberDecimal_weight)
        height = findViewById(R.id.editTextNumberDecimal_height)
        activity = findViewById(R.id.seekBar_activity)
        pregnancy = findViewById(R.id.switch_pregnancy)
        breastfeeding = findViewById(R.id.switch_feeding)
        iw = findViewById(R.id.editTextNumber_IW)
        hypoglycemia = findViewById(R.id.editTextNumberDecimal3)
        hyperglycemia = findViewById(R.id.editTextNumber2)
        buttonSave = findViewById(R.id.button_save)

        buttonSave.setOnClickListener {
            saveUserData()
        }
        userId = intent.getStringExtra("uID") ?: ""
        if (userId.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (userId.isNotEmpty()) {
            loadUserData()
        }
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun loadUserData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    editTextName.setText(user.name)
                    editTextDate.setText(user.dateOfBirth)
                    iw.setText(user.insulinWW.toString())
                    hypoglycemia.setText(user.hypoglycaemia.toString())
                    hyperglycemia.setText(user.hyperglycaemia.toString())
                    weight.setText(user.weight.toString())
                    height.setText(user.height.toString())
                }
                Toast.makeText(
                    this@UserInfoActivity,
                    "User info saved successfully.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@UserInfoActivity,
                    "Failed to load user data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveUserData() {
        val name = editTextName.text.toString()
        val dateOfBirth = editTextDate.text.toString()
        val insulinWW = iw.text.toString().toDoubleOrNull() ?: 0.0
        val hypoglycaemia = hypoglycemia.text.toString().toDoubleOrNull() ?: 0.0
        val hyperglycaemia = hyperglycemia.text.toString().toDoubleOrNull() ?: 0.0
        val height = height.text.toString().toDoubleOrNull() ?: 0.0
        val weight = weight.text.toString().toDoubleOrNull() ?: 0.0

        val updatedUser = User(
            name = name,
            dateOfBirth = dateOfBirth,
            insulinWW = insulinWW,
            hypoglycaemia = hypoglycaemia,
            hyperglycaemia = hyperglycaemia,
            height = height,
            weight = weight,
            userId = userId
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                dbOperations.updateUser(userId, updatedUser)
                Toast.makeText(this@UserInfoActivity, "User data saved successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@UserInfoActivity, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

}