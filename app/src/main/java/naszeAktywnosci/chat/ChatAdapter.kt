package naszeAktywnosci.chat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import naszeAktywnosci.FirebaseData.Message

class ChatAdapter(
    private val messages: List<Message>,
    private val currentUser: String
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val isCurrentUser = message.user1 == currentUser || message.user2 == currentUser
        holder.messageTextView.text = if (isCurrentUser) {
            "Ty: ${message.content} (o ${message.time})"
        } else {
            "Inny: ${message.content} (o ${message.time})"
        }
    }

    override fun getItemCount(): Int = messages.size
}