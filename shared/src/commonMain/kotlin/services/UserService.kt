package services

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import models.User

object UserService {
    suspend fun setUser(user: User) = withContext(Dispatchers.IO) {
        Firebase.firestore.document("/users/${user.userId}").set(User.serializer(), user)
    }

    suspend fun getUser(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            Firebase.firestore.document("/users/$userId").get().data()
        } catch (e: Exception) {
            null
        }
    }
}