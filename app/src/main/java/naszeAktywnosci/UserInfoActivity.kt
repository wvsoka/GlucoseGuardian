package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacjatestowa.R

class UserInfoActivity : AppCompatActivity() {

    private lateinit var buttonBack: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        buttonBack = findViewById(R.id.button_backToMain)

        buttonBack.setOnClickListener {
            openActivityMain()
        }

        editTextName = findViewById(R.id.editText_name)
        editTextDate = findViewById(R.id.editText_date)
    }

    //Metoda powrotu do main activity
    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}