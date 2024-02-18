package screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import color
import com.eygraber.compose.colorpicker.ColorPicker
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import models.User
import screens.SplashScreen
import screens.create.GroupScreen
import screens.uid
import services.UserService
import utils.FileProcessor
import utils.FileSelector
import utils.ImageEditOptions
import utils.ProfileIcon

object SettingsTab : Tab {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent ?: return
        val coScope = rememberCoroutineScope()

        var user: User? by remember { mutableStateOf(null) }
        var selectingColor: Boolean by remember { mutableStateOf(false) }

        LaunchedEffect(null) {
            user = UserService.getUser(uid ?: return@LaunchedEffect)
        }

        Column(modifier = Modifier.padding(16.dp).padding(top = 32.dp)) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)) {
                ProfileIcon(modifier = Modifier.fillMaxWidth(.3f).aspectRatio(1f), editOptions = ImageEditOptions {
                    FileProcessor.startFileSelection()
                })
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = user?.displayName ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            AnimatedVisibility(selectingColor) {
                ColorPicker(modifier = Modifier.padding(20.dp)) {
                    color.value = it
                }
            }
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (selectingColor) {
                        coScope.launch {
                            UserService.setUser(UserService.currentUser!!.copy(seedColor = color.value.toArgb()))
                        }
                    }
                    selectingColor = !selectingColor
                },
                content = {
                    if (selectingColor) {
                        Text("Choose Color")
                    } else {
                        Text("Change App Color")
                    }
                }
            )
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coScope.launch {
                        navigator.push(GroupScreen())
                    }
                },
                content = {
                    Text("Invite User")
                }
            )
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coScope.launch {
                        navigator.push(GroupScreen())
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
                            Firebase.auth.signOut()
                            navigator.replaceAll(SplashScreen())
                        } catch (e: Exception) {}
                    }
                },
                content = {
                    Text("Sign out")
                }
            )
        }

        FileSelector()
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }
}