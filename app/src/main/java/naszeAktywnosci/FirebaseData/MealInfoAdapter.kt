package naszeAktywnosci.FirebaseData

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R

class MealInfoAdapter(private var mealList: List<MealInfo>) : RecyclerView.Adapter<MealInfoAdapter.MealViewHolder>() {

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealDate: TextView = itemView.findViewById(R.id.mealDate)
        val mealCarbohydrates: TextView = itemView.findViewById(R.id.mealCarbohydrates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val currentMeal = mealList[position]
        holder.mealName.text = currentMeal.name
        holder.mealDate.text = currentMeal.date
        holder.mealCarbohydrates.text = "Carbohydrates: ${currentMeal.carbohydrates}"
    }

    override fun getItemCount() = mealList.size

    fun updateMealList(newMealList: List<MealInfo>) {
        mealList = newMealList
        notifyDataSetChanged()
    }
}
