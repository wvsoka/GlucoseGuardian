package naszeAktywnosci

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PanZoom
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import com.example.aplikacjatestowa.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler
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
            openActivityAddInsulin()
        }

        buttonGlucose.setOnClickListener {
            openActivityAddGlucose()
        }

        buttonMeal.setOnClickListener { openActivityAddMeal() }

        buttonNotification.setOnClickListener { openScheduleNotificationsActivity() }


        fetchMeasurements()

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

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_add_insulin)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val closeButton: Button = dialog.findViewById(R.id.button_backToMainFromInsulin)
        closeButton.setOnClickListener { dialog.dismiss() }

        //startActivity(intent)
        dialog.show()
    }

    private fun openActivityAddGlucose() {
        val intent = Intent(this, AddGlucoseMeasurmentActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_add_glucose_measurment)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val closeButton: Button = dialog.findViewById(R.id.button_backToMainFromGlucose)
        closeButton.setOnClickListener { dialog.dismiss() }

        //startActivity(intent)
        dialog.show()
    }

    private fun openActivityAddMeal(){
        val intent = Intent(this, AddMealActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_add_meal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val closeButton: Button = dialog.findViewById(R.id.button_backToMainFromMeal)
        closeButton.setOnClickListener { dialog.dismiss() }

        //startActivity(intent)
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

}