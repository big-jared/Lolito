package screens.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import screens.SplashScreen
import services.GroupService

class CreateGroupScreen : Screen {
    @Composable
    override fun Content() {
        val coScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        var groupName by remember { mutableStateOf("") }
        var dialogText by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                    textAlign = TextAlign.Center,
                    text = "Create a group!",
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                    textAlign = TextAlign.Center,
                    text = "Now you just needs some friends",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = "Group Name"
                )
                OutlinedTextField(
                    value = groupName,
                    maxLines = 1,
                    onValueChange = {
                        groupName = it.trim()
                    })

                if (groupName.isNotEmpty()) {
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                        onClick = {
                            coScope.launch {
                                try {
                                    GroupService.createGroup(name = groupName)
                                    navigator.replaceAll(SplashScreen())
                                } catch (e: Exception) {
                                    dialogText = e.message ?: ""
                                }
                            }
                        }) {
                        Text("Create Group")
                    }
                }
            }

            if (dialogText.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { dialogText = "" },
                    text = {
                        Text(dialogText)
                    },
                    confirmButton = {
                        Button(modifier = Modifier.fillMaxWidth(),
                            onClick = { dialogText = "" },
                            content = {
                                Text("Ok")
                            })
                    },
                )
            }
        }
    }
}