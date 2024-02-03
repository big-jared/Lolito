import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.Progress
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import screens.SplashScreen
import screens.uid

sealed class FileSelectionState {
    data object NotSelecting : FileSelectionState()
    data object PickingFile : FileSelectionState()
    class Uploading(val fileUploadProgress: Flow<Progress?>) : FileSelectionState()
}

object FileProcessor {
    var state = mutableStateOf<FileSelectionState>(FileSelectionState.NotSelecting)

    suspend fun uploadFile(fileName: String) = withContext(Dispatchers.IO) {
        val uploadingFlow = Firebase.storage.reference("$uid/profile-image").putFileResumable(toFile(fileName))
        state.value = FileSelectionState.Uploading(uploadingFlow)
    }

    fun startFileSelection() {
        state.value = FileSelectionState.PickingFile
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
    }
}

@Composable
fun CircularProgress(modifier: Modifier = Modifier, progress: Float, activeColor: Color) {
    val animatedProgress by animateFloatAsState(progress)
    val outerColor = green

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(80.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize(.85f)) {
            // Define colors and stroke style
            val stroke = 14.dp.toPx()
            val startAngle = 270f
            val sweep = 360 * animatedProgress
            // Draw outer circle
            drawCircle(
                color = outerColor,
                style = Stroke(stroke)
            )
            // Draw inner arc with percentage value
            drawArc(
                color = activeColor,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(stroke)
            )
        }
    }
}

expect fun getPlatformName(): String
expect fun randomUUID(): String
expect fun toFile(path: String): File