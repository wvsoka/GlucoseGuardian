package naszeAktywnosci

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PanZoom
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import com.example.aplikacjatestowa.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.InsulinInfo
import naszeAktywnosci.FirebaseData.MealInfo
import naszeAktywnosci.FirebaseData.UserMeasurments
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale



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
    private lateinit var plot: XYPlot
    private lateinit var userId: String
    private val db = Firebase.firestore
    private val dbOperations = FirestoreHandler(db)

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
        plot = findViewById(R.id.plot)

        // Pobierz intent, który uruchomił tę aktywność
        val intent = intent

        // Sprawdź, czy intent zawiera dodatkowe dane o nazwie "uID"
        userId = intent.getStringExtra("uID").toString()

        //obsluga przycisku do zmieniania widoku na user info
        buttonMenu.setOnClickListener {
            openActivityUserInfo()
        }

        buttonInsulin.setOnClickListener{
            openDialogAddInsulin()
        }

        buttonGlucose.setOnClickListener {
            openDialogAddGlucose()
        }

        buttonMeal.setOnClickListener { openDialogAddMeal() }

        buttonNotification.setOnClickListener { openScheduleNotificationsActivity() }


        fetchMeasurements()

    }

    //Metoda do otwierania UserInfo
    private fun openActivityUserInfo(){
        val intent = Intent(this, UserInfoActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun openDialogAddInsulin() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_add_insulin)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val buttonAddInsulin: Button = dialog.findViewById(R.id.button_addinsulin)
        val numberInsulin: EditText = dialog.findViewById(R.id.editTextNumber_insulin)
        val closeButton: Button = dialog.findViewById(R.id.button_backToMainFromInsulin)

        closeButton.setOnClickListener {
            val intent=Intent(this, MainActivity::class.java)
            intent.putExtra("uID", userId)
            dialog.dismiss()
        }
        buttonAddInsulin.setOnClickListener {
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
                    Toast.makeText(this@MainActivity, "Insulin dose added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Failed to add insulin dose", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun openDialogAddGlucose() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_add_glucose_measurment)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val closeButton: Button = dialog.findViewById(R.id.button_backToMainFromGlucose)
        val buttonAddInsert: Button = dialog.findViewById(R.id.button_addinsert)
        val numberInsert: EditText = dialog.findViewById(R.id.editTextNumber_glucose)

        closeButton.setOnClickListener {
            val intent=Intent(this, MainActivity::class.java)
            intent.putExtra("uID", userId)
            dialog.dismiss() }
        buttonAddInsert.setOnClickListener {
            val measurementValue = numberInsert.text.toString().toDoubleOrNull() ?: 0.0

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val firestoreHandler = FirestoreHandler(FirebaseFirestore.getInstance())
                    val measurementId = dbOperations.generateId(db, "user_measurements")
                    val measurement = UserMeasurments(
                        measurment = measurementValue,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        measurmentID = measurementId
                    )
                    firestoreHandler.addMeasurements(userId, measurement)
                    Toast.makeText(this@MainActivity, "Measurement added successfully", Toast.LENGTH_SHORT).show()
                    fetchMeasurements() // Refresh the plot
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Failed to add measurement", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH-mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun openDialogAddMeal() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_add_meal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val buttonAddMeal: Button = dialog.findViewById(R.id.button_addmeal)
        val meal: EditText = dialog.findViewById(R.id.editText_meal)
        val numberWW: EditText = dialog.findViewById(R.id.editTextNumber_WW)
        val closeButton: Button = dialog.findViewById(R.id.button_backToMainFromMeal)
        val buttonAllMeals: Button = dialog.findViewById(R.id.button_toInfoMeal)

        closeButton.setOnClickListener {
            val intent=Intent(this, MainActivity::class.java)
            intent.putExtra("uID", userId)
            dialog.dismiss() }
        buttonAddMeal.setOnClickListener {
            val mealName = meal.text.toString()
            val carbohydrates = numberWW.text.toString().toDoubleOrNull() ?: 0.0

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val mealId = dbOperations.generateId(db, "meal_info")
                    val mealInfo = MealInfo(
                        name = mealName,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        carbohydrates = carbohydrates,
                        mealId = mealId
                    )
                    dbOperations.addMealInfo(userId, mealInfo)
                    Toast.makeText(this@MainActivity, "Meal added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Failed to add meal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonAllMeals.setOnClickListener {
            val intent = Intent(this, MealDataActivity::class.java)
            intent.putExtra("uID", userId)
            startActivity(intent)
        }

        dialog.show()
    }

    private fun openScheduleNotificationsActivity(){
        val intent = Intent(this, ScheduleNotificationsActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }

    private fun fetchMeasurements() {
        val firestoreHandler = FirestoreHandler(FirebaseFirestore.getInstance())
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val measurements = firestoreHandler.getMeasurements(userId)
                val todayMeasurements = measurements.filter { it.date == getCurrentDate() }
                updatePlot(todayMeasurements)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching measurements: $e")
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun updatePlot(measurements: List<UserMeasurments>) {
        if (measurements.isEmpty()) {
            Log.d(TAG, "No measurements for today")
            return
        }

        val sortedMeasurements = measurements.sortedBy { it.time }

        val domainLabels = sortedMeasurements.map { it.time }.toTypedArray()
        val seriesValues = sortedMeasurements.map { it.measurment }.toTypedArray()

        val series1: XYSeries = SimpleXYSeries(listOf(*seriesValues), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Today's Measurements")
        val series1Format = LineAndPointFormatter(Color.BLUE, Color.BLACK, null, null)

        if (seriesValues.size >= 3) {
            series1Format.setInterpolationParams(CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal))
        }
        plot.clear()
        plot.addSeries(series1, series1Format)
        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(
                obj: Any?,
                toAppendTo: StringBuffer?,
                pos: FieldPosition?
            ): StringBuffer {
                val i = Math.round((obj as Number).toFloat())
                return toAppendTo!!.append(domainLabels[i.toInt()])
            }

            override fun parseObject(source: String?, pos: ParsePosition?): Any {
                throw UnsupportedOperationException("parseObject not supported")
            }
        }
        plot.redraw()
    }

    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }



}