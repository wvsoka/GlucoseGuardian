package naszeAktywnosci.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import naszeAktywnosci.FirebaseData.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        adapter = ChatAdapter(messages, auth.currentUser?.uid ?: "")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            sendMessage()
        }

        listenForMessages()
    }

    private fun sendMessage() {
        val messageContent = messageEditText.text.toString()
        if (messageContent.isNotBlank()) {
            val messageId = database.collection("messages").document().id
            val message = Message(
                user1 = auth.currentUser?.uid ?: "",
                user2 = "user2_id",
                content = messageContent,
                time = System.currentTimeMillis().toString(),
                messageId = messageId
            )
            database.collection("messages").document(messageId)
                .set(message)
                .addOnSuccessListener {
                    messageEditText.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Błąd wysyłania wiadomości", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun listenForMessages() {
        database.collection("messages")
            .orderBy("time")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                snapshots?.let {
                    messages.clear()
                    for (document in it.documents) {
                        val message = document.toObject(Message::class.java)
                        message?.let { msg -> messages.add(msg) }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
    }

}
