package utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import lightGrey
import screens.uid

@Composable
fun ColorHintCircle(modifier: Modifier = Modifier, color: Color) {
    Box(modifier.background(color = color, shape = CircleShape))
}

@Composable
fun HighlightBox(
    modifier: Modifier = Modifier,
    color: Color? = null,
    backgroundColor: Color? = null,
    frontIcon: (@Composable () -> Unit)? = null,
    backIcon: (@Composable () -> Unit)? = null,
    text: String
) {
    val mainColor = color ?: MaterialTheme.colorScheme.primary
    val background = backgroundColor ?: MaterialTheme.colorScheme.primaryContainer

    Box(modifier = modifier.background(Color.White, shape = CircleShape)) {
        Row(Modifier.background(background.copy(alpha = .4f), shape = CircleShape)) {
            if (frontIcon != null) {
                Box(modifier = Modifier.padding(12.dp).size(16.dp).align(Alignment.CenterVertically)) {
                    frontIcon()
                }
            }
            Text(
                modifier = if (frontIcon == null) Modifier.padding(8.dp) else Modifier
                    .padding(end = 12.dp)
                    .align(Alignment.CenterVertically),
                text = text,
                color = mainColor,
                style = MaterialTheme.typography.labelMedium
            )
            if (backIcon != null) {
                Box(modifier = Modifier.padding(12.dp).size(32.dp)) {
                    backIcon()
                }
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