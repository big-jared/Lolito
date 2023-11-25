package screens.home

import LocalNavigator
import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Task
import models.TaskType
import models.allTasks
import models.currentUser
import screens.create.AddTasksScreen
import services.TaskService

class HomeScreen : Screen() {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun BoxScope.Content() {
        val tasksByType = allTasks.value.filter { it.assignee == currentUser }.groupBy { it.type }
        val navigator = LocalNavigator.current
        val coScope = rememberCoroutineScope()

        Scaffold {
            Column(Modifier.fillMaxSize().padding(all = 8.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TasksHeader()
                    FlowRow(modifier = Modifier.padding(top = 8.dp)) {
                        tasksByType.toList().sortedByDescending { it.first.name }
                            .forEach { (type, tasks) ->
                                TypeCard(type, tasks)
                            }
                    }
                }
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    coScope.launch {
                        navigator.changeScreen(AddTasksScreen())
                    }
                }) {
                    Text("Add Tasks")
                }
            }
        }
    }

    @Composable
    fun TasksHeader() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = today.dayOfWeek.name.lowercase().capitalize(Locale.current),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = "${today.dayOfMonth}th of ${
                        today.month.name.lowercase().capitalize(
                            Locale.current
                        )
                    }",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            FilledTonalIconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    bottomSheetNavigator.show(SettingsSheet())
                }
            ) {
                Icon(Icons.Rounded.Settings, "")
            }
        }
    }

    @Composable
    fun TypeCard(type: TaskType, tasks: List<Task>) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(4.dp)
                .background(Color(type.lightColor), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                modifier = Modifier.background(color = Color(type.darkColor))
                    .padding(4.dp).padding(start = 4.dp).fillMaxWidth(),
                text = type.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Column(modifier = Modifier.padding(all = 8.dp)) {
                tasks.sortedBy { it.name }.forEach { task ->
                    Row {
                        Text(
                            modifier = Modifier.weight(1f)
                                .align(Alignment.CenterVertically),
                            text = task.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        FilledTonalButton(
                            modifier = Modifier.scale(.7f).align(Alignment.Top),
                            onClick = {
                                TaskService.completeTask(task)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (task.complete) {
                                    Color(type.darkColor)
                                } else {
                                    MaterialTheme.colorScheme.background
                                }
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Icon(Icons.Filled.Check, "", tint = Color(type.lightColor))
                        }
                    }
                }
            }
        }
    }
}