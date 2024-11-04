package naszeAktywnosci.FirebaseData

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import android.util.Log
import java.lang.Exception
import java.util.UUID

class FirestoreHandler(private val db: FirebaseFirestore) : FirestoreInterface{

    suspend fun generateId(db: FirebaseFirestore, collectionPath: String): String {
        val id = UUID.randomUUID().toString()
        val documentSnapshot = db.collection(collectionPath).document(id).get().await()
        return if (documentSnapshot.exists()) {
            generateId(db, collectionPath)
        } else {
            id
        }
    }

    override suspend fun addUser(userId: String, user: User) {
        try {
            db.collection("users").document(userId).set(user).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user: $e")
            throw FirestoreException("Error adding user", e)
        }
    }

    override suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user: $e")
            null
        }
    }

    override suspend fun updateUser(userId: String, updatedUser: User) {
        try {
            db.collection("users").document(userId).set(updatedUser).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: $e")
            throw FirestoreException("Error updating user", e)
        }
    }

    override suspend fun deleteUser(userId: String) {
        try {
            db.collection("users").document(userId).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user: $e")
            throw FirestoreException("Error deleting user", e)
        }
    }

    override suspend fun addMeasurements(userId: String, measurements: UserMeasurments) {
        try {
            db.collection("user_measurements").document(userId).collection("measurements").add(measurements).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding measurements: $e")
            throw FirestoreException("Error adding measurements", e)
        }
    }

    override suspend fun getMeasurements(userId: String): List<UserMeasurments> {
        return try {
            val snapshot = db.collection("user_measurements").document(userId)
                .collection("measurements")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(UserMeasurments::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting measurements: $e")
            emptyList()
        }
    }

    override suspend fun deleteMeasurements(userId: String, measurementId: String) {
        try {
            db.collection("user_measurements").document(userId)
                .collection("measurements")
                .document(measurementId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting measurements: $e")
            throw FirestoreException("Error deleting measurements", e)
        }
    }

    override suspend fun updateMeasurement(userId: String, measurementId: String, updatedMeasurement: UserMeasurments) {
        try {
            db.collection("user_measurements").document(userId)
                .collection("measurements")
                .document(measurementId).set(updatedMeasurement).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: $e")
            throw FirestoreException("Error updating user", e)
        }
    }

    override suspend fun addMealInfo(userId: String, mealInfo: MealInfo) {
        try {
            db.collection("meal_info").document(userId).collection("meals").add(mealInfo).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding meal info: $e")
            throw FirestoreException("Error adding meal info", e)
        }
    }

    override suspend fun getMealInfo(userId: String): List<MealInfo> {
        return try {
            val snapshot = db.collection("meal_info").document(userId)
                .collection("meals")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(MealInfo::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting meal info: $e")
            emptyList()
        }
    }

    override suspend fun deleteMealInfo(userId: String, mealId: String) {
        try {
            db.collection("meal_info").document(userId)
                .collection("meals")
                .document(mealId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting meal info: $e")
            throw FirestoreException("Error deleting meal info", e)
        }
    }

    override suspend fun updateMealInfo(userId: String, mealId: String, mealInfo: MealInfo) {
        try {
            db.collection("meal_info").document(userId)
                .collection("meals")
                .document(mealId).set(mealInfo).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating mela: $e")
            throw FirestoreException("Error updating meal", e)
        }
    }

    override suspend fun addInsulinInfo(userId: String, insulinInfo: InsulinInfo) {
        try {
            db.collection("insulin_info").document(userId).collection("insulin").add(insulinInfo).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding insulin info: $e")
            throw FirestoreException("Error adding insulin info", e)
        }
    }

    override suspend fun getInsulinInfo(userId: String): List<InsulinInfo> {
        return try {
            val snapshot = db.collection("insulin_info").document(userId)
                .collection("insulin")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(InsulinInfo::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting insulin info: $e")
            emptyList()
        }
    }

    override suspend fun deleteInsulinInfo(userId: String, insulinId: String) {
        try {
            db.collection("insulin_info").document(userId)
                .collection("insulin")
                .document(insulinId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting insulin info: $e")
            throw FirestoreException("Error deleting insulin info", e)
        }
    }

    override suspend fun updateInsulinInfo(userId: String, insulinId: String, insulinInfo: InsulinInfo) {
        try {
            db.collection("insulin_info").document(userId)
                .collection("insulin")
                .document(insulinId).set(insulinInfo).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: $e")
            throw FirestoreException("Error updating user", e)
        }
    }

    companion object {
        private const val TAG = "FirestoreHandler"
    }
}


class FirestoreException(message: String, cause: Throwable) : Exception(message, cause)