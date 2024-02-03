package screens.home

import FileProcessor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.Progress
import dev.gitlive.firebase.storage.ProgressFlow
import dev.gitlive.firebase.storage.storage
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import lightGrey
import models.User
import screens.SplashScreen
import screens.create.GroupScreen
import screens.uid
import services.UserService

interface BottomSheetScreen : Screen {
    @Composable
    override fun Content() {
        val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, shape = shape).clip(shape)
                .padding(top = 8.dp)
        ) {
            BottomSheetContent()
        }
    }

    @Composable
    fun ColumnScope.BottomSheetContent()
}

@Composable
fun ProfileIcon(modifier: Modifier, showFilePicker: () -> Unit) {
    var profileIconUrl by remember { mutableStateOf("") }

    LaunchedEffect(null) {
        try {
            profileIconUrl = Firebase.storage.reference("Screenshot 2024-01-16 at 2.04.40 PM.png").getDownloadUrl()
        } catch (ignored: Exception) {}
    }

    Box {
        Image(
            modifier = modifier.background(color = lightGrey, shape = CircleShape).clip(CircleShape).padding(4.dp),
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
        Image(
            modifier = Modifier.size(32.dp).background(color = lightGrey, shape = CircleShape).clip(CircleShape).align(Alignment.BottomEnd).clickable {
                showFilePicker()
            }.padding(8.dp),
            painter = rememberVectorPainter(Icons.Default.Edit),
            contentDescription = "profile image default",
            contentScale = ContentScale.FillBounds,
        )
    }
}

class SettingsSheet(val mainNavigator: Navigator) : BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        val navigator = LocalBottomSheetNavigator.current
        val coScope = rememberCoroutineScope()

        var user: User? by remember { mutableStateOf(null) }

        LaunchedEffect(null) {
            user = UserService.getUser(uid ?: return@LaunchedEffect)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)) {
                ProfileIcon(modifier = Modifier.fillMaxWidth(.3f).aspectRatio(1f)) {
                    FileProcessor.startFileSelection()
                }
            }
            AnimatedVisibility(user != null) {
                Text(modifier = Modifier.padding(16.dp), text = user?.displayName ?: "", style = MaterialTheme.typography.titleLarge)
            }
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coScope.launch {
                        navigator.hide()
                        mainNavigator.push(GroupScreen())
                    }
                },
                content = {
                    Text("Switch Groups")
                }
            )
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coScope.launch {
                        try {
                            navigator.hide()
                            Firebase.auth.signOut()
                            mainNavigator.replaceAll(SplashScreen())
                        } catch (e: Exception) { }
                    }
                },
                content = {
                    Text("Sign out")
                }
            )
        }
    }
}