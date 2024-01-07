import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle

//
//val LocalNavigator = compositionLocalOf<MicroNavigator> { throw Exception() }
//
//abstract class Screen {
//    val path = Path()
//
//    @Composable
//    abstract fun BoxScope.Content()
//
//    @Composable
//    fun AnimatableContent(navigator: MicroNavigator) {
//        val animatePercent = remember { Animatable(0f) }
//        var direction by remember { mutableStateOf(Direction.Up) }
//        LaunchedEffect(animatePercent, navigator.currentScreen.value) {
//            direction = when (direction) {
//                Direction.Down -> Direction.Up
//                Direction.Up -> Direction.Down
//            }
//            animatePercent.animateTo(
//                targetValue = if (animatePercent.value > 0f) 0f else 1f,
//                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
//            )
//        }
//        Box(modifier = Modifier.fillMaxSize().background(lightGrey)) {
//            Box(modifier = Modifier.fillMaxSize().clipToBounds().drawWithContent {
//                drawPath(backgroundCirclePath(animatePercent.value, direction), color = darkGrey)
//            })
//            CompositionLocalProvider(
//                LocalContentColor provides Color.Black,
//            ) {
//                Box(modifier = Modifier.fillMaxSize()) {
//                    Content()
//                }
//            }
//            CompositionLocalProvider(
//                LocalContentColor provides Color.White,
//            ) {
//                Box(modifier = Modifier.fillMaxSize().drawWithContent {
//                    clipPath(backgroundCirclePath(animatePercent.value, direction)) {
//                        this@drawWithContent.drawContent()
//                    }
//                }) {
//                    Content()
//                }
//            }
//        }
//    }
//
//    private enum class Direction {
//        Down, Up
//    }
//
//    private fun DrawScope.backgroundCirclePath(animatePercent: Float, direction: Direction): Path {
//        val radius = size.height * 3 * ((animatePercent / 2f) + .25f)
//        path.reset()
//        return path.apply {
//            addOval(
//                Rect(
//                    left = (size.width / 2) - radius,
//                    right = (size.width / 2) + radius,
//                    bottom = (size.height * 2) + radius,
//                    top = (size.height * 2) - radius,
//                )
//            )
//        }
//    }
//}
//
//class MicroNavigator(startingScreen: Screen) {
//    val currentScreen = mutableStateOf(startingScreen)
//
//    suspend fun changeScreen(screen: Screen, keepBackstack: Boolean = true) {
//        currentScreen.value = screen
//    }
//}
//
//@Composable
//fun Navigation(startingScreen: Screen) {
//    val navigator = MicroNavigator(startingScreen)
//    CompositionLocalProvider(LocalNavigator provides navigator) {
//        navigator.currentScreen.value.AnimatableContent(navigator)
//    }
//}