import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import dev.gitlive.firebase.storage.File
import screens.SplashScreen
import utils.DialogCoordinator
import utils.DialogState
import utils.NoRippleTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val keyboardController = LocalSoftwareKeyboardController.current

    AppTheme {
        val ripple = LocalRippleTheme.current
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            Box(
                Modifier.clickable { keyboardController?.hide() }
            ) {
                CompositionLocalProvider(LocalRippleTheme provides ripple) {
                    BottomSheetNavigator(
                        sheetBackgroundColor = Color.Transparent
                    ) {
                        Navigator(SplashScreen()) { navigator ->
                            SlideTransition(navigator)
                        }
                    }
                    val dialogState = DialogCoordinator.state.value
                    AnimatedVisibility(dialogState is DialogState.Showing) {
                        AlertDialog(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            onDismissRequest = { DialogCoordinator.close() },
                            content = {
                                (dialogState as? DialogState.Showing)?.data?.content?.invoke()
                            },
                        )
                    }
                }
            }
        }
    }
}

expect fun getPlatformName(): String
expect fun randomUUID(): String
expect fun toFile(path: String): File