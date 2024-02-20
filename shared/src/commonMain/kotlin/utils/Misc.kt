package utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import lightGrey
import screens.uid

object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}

@Composable
fun ColorHintCircle(modifier: Modifier = Modifier, color: Color) {
    Box(modifier.background(color = color, shape = CircleShape))
}

fun <T> List<T>.takeIfNotEmpty() = this.takeIf { it.isNotEmpty() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HighlightBox(
    modifier: Modifier = Modifier,
    color: Color? = null,
    backgroundColor: Color? = null,
    frontIcon: (@Composable () -> Unit)? = null,
    backIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    text: String?
) {
    val mainColor = color ?: MaterialTheme.colorScheme.primary
    val background = backgroundColor ?: MaterialTheme.colorScheme.primaryContainer

    Row(
        modifier
            .background(background.copy(alpha = .4f), shape = CircleShape)
            .clip(CircleShape)
            .combinedClickable(onClick = { onClick() }, onLongClick = { onLongClick() })
    ) {
        if (frontIcon != null) {
            Box(modifier = Modifier.padding(12.dp).size(16.dp).align(Alignment.CenterVertically)) {
                frontIcon()
            }
        }
        text?.let {
            Text(
                modifier = if (frontIcon == null) Modifier.padding(8.dp) else Modifier
                    .padding(end = 12.dp)
                    .align(Alignment.CenterVertically),
                text = text,
                color = mainColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
        if (backIcon != null) {
            Box(modifier = Modifier.padding(12.dp).size(32.dp)) {
                backIcon()
            }
        }
    }
}

class ImageEditOptions(val showFilePicker: () -> Unit = {})

@Composable
fun ProfileIcon(modifier: Modifier = Modifier, editOptions: ImageEditOptions? = null) {
    var profileIconUrl by remember { mutableStateOf("") }

    LaunchedEffect(null) {
        try {
            profileIconUrl = Firebase.storage.reference("$uid/profile-image").getDownloadUrl()
        } catch (ignored: Exception) {
        }
    }

    Box {
        Image(
            modifier = modifier.background(color = lightGrey, shape = CircleShape).clip(CircleShape)
                .padding(4.dp),
            painter = rememberVectorPainter(Icons.Default.Person),
            contentDescription = "image",
            contentScale = ContentScale.FillBounds,
        )
        AnimatedVisibility(profileIconUrl.isNotBlank(), enter = fadeIn()) {
            KamelImage(
                modifier = modifier.clip(CircleShape),
                resource = asyncPainterResource(data = profileIconUrl),
                contentDescription = "profile image",
                contentScale = ContentScale.FillBounds,
            )
        }
        editOptions?.let {
            Image(
                modifier = Modifier.size(32.dp).background(color = lightGrey, shape = CircleShape)
                    .clip(CircleShape).align(Alignment.BottomEnd).clickable {
                        it.showFilePicker()
                    }.padding(8.dp),
                painter = rememberVectorPainter(Icons.Default.Edit),
                contentDescription = "profile image default",
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}


@Composable
fun CircularProgress(modifier: Modifier = Modifier, progress: Float, activeColor: Color) {
    val animatedProgress by animateFloatAsState(progress)
    val outerColor = lightGrey

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


@Composable
fun AppIconButton(
    modifier: Modifier,
    onClick: () -> Unit,
    painter: Painter = rememberVectorPainter(Icons.Rounded.Add),
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    FilledTonalIconButton(
        modifier = modifier, onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor)
    ) {
        Icon(modifier = Modifier.fillMaxSize(.6f), painter = painter, contentDescription = "", tint = contentColor)
    }
}