package screens.preAuth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import darkGrey
import kotlinx.coroutines.launch
import lightGrey
import lighterGrey
import medGrey

val EaseOutBounce: Easing = Easing { fraction ->
    val n1 = 7.5625f
    val d1 = 2.75f
    var newFraction = fraction

    return@Easing if (newFraction < 1f / d1) {
        n1 * newFraction * newFraction
    } else if (newFraction < 2f / d1) {
        newFraction -= 1.5f / d1
        n1 * newFraction * newFraction + 0.6f
    } else if (newFraction < 2.5f / d1) {
        newFraction -= 2.25f / d1
        n1 * newFraction * newFraction + 0.8575f
    } else {
        newFraction -= 2.625f / d1
        n1 * newFraction * newFraction + 0.964375f
    }
}

class Landing : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()
        val heightAnimatePercent = remember { Animatable(0f) }

        LaunchedEffect(null) {
            heightAnimatePercent.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = EaseOutBounce)
            )
        }

        Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
            Box(Modifier.fillMaxSize().drawWithContent {
                drawIntoCanvas {
                    it.drawCircle(
                        Offset(size.width / 2f, size.height * 1.5f),
                        size.height * 1.6f * heightAnimatePercent.value,
                        Paint().apply { color = darkGrey })
                }
            })
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                    text = "Micro",
                    style = MaterialTheme.typography.displayLarge,
                    color = lighterGrey
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                    text = "(management)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = lighterGrey
                )
            }

            Box(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                    .size(84.dp)
                    .background(shape = CircleShape, color = medGrey)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                        .background(shape = CircleShape, color = lightGrey)
                        .clip(CircleShape)
                        .clickable {
                            coScope.launch {
                                navigator.replaceAll(SignInScreen())
                            }
                        }
                )
                Icon(
                    modifier = Modifier.size(32.dp).align(Alignment.Center),
                    painter = rememberVectorPainter(Icons.Rounded.ArrowForward),
                    contentDescription = null,
                    tint = darkGrey
                )
            }
        }
    }
}
