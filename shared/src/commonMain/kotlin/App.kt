import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import screens.SplashScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App() {
    AppTheme {
        BottomSheetNavigator(
            sheetBackgroundColor = Color.Transparent
        ) {
            Navigation(startingScreen = SplashScreen())
        }
    }
}

expect fun getPlatformName(): String

expect fun randomUUID(): String