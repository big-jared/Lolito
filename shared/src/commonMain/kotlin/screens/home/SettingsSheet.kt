package screens.home

import BottomSheetScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import models.User
import screens.SplashScreen
import screens.create.GroupScreen
import screens.uid
import services.UserService
import utils.FileProcessor
import utils.ImageEditOptions
import utils.ProfileIcon

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
                ProfileIcon(modifier = Modifier.fillMaxWidth(.3f).aspectRatio(1f), editOptions = ImageEditOptions {
                    navigator.hide()
                    FileProcessor.startFileSelection()
                })
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = user?.displayName ?: "",
                style = MaterialTheme.typography.titleLarge
            )
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
                        } catch (e: Exception) {
                        }
                    }
                },
                content = {
                    Text("Sign out")
                }
            )
        }
    }
}