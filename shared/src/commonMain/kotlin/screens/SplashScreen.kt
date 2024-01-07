package screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import screens.create.GroupScreen
import screens.home.HomeScreen
import screens.preAuth.Landing
import services.GroupService

val uid: String? get() = Firebase.auth.currentUser?.uid

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(null) {
            navigator.push(when {
                uid == null -> Landing()
                GroupService.getGroups(uid!!).isEmpty() -> GroupScreen()
                else -> HomeScreen()
            })
        }
    }
}