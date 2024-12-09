package naszeAktywnosci.chat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import naszeAktywnosci.FirebaseData.Message
import naszeAktywnosci.FirebaseData.User
import naszeAktywnosci.chat.adapter.ChatAdapter

class ChatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private var chats: MutableList<Message> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerViewChats)
        chatAdapter = ChatAdapter(chats)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatAdapter
        Log.d("ChatFragment", "RecyclerView initialized and adapter set.")
        loadChats()

        return rootView
    }

    private fun loadChats() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.email ?: return
        val db = FirebaseFirestore.getInstance()

        val chatMap = mutableMapOf<String, Message>()

        db.collection("messages")
            .whereEqualTo("senderId", currentUserId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ChatFragment", "Error fetching sent messages: ", exception)
                    return@addSnapshotListener
                }

                snapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    val chatPartnerId = if (message?.senderId == currentUserId) {
                        message.receiverId
                    } else {
                        message?.senderId
                    }
                    if (message != null && chatPartnerId != null) {
                        chatMap[chatPartnerId] = message
                    }
                }

                db.collection("messages")
                    .whereEqualTo("receiverId", currentUserId)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshotReceiver, exceptionReceiver ->
                        if (exceptionReceiver != null) {
                            Log.e("ChatFragment", "Error fetching received messages: ", exceptionReceiver)
                            return@addSnapshotListener
                        }

                        snapshotReceiver?.documents?.forEach { document ->
                            val message = document.toObject(Message::class.java)
                            val chatPartnerId = if (message?.senderId == currentUserId) {
                                message.receiverId
                            } else {
                                message?.senderId
                            }
                            if (message != null && chatPartnerId != null) {
                                chatMap[chatPartnerId] = message
                            }
                        }
                        val chatsList = chatMap.values.toList()
                        Log.d("ChatFragment", "Loaded chats: ${chatsList.size}")
                        chatAdapter.updateData(chatsList)
                    }
            }
    }

}
