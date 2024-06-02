package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikacjatestowa.R

class AddMealActivity : AppCompatActivity() {
    private lateinit var buttonBack : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        buttonBack = findViewById(R.id.button_backToMainFromMeal)
        buttonBack.setOnClickListener { openActivityMain() }
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

//@+id/editText_meal - wpisanie nazwy posiłku
//@+id/editTextNumber_WW - wpisanie g węglowodanów
//@+id/button_addmeal - dodanie posiłku