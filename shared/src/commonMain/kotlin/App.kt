import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.ScaleTransition
import cafe.adriel.voyager.transitions.SlideOrientation
import cafe.adriel.voyager.transitions.SlideTransition
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import screens.Landing
import screens.home.HomeScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App() {
    AppTheme {
        BottomSheetNavigator(
            sheetBackgroundColor = Color.Transparent
        ) {
            Navigator(SplashScreen()) { navigator ->
                SlideTransition(navigator, orientation = SlideOrientation.Horizontal)
            }
        }
    }
}

class SplashScreen: Screen {
    @Composable
    override fun Content() {
        if (Firebase.auth.currentUser == null) Landing().Content() else HomeScreen().Content()
    }
}

expect fun getPlatformName(): String

expect fun randomUUID(): String