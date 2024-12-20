package naszeAktywnosci.chat.adapter

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private var messages: MutableList<Message>, private val currentUserId: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.text

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateText = dateFormat.format(Date(message.timestamp))
        holder.timestampTextView.text = dateText

        if (message.senderId == currentUserId) {
            holder.container.gravity = Gravity.END
            holder.messageTextView.setBackgroundResource(R.drawable.sent_message_background)
        } else {
            holder.container.gravity = Gravity.START
            holder.messageTextView.setBackgroundResource(R.drawable.received_message_background)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun updateData(newMessages: List<Message>) {
        Log.d("MessageAdapter", "Updating adapter with ${newMessages.size} messages.")
        messages.clear()
        messages.addAll(newMessages)
        Log.d("MessageAdapter", "Messages after update: ${messages.size}")
        notifyDataSetChanged()
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.textViewMessage)
        val timestampTextView: TextView = view.findViewById(R.id.textViewTimestamp)
        val container: LinearLayout = view.findViewById(R.id.container)
    }
}