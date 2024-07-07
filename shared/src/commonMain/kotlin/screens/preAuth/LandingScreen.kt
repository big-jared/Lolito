package screens.preAuth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.materialkolor.ktx.lighten
import com.mmk.kmpnotifier.notification.NotifierManager
import darkGrey
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import kotlinx.coroutines.launch

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
    val path = Path()

    @Composable
    override fun Content() {
        val heightAnimatePercent = remember { Animatable(0f) }
        val color = MaterialTheme.colorScheme.primary

        LaunchedEffect(null) {
            heightAnimatePercent.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = EaseOutBounce)
            )
        }

        Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
            Box(Modifier.fillMaxSize().drawWithContent {
                drawPath(backgroundCirclePath(heightAnimatePercent.value), color)
            })
            CompositionLocalProvider(
                LocalContentColor provides Color.Black,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainContent()
                }
            }
            CompositionLocalProvider(
                LocalContentColor provides Color.White,
            ) {
                Box(modifier = Modifier.fillMaxSize().drawWithContent {
                    clipPath(backgroundCirclePath(heightAnimatePercent.value)) {
                        this@drawWithContent.drawContent()
                    }
                }) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    fun BoxScope.MainContent() {
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                text = "Micro",
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                text = "(management)",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).safeContentPadding().padding(bottom = 12.dp)
                .size(84.dp)
                .background(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(8.dp)
                    .background(shape = CircleShape, color = MaterialTheme.colorScheme.primary.lighten(2f))
                    .clip(CircleShape)
                    .clickable {
                        coScope.launch {
                            navigator.replaceAll(SignInScreen())
                        }
                    }
            )
            Icon(
                modifier = Modifier.size(32.dp).align(Alignment.Center),
                painter = rememberVectorPainter(Icons.AutoMirrored.Rounded.ArrowForward),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }

    private fun DrawScope.backgroundCirclePath(animatePercent: Float): Path {
        path.reset()
        return path.apply {
            val center = Offset(size.width / 2f, size.height * 1.5f)
            val radius = size.height * 1.6f * animatePercent
            addOval(
                Rect(
                    left = center.x - radius,
                    right = center.x + radius,
                    bottom = center.y + radius,
                    top = center.y - radius,
                )
            )
        }
    }
}


