package screens.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import green
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import models.Task
import models.TaskType
import screens.uid
import services.TaskService
import services.UserService

class AddTasksScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()
        var dialogText by remember { mutableStateOf("") }
        var taskName by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = "Name"
                )
                OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = taskName,
                    onValueChange = {
                        taskName = it
                    })
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = "Type"
                )
                OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = type,
                    onValueChange = {
                        type = it
                    })
                Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                    onClick = {
                        coScope.launch(Dispatchers.IO) {
                            try {
                                TaskService.setTask(
                                    Task(
                                        name = taskName,
                                        creator = UserService.getUser(uid!!)!!,
                                        complete = false
                                    ), TaskType(name = type, color = green.toArgb())
                                )
                                navigator.pop()
                            } catch (e: Exception) {
                                dialogText = e.message.toString()
                            }
                        }
                    }
                ) {
                    Text("Sign Up")
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