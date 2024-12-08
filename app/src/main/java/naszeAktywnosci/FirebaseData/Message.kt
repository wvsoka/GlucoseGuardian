package naszeAktywnosci.FirebaseData

import com.google.firebase.firestore.PropertyName

data class Message(
    @get:PropertyName("senderId") @set:PropertyName("senderId") var senderId: String = "",
    @get:PropertyName("receiverId") @set:PropertyName("receiverId") var receiverId: String = "",
    @get:PropertyName("text") @set:PropertyName("text") var text: String = "",
    @get:PropertyName("timestamp") @set:PropertyName("timestamp") var timestamp: Long = 0L // Long zamiast String
)