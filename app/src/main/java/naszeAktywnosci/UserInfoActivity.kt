package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacjatestowa.R

class UserInfoActivity : AppCompatActivity() {

    private lateinit var buttonBack : Button // do cofania do widoku glownego


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        buttonBack = findViewById(R.id.button_backToMain)

        buttonBack.setOnClickListener {
            openActivityMain()
        }

    }

    //Metoda powrotu do main activity
    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}