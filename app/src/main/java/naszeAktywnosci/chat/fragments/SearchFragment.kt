package naszeAktywnosci.chat.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.aplikacjatestowa.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import naszeAktywnosci.FirebaseData.Message
import naszeAktywnosci.FirebaseData.User
import naszeAktywnosci.chat.adapter.UserAdapter

class SearchFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: UserAdapter
    private var usersWithMessages: MutableList<Pair<User, Message?>> = mutableListOf() // Pair to hold user and their last message
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize RecyclerView and EditText
        recyclerView = rootView.findViewById(R.id.recyclerViewUsers)
        searchEditText = rootView.findViewById(R.id.searchEditText)
        searchAdapter = UserAdapter(usersWithMessages) { user -> onUserSelected(user) }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = searchAdapter

        // Add TextWatcher to search input
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchUsers(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        fetchAllUsersWithMessages()

        return rootView
    }

    private fun fetchAllUsersWithMessages() {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").get().addOnSuccessListener { userResult ->
            val userList = userResult.map { it.toObject(User::class.java) }
            val tempUsersWithMessages = mutableListOf<Pair<User, Message?>>()

            db.collection("messages")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { messageResult ->
                    for (user in userList) {
                        if (user.userId != currentUserId) {
                            val lastMessage = messageResult.documents
                                .map { it.toObject(Message::class.java)!! }
                                .firstOrNull { it.senderId == user.userId || it.receiverId == user.userId }
                            tempUsersWithMessages.add(Pair(user, lastMessage))
                        }
                    }
                    usersWithMessages = tempUsersWithMessages
                    searchAdapter.updateData(usersWithMessages)
                }
        }
    }

    private fun searchUsers(query: String) {
        val filteredUsers = usersWithMessages.filter {
            it.first.name.contains(query, ignoreCase = true)
        }
        searchAdapter.updateData(filteredUsers)
    }

    private fun onUserSelected(user: User) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.email ?: return
        val message = Message(
            senderId = currentUserId,
            receiverId = user.userId,
            text = "Hello! This is the first message.",
            timestamp = System.currentTimeMillis()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("messages").add(message).addOnSuccessListener {
            Toast.makeText(context, "New chat started!", Toast.LENGTH_SHORT).show()
            startChat(user)
        }
    }

    private fun startChat(user: User) {
        //todo
    }

}