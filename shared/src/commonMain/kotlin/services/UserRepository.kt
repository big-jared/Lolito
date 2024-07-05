package services

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import appStyle
import color
import com.materialkolor.PaletteStyle
import defaultTone
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import models.AlertTone
import models.User
import screens.uid

object UserService {
    val currentUser: Flow<User?> get() = _currentUser
    private val _currentUser: MutableSharedFlow<User?> = MutableSharedFlow()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        _currentUser.emit(getUser(uid ?: return@withContext null))
    }

    suspend fun setStyle(style: PaletteStyle) = withContext(Dispatchers.IO) {
        _currentUser.emit(_currentUser.lastOrNull()?.copy(style = style.name))
    }

    suspend fun setColor(color: Color) = withContext(Dispatchers.IO) {
        val currentUser = _currentUser.lastOrNull()
        _currentUser.emit(currentUser?.copy(seedColor = color.toArgb()))
    }

    suspend fun getUser(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            Firebase.firestore.document("/users/$userId").get().data()
        } catch (e: Exception) {
            null
        }
    }
}

interface UserDataSource {
    fun get(userId: String): User?
    fun update(user: User): User?
    fun create(user: User): User?
    fun delete(user: User): User?
}