package naszeAktywnosci.FirebaseData

import com.google.firebase.firestore.PropertyName

data class Message (
    @get:PropertyName("user1") @set:PropertyName("user1") var user1: String = "",
    @get:PropertyName("user2") @set:PropertyName("user2") var user2: String = "",
    @get:PropertyName("content") @set:PropertyName("content") var content: String = "",
    @get:PropertyName("time") @set:PropertyName("time") var time: String = "",
    @get:PropertyName("messageId") @set:PropertyName("messageId") var messageId: String = ""

)