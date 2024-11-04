package naszeAktywnosci.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler
import naszeAktywnosci.FirebaseData.MealInfo
import naszeAktywnosci.FirebaseData.UserMeasurments

class GlucoseMeasurementAdapter(private var measurementsList: ArrayList<UserMeasurments>, private val context: Context,
                                private val userId: String) :
    RecyclerView.Adapter<GlucoseMeasurementAdapter.MeasurementViewHolder>() {

    inner class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val glucoseMeasurement: TextView = itemView.findViewById(R.id.glucoseMeasurement)
        val measurementDate: TextView = itemView.findViewById(R.id.measurementDate)
        val measurementTime: TextView = itemView.findViewById(R.id.measurementTime)
        val editButton : Button = itemView.findViewById(R.id.button_editMeasurementItem)
    }

    private fun openEditMeasurementDialog(measurement: UserMeasurments, adapter: GlucoseMeasurementAdapter) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_edit_measurement)
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        val editMeasurement: EditText = dialog.findViewById(R.id.editTextNumber_measurement_edit)
        val editMeasurementDate: EditText = dialog.findViewById(R.id.editText_measurement_date)
        val editMeasurementTime: EditText = dialog.findViewById(R.id.editText_measurement_time)
        val buttonSave: Button = dialog.findViewById(R.id.button_saveMeasurement)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancelMeasurement)
        val buttonDelete: Button = dialog.findViewById(R.id.button_deleteMeasurement)



        editMeasurement.setText(measurement.measurment.toString())
        editMeasurementDate.setText(measurement.date)
        editMeasurementTime.setText(measurement.time)

        buttonSave.setOnClickListener {
            val updatedMeasurement = editMeasurement.text.toString().toDoubleOrNull() ?: 0.0
            val updatedDate = editMeasurementDate.text.toString()
            val updatedTime = editMeasurementTime.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userMeasurments = UserMeasurments(
                        date = updatedDate,
                        time = updatedTime,
                        measurment = updatedMeasurement,
                        measurmentID = measurement.measurmentID
                    )
                    FirestoreHandler(Firebase.firestore).updateMeasurement(userId, measurement.measurmentID, userMeasurments)
                    adapter.updateMeasurement(measurement)
                    Toast.makeText(context, "Measurement updated successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to update measurement", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    FirestoreHandler(Firebase.firestore).deleteMeasurements(userId, measurement.measurmentID)
                    Toast.makeText(context, "Measurement deleted successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to delete measurement", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun updateMeasurement(updatedMeasurement: UserMeasurments) {
        val index = measurementsList.indexOfFirst { it.measurmentID == updatedMeasurement.measurmentID }
        if (index != -1) {
            measurementsList[index] = updatedMeasurement
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.measurement_item, parent, false)
        return MeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val measurements = measurementsList[position]
        holder.measurementDate.text = measurements.date
        holder.measurementTime.text = measurements.time
        holder.glucoseMeasurement.text = measurements.measurment.toString()

        holder.editButton.setOnClickListener {
            openEditMeasurementDialog(measurements, this)
        }
    }

    override fun getItemCount(): Int {
        return measurementsList.size
    }
}
