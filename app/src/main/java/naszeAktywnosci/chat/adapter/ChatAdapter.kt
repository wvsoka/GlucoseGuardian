package naszeAktywnosci.chat.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import naszeAktywnosci.FirebaseData.Message
import naszeAktywnosci.chat.fragments.ChatFragment
import naszeAktywnosci.chat.fragments.ConversationFragment

class ChatAdapter(private var chats: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        val db = FirebaseFirestore.getInstance()
        val chatPartnerId = if (chat.senderId == FirebaseAuth.getInstance().currentUser?.email) chat.receiverId else chat.senderId

        db.collection("users").document(chatPartnerId).get()
            .addOnSuccessListener { document ->
                val username = document.getString("name") ?: "Unknown User"
                holder.usernameTextView.text = username
            }
            .addOnFailureListener { exception ->
                Log.e("ChatAdapter", "Failed to fetch user data: $exception")
                holder.usernameTextView.text = "Unknown User"
            }

        holder.lastMessageTextView.text = chat.text

        holder.itemView.setOnClickListener {
            Log.d("ChatAdapter", "Item clicked! ReceiverId: $chatPartnerId")

            val chatFragment = ConversationFragment()
            val bundle = Bundle()
            bundle.putString("receiverId", chatPartnerId)
            chatFragment.arguments = bundle

            val fragmentContainer = (it.context as AppCompatActivity).findViewById<FrameLayout>(R.id.fragment_container_chat)
            if (fragmentContainer.visibility != View.VISIBLE) {

                fragmentContainer.visibility = View.VISIBLE
            }
            Log.d("ConversationFragment", "Receiver ID: $chatPartnerId")
            val viewPager = (it.context as AppCompatActivity).findViewById<ViewPager>(R.id.view_pager_chat)
            viewPager.visibility = View.GONE
            Log.d("ChatAdapter", "Before fragment transaction")
            (it.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_chat, chatFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
            Log.d("ChatAdapter", "Fragment transaction committed")


            Log.d("ChatAdapter", "Fragment transaction started.")
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun updateData(newChats: List<Message>) {
        chats.clear()
        chats.addAll(newChats)
        notifyDataSetChanged()
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val lastMessageTextView: TextView = view.findViewById(R.id.lastMessageTextView)
    }
}