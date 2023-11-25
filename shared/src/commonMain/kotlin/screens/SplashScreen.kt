package screens

import LocalNavigator
import Screen
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import screens.home.HomeScreen
import screens.preAuth.Landing

class SplashScreen : Screen() {
    @Composable
    override fun BoxScope.Content() {
        val navigator = LocalNavigator.current

        LaunchedEffect(null) {
            navigator.changeScreen(if (Firebase.auth.currentUser == null) Landing() else HomeScreen(), false)
        }
    }
}