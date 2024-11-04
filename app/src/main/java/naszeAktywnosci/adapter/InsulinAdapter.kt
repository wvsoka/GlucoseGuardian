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
import naszeAktywnosci.FirebaseData.InsulinInfo

class InsulinAdapter(private val insulinList: ArrayList<InsulinInfo>, private val context: Context,
                     private val userId: String) :
    RecyclerView.Adapter<InsulinAdapter.InsulinViewHolder>() {

    class InsulinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val insulinDate: TextView = itemView.findViewById(R.id.insulinDate)
        val insulinTime: TextView = itemView.findViewById(R.id.insulinTime)
        val insulinMeasurment: TextView = itemView.findViewById(R.id.insulinName)
        val editButton : Button = itemView.findViewById(R.id.button_editInsulinItem)
    }

    private fun openEditInsulinDialog(insulin: InsulinInfo) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_edit_insulin)
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        val editDose: EditText = dialog.findViewById(R.id.editTextNumber_dose_edit)
        val editInsulinDate: EditText = dialog.findViewById(R.id.editText_insulin_date)
        val editInsulinTime: EditText = dialog.findViewById(R.id.editText_insulin_time)
        val buttonSave: Button = dialog.findViewById(R.id.button_saveInsulin)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancelInsulin)
        val buttonDelete: Button = dialog.findViewById(R.id.button_deleteInsulin)



        editDose.setText(insulin.measurment.toString())
        editInsulinDate.setText(insulin.date)
        editInsulinTime.setText(insulin.time)

        buttonSave.setOnClickListener {
            val updatedDose = editDose.text.toString().toDoubleOrNull() ?: 0.0
            val updatedDate = editInsulinDate.text.toString()
            val updatedTime = editInsulinTime.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val insulinInfo = InsulinInfo(
                        date = updatedDate,
                        time = updatedTime,
                        measurment = updatedDose,
                        insulinId = insulin.insulinId
                    )
                    FirestoreHandler(Firebase.firestore).updateInsulinInfo(userId, insulin.insulinId, insulinInfo)
                    Toast.makeText(context, "Insulin updated successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to update insulin", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    FirestoreHandler(Firebase.firestore).deleteInsulinInfo(userId, insulin.insulinId)
                    Toast.makeText(context, "Insulin deleted successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to delete insulin", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsulinViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.insulin_item, parent, false)
        return InsulinViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InsulinViewHolder, position: Int) {
        val insulin = insulinList[position]

        holder.insulinDate.text = insulin.date
        holder.insulinTime.text = insulin.time
        holder.insulinMeasurment.text = insulin.measurment.toString()

        holder.editButton.setOnClickListener {
            openEditInsulinDialog(insulin)
        }
    }

    override fun getItemCount(): Int {
        return insulinList.size
    }
}
