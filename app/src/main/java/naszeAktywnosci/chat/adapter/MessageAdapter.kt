package naszeAktywnosci.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import naszeAktywnosci.FirebaseData.Message

class MessageAdapter(private var messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.text
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
    }
}