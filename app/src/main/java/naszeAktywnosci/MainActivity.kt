package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacjatestowa.R
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import naszeAktywnosci.FirebaseData.FirestoreHandler


class MainActivity : AppCompatActivity() {
    // widok z wykresem

    private lateinit var buttonMenu : Button// przycisk do otworzenia widoku user_info
    private lateinit var buttonInsulin : Button// przycisk do dodawania dawki insuliny
    private lateinit var buttonMeal : Button
    private lateinit var buttonGlucose : Button
    private var button6h : Button? = null
    private var button12h : Button? = null
    private var button24h : Button? = null
    private lateinit var buttonNotification : Button

    private lateinit var userId: String

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
        buttonNotification = findViewById(R.id.button_notifications)


        // Pobierz intent, który uruchomił tę aktywność
        val intent = intent

        // Sprawdź, czy intent zawiera dodatkowe dane o nazwie "uID"
        userId = intent.getStringExtra("uID").toString()

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

        buttonNotification.setOnClickListener { openScheduleNotificationsActivity() }



    }

    //Metoda do otwierania UserInfo
    private fun openActivityUserInfo(){
        val intent = Intent(this, UserInfoActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun openActivityAddInsulin(){
        val intent = Intent(this, AddInsulinActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun openActivityAddGlucose(){
        val intent = Intent(this, AddGlucoseMeasurmentActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun openActivityAddMeal(){
        val intent = Intent(this, AddMealActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun openScheduleNotificationsActivity(){
        val intent = Intent(this, ScheduleNotificationsActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }
}