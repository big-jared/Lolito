package screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import appStyle
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import color
import com.eygraber.compose.colorpicker.ColorPicker
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.blend
import defaultTone
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import lightGrey
import models.AlertTone
import models.Tone
import models.User
import screens.SplashScreen
import screens.create.GroupScreen
import screens.uid
import services.GroupService.activeGroup
import services.UserService
import utils.DialogColumn
import utils.DialogCoordinator
import utils.DialogData
import utils.FileProcessor
import utils.FileSelector
import utils.FullScreenProgressIndicator
import utils.HighlightBox
import utils.ImageEditOptions
import utils.ProfileIcon

object SettingsTab : Tab {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent ?: return
        val coScope = rememberCoroutineScope()

        var localUser: User? by remember { mutableStateOf(null) }

        LaunchedEffect(null) {
            localUser = UserService.getUser(uid ?: return@LaunchedEffect)
        }

        localUser?.let { user ->

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)
                    .padding(top = 32.dp)
            ) {
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                ) {
                    ProfileIcon(
                        modifier = Modifier.fillMaxWidth(.3f).aspectRatio(1f),
                        editOptions = ImageEditOptions {
                            FileProcessor.startFileSelection()
                        })
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "User Settings",
                    style = MaterialTheme.typography.labelMedium
                )
                SettingsCard {
                    Row {
                        Text(
                            modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                            text = "Display name",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(modifier = Modifier.padding(start = 4.dp), onClick = {
                            DialogCoordinator.show(DialogData {
                                DialogColumn {
                                    var localDisplayName by remember {
                                        mutableStateOf(user.displayName)
                                    }
                                    Column(Modifier.padding(horizontal = 16.dp)) {
                                        OutlinedTextField(
                                            modifier = Modifier.fillMaxWidth(),
                                            value = localDisplayName,
                                            onValueChange = { localDisplayName = it })
                                        FilledTonalButton(
                                            modifier = Modifier.padding(top = 8.dp)
                                                .align(Alignment.CenterHorizontally), onClick = {
                                                coScope.launch {
                                                    UserService.setUser(
                                                        user.copy(displayName = localDisplayName)
                                                            .also { localUser = it })
                                                    DialogCoordinator.close()
                                                }
                                            }, content = {
                                                Text("Change")
                                            })
                                    }
                                }
                            })
                        }, content = {
                            Text(user.displayName)
                        })
                    }

                    Row {
                        Text(
                            modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                            text = "Group",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(modifier = Modifier.padding(start = 4.dp), onClick = {
                            navigator.push(GroupScreen())
                        }, content = {
                            Text(activeGroup?.groupName ?: "Not set")
                        })
                    }
                    Row {
                        Text(
                            modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                            text = "Other members",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(modifier = Modifier.padding(start = 4.dp), onClick = {
                            navigator.push(GroupScreen())
                        }, content = {
                            Text("Invite")
                        })
                    }
//                    FlowRow(modifier = Modifier.fillMaxWidth()) {
//                        HighlightBox(frontIcon = )
//                    }
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "App Settings",
                    style = MaterialTheme.typography.labelMedium
                )
                SettingsCard {
                    Row {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "App color",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = color.value),
                            onClick = {
                                DialogCoordinator.show(DialogData {
                                    Column {
                                        var localColor by remember { mutableStateOf(color.value) }
                                        ColorPicker(modifier = Modifier.padding(horizontal = 20.dp)) {
                                            localColor = it
                                        }
                                        FilledTonalButton(
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = localColor
                                            ),
                                            modifier = Modifier.padding(top = 8.dp)
                                                .align(Alignment.CenterHorizontally),
                                            onClick = {
                                                coScope.launch {
                                                    color.value = localColor
                                                    UserService.setUser(
                                                        (UserService.currentUser?.copy(
                                                            seedColor = localColor.toArgb()
                                                        ) ?: return@launch).also { localUser = it }
                                                    )
                                                    DialogCoordinator.close()
                                                }
                                            },
                                            content = {
                                                Text("Select Color")
                                            })
                                    }
                                })
                            },
                            content = {
                                Text("Edit")
                            })
                    }
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                            text = "App theme",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = {
                            DialogCoordinator.show(DialogData {
                                DialogColumn {
                                    var localStyle by remember { mutableStateOf(appStyle.value) }
                                    FlowRow(
                                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        PaletteStyle.values().forEach { style ->
                                            val selected = style == localStyle
                                            HighlightBox(
                                                modifier = Modifier.padding(4.dp),
                                                text = style.name,
                                                backgroundColor = if (selected) color.value.copy(
                                                    alpha = .5f
                                                ) else lightGrey,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                onClick = { localStyle = style },
                                            )
                                        }
                                    }
                                    FilledTonalButton(
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                            .padding(top = 8.dp),
                                        onClick = {
                                            coScope.launch {
                                                appStyle.value = localStyle
                                                UserService.setUser(
                                                    UserService.currentUser?.copy(
                                                        style = localStyle.name
                                                    ) ?: return@launch
                                                )
                                                DialogCoordinator.close()
                                            }
                                        },
                                        content = {
                                            Text("Select Theme")
                                        })
                                }
                            })
                        }, content = {
                            Text(appStyle.value.name)
                        })
                    }
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                            text = "Default Tone",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = {
                            DialogCoordinator.show(DialogData {
                                DialogColumn {
                                    var localTone by remember { mutableStateOf(defaultTone.value) }
                                    FlowRow(
                                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        AlertTone.values().forEach { tone ->
                                            val selected = tone.name == localTone.name
                                            HighlightBox(
                                                modifier = Modifier.padding(4.dp),
                                                text = tone.name,
                                                backgroundColor = if (selected) color.value.copy(
                                                    alpha = .5f
                                                ) else lightGrey,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                onClick = { localTone = Tone(tone.name) },
                                            )
                                        }
                                    }
                                    FilledTonalButton(
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                            .padding(top = 8.dp),
                                        onClick = {
                                            coScope.launch {
                                                UserService.setUser(
                                                    UserService.currentUser?.copy(
                                                        tone = localTone
                                                    ) ?: return@launch
                                                )
                                                defaultTone.value = localTone
                                                DialogCoordinator.close()
                                            }
                                        },
                                        content = {
                                            Text("Set Default Tone")
                                        })
                                }
                            })
                        }, content = {
                            Text(defaultTone.value.name)
                        })
                    }
                }
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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

                FileSelector()
            }
        } ?: FullScreenProgressIndicator()
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.padding(top = 8.dp).background(
            color = MaterialTheme.colorScheme.background.blend(MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ).padding(16.dp)
    ) {
        content()
    }
}