package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikacjatestowa.R

class AddInsulinActivity : AppCompatActivity() {

    private lateinit var buttonBack : Button
    private lateinit var buttonAddInsulin: Button
    private lateinit var numberInsulin : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_insulin)

        buttonBack = findViewById(R.id.button_backToMainFromInsulin)

        buttonBack.setOnClickListener{
            openActivityMain()
        }

        buttonAddInsulin = findViewById(R.id.button_addinsulin)
        numberInsulin = findViewById(R.id.editTextNumber_insulin)

    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

//@+id/editTextNumber_insulin - wpisanie podanej dawki insuliny
//@+id/button_addinsulin - dodanie tej dawki