package naszeAktywnosci.FirebaseData

interface FirestoreInterface {
    suspend fun addData()
    suspend fun updateData()
    suspend fun getData()
    suspend fun deleteData()

}