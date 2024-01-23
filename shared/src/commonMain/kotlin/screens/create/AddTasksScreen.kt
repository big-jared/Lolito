package screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import green
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import lightGrey
import lighterGrey
import models.Task
import models.TaskType
import screens.uid
import services.TaskService
import services.UserService


class AddTasksScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()
        var dialogText by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }

        var initialAnimation by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        var showDialog = rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(null) {
            withContext(Dispatchers.IO) {
                delay(100)
                initialAnimation = true
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                AnimatedVisibility(initialAnimation) {
                    Column {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "Create New Task",
                            style = MaterialTheme.typography.titleLarge,
                            color = lighterGrey
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name", color = lighterGrey) },
                            placeholder = { Text("Write a task app", color = lighterGrey) },
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = lightGrey, focusedBorderColor = lightGrey))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable {
                                showDialog.value = true
                            },
                            enabled = false,
                            value = Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis ?: 0).toString(),
                            onValueChange = {},
                            label = { Text(text = "Due Date", color = lighterGrey) },
                            placeholder = { Text("Monday 22, July", color = lighterGrey) },
                            trailingIcon = { Icon(painter = rememberVectorPainter(Icons.Rounded.DateRange ), "", tint = lightGrey) },
                            colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = lightGrey)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(16.dp).defaultMinSize(120.dp),
                value = details,
                onValueChange = { details = it },
                label = { Text("Details") },
                placeholder = { Text("Write something here. Please. I came up with this hint, you can come up with something too") })
            Text(modifier = Modifier.padding(top = 8.dp, start = 16.dp), text = "Assign to")
            Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                onClick = {
                    coScope.launch(Dispatchers.IO) {
                        try {
                            TaskService.setTask(
                                Task(
                                    name = name,
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
        if (showDialog.value) {
            DatePickerDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Ok")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                },
            ) {
                DatePicker(state = datePickerState)
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