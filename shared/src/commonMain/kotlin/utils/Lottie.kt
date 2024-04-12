package utils

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import animateKottieCompositionAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import micro.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import rememberKottieComposition

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Lottie(modifier: Modifier = Modifier, fileName: String, iterations: Int = Int.MAX_VALUE) {
    var lottieData by mutableStateOf<String?>(null)
    LaunchedEffect(Dispatchers.IO) {
        lottieData = Res.readBytes("files/$fileName").toString()
    }

    val lottie = lottieData ?: return

    val composition = rememberKottieComposition(
        KottieCompositionSpec.JsonString(lottie)
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        speed = 1f,
        iterations = iterations
    )

    KottieAnimation(
        composition = composition,
        progress = { animationState.progress },
        modifier = modifier.fillMaxSize(),
    )
}