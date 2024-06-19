package naszeAktywnosci

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.MealInfo

class MealAdapter(private val mealList : ArrayList<MealInfo>) : RecyclerView.Adapter<MealAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return  MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MealAdapter.MyViewHolder, position: Int) {
        val meal : MealInfo = mealList[position]
        holder.mealName.text = meal.name
        holder.mealDate.text = meal.date
        holder.mealTime.text = meal.time
        holder.mealCarbo.text = meal.carbohydrates.toString()
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val mealName : TextView = itemView.findViewById(R.id.mealName)
            val mealDate : TextView = itemView.findViewById(R.id.mealDate)
            val mealTime : TextView = itemView.findViewById(R.id.mealTime)
            val mealCarbo : TextView = itemView.findViewById(R.id.mealCarbo)
    }

}