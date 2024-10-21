package naszeAktywnosci.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.InsulinInfo

class InsulinAdapter(private val insulinList: ArrayList<InsulinInfo>) :
    RecyclerView.Adapter<InsulinAdapter.InsulinViewHolder>() {

    class InsulinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val insulinDate: TextView = itemView.findViewById(R.id.insulinDate)
        val insulinTime: TextView = itemView.findViewById(R.id.insuliunTime)
        val insulinDose: TextView = itemView.findViewById(R.id.insulinName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsulinViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.insulin_item, parent, false)
        return InsulinViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InsulinViewHolder, position: Int) {
        val insulin = insulinList[position]

        holder.insulinDate.text = insulin.date
        holder.insulinTime.text = insulin.time
        holder.insulinDose.text = insulin.measurment.toString()
    }

    override fun getItemCount(): Int {
        return insulinList.size
    }
}
