package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacjatestowa.R



class MainActivity : AppCompatActivity() {
    // widok z wykresem

    private lateinit var buttonMenu : Button// przycisk do otworzenia widoku user_info
    private lateinit var buttonInsulin : Button// przycisk do dodawania dawki insuliny
    private lateinit var buttonMeal : Button
    private lateinit var buttonGlucose : Button
    private var button6h : Button? = null
    private var button12h : Button? = null
    private var button24h : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // tu implementujemy widgety z ich Id
        buttonMenu = findViewById(R.id.button_menu)
        buttonInsulin = findViewById(R.id.button_insulin)
        buttonMeal = findViewById(R.id.button_meal)
        buttonGlucose = findViewById(R.id.button_glucose)
        button6h = findViewById(R.id.button_6h)
        button12h = findViewById(R.id.button_12h)
        button24h = findViewById(R.id.button_24h)

        //obsluga przycisku do zmieniania widoku na user info
        buttonMenu.setOnClickListener {
            openActivityUserInfo()
        }

        buttonInsulin.setOnClickListener{
            openActivityAddInsulin()
        }

        buttonGlucose.setOnClickListener {
            openActivityAddGlucose()
        }

        buttonMeal.setOnClickListener { openActivityAddMeal() }




    }

    //Metoda do otwierania UserInfo
    private fun openActivityUserInfo(){
        val intent = Intent(this, UserInfoActivity::class.java)
        startActivity(intent)
    }

    private fun openActivityAddInsulin(){
        val intent = Intent(this, AddInsulinActivity::class.java)
        startActivity(intent)
    }

    private fun openActivityAddGlucose(){
        val intent = Intent(this, AddGlucoseMeasurmentActivity::class.java)
        startActivity(intent)
    }

    private fun openActivityAddMeal(){
        val intent = Intent(this, AddMealActivity::class.java)
        startActivity(intent)
    }
}