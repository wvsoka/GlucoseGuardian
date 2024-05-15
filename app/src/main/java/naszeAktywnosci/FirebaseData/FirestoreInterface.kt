package naszeAktywnosci.FirebaseData

interface FirestoreInterface {
    suspend fun addUser(userId: String, user: User)
    suspend fun getUser(userId: String): User?
    suspend fun updateUser(userId: String, updatedUser: User)
    suspend fun deleteUser(userId: String)
    suspend fun addMeasurements(userId: String, measurements: UserMeasurments)
    suspend fun getMeasurements(userId: String): List<UserMeasurments>
    suspend fun deleteMeasurements(userId: String, measurementId: String)
    suspend fun addMealInfo(userId: String, mealInfo: MealInfo)
    suspend fun getMealInfo(userId: String): List<MealInfo>
    suspend fun deleteMealInfo(userId: String, mealId: String)
    suspend fun addInsulinInfo(userId: String, insulinInfo: InsulinInfo)
    suspend fun getInsulinInfo(userId: String): List<InsulinInfo>
    suspend fun deleteInsulinInfo(userId: String, insulinId: String)
}