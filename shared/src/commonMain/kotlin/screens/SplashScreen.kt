package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import screens.create.GroupScreen
import screens.preAuth.Landing
import services.GroupService
import services.UserService.getCurrentUser

val uid: String? get() = Firebase.auth.currentUser?.uid

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(null) {
            navigator.push(
                when {
                    getCurrentUser() == null -> Landing()
                    GroupService.getActiveGroup() == null -> GroupScreen()
                    else -> AuthenticatedScreen
                }
            )
        }

        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}