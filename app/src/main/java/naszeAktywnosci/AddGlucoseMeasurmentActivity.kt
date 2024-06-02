package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikacjatestowa.R

class AddGlucoseMeasurmentActivity : AppCompatActivity() {

    private lateinit var buttonBack : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_glucose_measurment)

        buttonBack = findViewById(R.id.button_backToMainFromGlucose)
        buttonBack.setOnClickListener { openActivityMain() }
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

//@+id/editTextNumber_insert - wpisanie pomiaru glukozy
//@+id/button_addinsert - dodanie pojedynczego pomiaru
//@+id/button_addfile - dodanie pliku z pomiarami