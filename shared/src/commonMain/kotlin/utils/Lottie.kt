package utils

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import animateKottieCompositionAsState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import rememberKottieComposition

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Lottie(modifier: Modifier = Modifier, fileName: String, iterations: Int = Int.MAX_VALUE) {
    val composition = rememberKottieComposition(
        KottieCompositionSpec.File(resource(fileName))
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