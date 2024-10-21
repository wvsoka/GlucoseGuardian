package naszeAktywnosci.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.UserMeasurments

class GlucoseMeasurementAdapter(private var measurementsList: ArrayList<UserMeasurments>) :
    RecyclerView.Adapter<GlucoseMeasurementAdapter.MeasurementViewHolder>() {

    inner class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val glucoseMeasurement: TextView = itemView.findViewById(R.id.glucoseMeasurement)
        val measurementDate: TextView = itemView.findViewById(R.id.measurementDate)
        val measurementTime: TextView = itemView.findViewById(R.id.measurementTime)
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
    }

    override fun getItemCount(): Int {
        return measurementsList.size
    }
}
