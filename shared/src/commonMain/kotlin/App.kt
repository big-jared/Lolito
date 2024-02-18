import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import dev.gitlive.firebase.storage.File
import screens.SplashScreen
import utils.DialogCoordinator
import utils.DialogState

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        BottomSheetNavigator(
            sheetBackgroundColor = Color.Transparent
        ) {
            Navigator(SplashScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
        val dialogState = DialogCoordinator.state.value
        if (dialogState is DialogState.Showing) {
            AlertDialog(
                modifier = Modifier.padding(horizontal = 16.dp),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { DialogCoordinator.close() },
                content = {
                    dialogState.data.content?.invoke()
                },
            )
        }
    }
}

expect fun getPlatformName(): String
expect fun randomUUID(): String
expect fun toFile(path: String): File