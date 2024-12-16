package naszeAktywnosci.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.Message
import naszeAktywnosci.FirebaseData.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserAdapter(
    private var usersWithMessages: List<Pair<User, Message?>>,
    private val onUserSelected: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val (user, lastMessage) = usersWithMessages[position]
        if (lastMessage != null) {
            holder.bind(user, lastMessage)
        }
    }

    override fun getItemCount(): Int = usersWithMessages.size

    fun updateData(newData: List<Pair<User, Message?>>) {
        Log.d("UserAdapter", "Updating adapter data: ${newData.size} items")
        usersWithMessages = newData
        notifyDataSetChanged()
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        private val lastMessageTextView = view.findViewById<TextView>(R.id.lastMessageTextView)

        fun bind(user: User, message: Message) {
            try {
                usernameTextView.text = user.name
                val date = formatTimestamp(message.timestamp)
                lastMessageTextView.text = "${message.text}\nSent: $date"

                itemView.setOnClickListener {
                    Log.d("UserAdapter", "User selected: ${user.name}")
                    onUserSelected(user)
                }
            } catch (e: Exception) {
                Log.e("UserAdapter", "Error binding data: ${e.message}", e)
            }
        }

        private fun formatTimestamp(timestamp: Long?): String {
            if (timestamp == null) return "Unknown date"
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
