package naszeAktywnosci.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.MealInfo

class MealAdapter(private val mealList : ArrayList<MealInfo>) : RecyclerView.Adapter<MealAdapter.MealViewHolder>(){
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
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val mealName : TextView = itemView.findViewById(R.id.mealName)
            val mealDate : TextView = itemView.findViewById(R.id.mealDate)
            val mealTime : TextView = itemView.findViewById(R.id.mealTime)
            val mealCarbo : TextView = itemView.findViewById(R.id.mealCarbo)
    }

}