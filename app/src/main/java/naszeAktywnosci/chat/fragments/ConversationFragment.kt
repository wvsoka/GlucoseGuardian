package naszeAktywnosci.chat.fragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import naszeAktywnosci.FirebaseData.Message
import naszeAktywnosci.chat.adapter.MessageAdapter

class ConversationFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private var messages: MutableList<Message> = mutableListOf()
    private var receiverId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_conversation, container, false)
        Log.d("ConversationFragment", "onCreateView called")

        recyclerView = rootView.findViewById(R.id.recyclerViewMessages)
        messageAdapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = messageAdapter
        recyclerView.visibility = View.VISIBLE

        editTextMessage = rootView.findViewById(R.id.editTextMessage)
        buttonSend = rootView.findViewById(R.id.buttonSend)

        // Pobranie ID odbiorcy z argumentów
        receiverId = arguments?.getString("receiverId")
        Log.d("ConversationFragment", "Fragment created with receiverId: $receiverId")

        // Załaduj wiadomości
        loadMessages()

        // Nasłuchuj na przycisk wyślij
        buttonSend.setOnClickListener {
            sendMessage()
        }

        Log.d("ConversationFragment", "Loaded messages: ${messages.size}")

        return rootView
    }

    override fun onStart() {
        super.onStart()
        Log.d("ConversationFragment", "onStart called")
    }

    private fun loadMessages() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.email ?: return
        val db = FirebaseFirestore.getInstance()

        Log.d("ConversationFragment", "Loading messages for sender: $currentUserId, receiver: $receiverId")

        db.collection("messages")
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", receiverId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ConversationFragment", "Error loading messages", exception)
                    return@addSnapshotListener
                }

                // Pobierz wszystkie dokumenty
                snapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        if (!messages.contains(message)) {
                            messages.add(message)
                        }
                    }
                }

                // Powiadom adapter o nowych wiadomościach
                messageAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
            }
    }

    private fun sendMessage() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.email ?: return
        val text = editTextMessage.text.toString().trim()

        if (text.isNotEmpty() && receiverId != null) {
            val message = Message(
                senderId = currentUserId,
                receiverId = receiverId!!,
                text = text,
                timestamp = System.currentTimeMillis()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("messages")
                .add(message)
                .addOnSuccessListener {
                    editTextMessage.text.clear()

                    // Immediately update the UI after sending the message
                    messages.add(message)
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)  // Przewiń do nowej wiadomości

                    // Load messages again after sending to reflect the updated data
                    loadMessages()
                }
                .addOnFailureListener { exception ->
                    Log.e("ConversationFragment", "Error sending message", exception)
                }
        }
    }
}