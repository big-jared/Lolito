package services

import androidx.compose.ui.graphics.Color
import appStyle
import color
import com.materialkolor.PaletteStyle
import defaultTone
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import models.AlertTone
import models.User
import screens.uid

object UserService {
    var currentUser: User? = null

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        currentUser = getUser(uid ?: return@withContext null)
        currentUser?.seedColor?.let {
            color.value = Color(it)
        }
        appStyle.value = PaletteStyle.valueOf(currentUser?.style ?: PaletteStyle.FruitSalad.name)
        defaultTone.value = currentUser?.tone ?: defaultTone.value
        currentUser
    }

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