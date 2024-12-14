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
        val currentUserId = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserId == null) {
            Log.e("ConversationFragment", "No current user found")
            return rootView
        }
        recyclerView = rootView.findViewById(R.id.recyclerViewMessages)
        messageAdapter = MessageAdapter(messages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = messageAdapter
        recyclerView.visibility = View.VISIBLE

        editTextMessage = rootView.findViewById(R.id.editTextMessage)
        buttonSend = rootView.findViewById(R.id.buttonSend)

        receiverId = arguments?.getString("receiverId")
        Log.d("ConversationFragment", "Fragment created with receiverId: $receiverId")

        loadMessages()

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
            .whereIn("senderId", listOf(currentUserId, receiverId))
            .whereIn("receiverId", listOf(currentUserId, receiverId))
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ConversationFragment", "Error loading messages", exception)
                    return@addSnapshotListener
                }

                messages.clear()
                snapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }

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

                    messages.add(message)
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)

                    loadMessages()
                }
                .addOnFailureListener { exception ->
                    Log.e("ConversationFragment", "Error sending message", exception)
                }
        }
    }
}