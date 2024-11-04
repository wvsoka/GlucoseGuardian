package naszeAktywnosci.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import naszeAktywnosci.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MealAdapter(private val mealList : ArrayList<MealInfo>, private val context: Context,
                  private val userId: String) : RecyclerView.Adapter<MealAdapter.MealViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.meal_item, parent, false)
        return  MealViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal : MealInfo = mealList[position]
        holder.mealName.text = meal.name
        holder.mealDate.text = meal.date
        holder.mealTime.text = meal.time
        holder.mealCarbo.text = meal.carbohydrates.toString()

        holder.editButton.setOnClickListener {
            openEditMealDialog(meal)
        }
    }

    private fun openEditMealDialog(meal: MealInfo) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_edit_meal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        val editMealName: EditText = dialog.findViewById(R.id.editText_meal_edit)
        val editCarbohydrates: EditText = dialog.findViewById(R.id.editTextNumber_WW_edit)
        val editMealDate: EditText = dialog.findViewById(R.id.editText_meal_date)
        val editMealTime: EditText = dialog.findViewById(R.id.editText_meal_time)
        val buttonSave: Button = dialog.findViewById(R.id.button_saveMeal)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancelMeal)
        val buttonDelete: Button = dialog.findViewById(R.id.button_deleteMeal)


        editMealName.setText(meal.name)
        editCarbohydrates.setText(meal.carbohydrates.toString())
        editMealDate.setText(meal.date)
        editMealTime.setText(meal.time)

        buttonSave.setOnClickListener {
            val updatedMealName = editMealName.text.toString()
            val updatedCarbohydrates = editCarbohydrates.text.toString().toDoubleOrNull() ?: 0.0
            val updatedDate = editMealDate.text.toString()
            val updatedTime = editMealTime.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val mealInfo = MealInfo(
                        name = updatedMealName,
                        date = updatedDate,
                        time = updatedTime,
                        carbohydrates = updatedCarbohydrates,
                        mealId = meal.mealId
                    )
                    FirestoreHandler(Firebase.firestore).updateMealInfo(userId, meal.mealId, mealInfo)
                    Toast.makeText(context, "Meal updated successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to update meal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    FirestoreHandler(Firebase.firestore).deleteMealInfo(userId, meal.mealId)
                    Toast.makeText(context, "Meal deleted successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to delete meal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH-mm", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val mealName : TextView = itemView.findViewById(R.id.mealName)
            val mealDate : TextView = itemView.findViewById(R.id.mealDate)
            val mealTime : TextView = itemView.findViewById(R.id.mealTime)
            val mealCarbo : TextView = itemView.findViewById(R.id.mealCarbo)
            val editButton: Button = itemView.findViewById(R.id.button_editMealItem)
    }

}