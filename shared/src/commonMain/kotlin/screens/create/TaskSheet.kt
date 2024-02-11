package screens.create

import BottomSheetScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import blue
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import darkGrey
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import lightGrey
import lightRed
import models.Task
import models.TaskType
import models.User
import red
import screens.home.TaskViewModel
import services.UserService.currentUser
import utils.ColorHintCircle
import utils.DialogCoordinator
import utils.DialogData
import utils.HighlightBox
import utils.ProfileIcon

data class TaskScreenModel(
    val existing: Boolean = false,
    val title: MutableState<String> = mutableStateOf(""),
    val taskType: MutableState<TaskType?> = mutableStateOf(null),
    val dueDate: MutableState<Instant> = mutableStateOf(now()),
    val assignedTo: MutableState<List<User>> = mutableStateOf(mutableListOf(currentUser!!)),
    val notes: MutableState<String> = mutableStateOf("")
) {
    companion object {
        fun fromExisting(task: Task, type: TaskType) = TaskScreenModel(
            existing = true,
            title = mutableStateOf(task.name),
            taskType = mutableStateOf(type),
            dueDate = mutableStateOf(task.dueDate ?: now()),
            assignedTo = mutableStateOf(task.assignees),
            notes = mutableStateOf(task.notes ?: "")
        )
    }
}

class TaskSheet(val model: TaskScreenModel = TaskScreenModel()) : BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        val horizontalPadding = Modifier.padding(horizontal = 16.dp)
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .weight(1f, false)
        ) {
            HeaderRow(horizontalPadding)
            TaskTypeRow(horizontalPadding)
            Divider(modifier = horizontalPadding.padding(top = 8.dp))
            DueDateRow(modifier = horizontalPadding)
            Divider(modifier = horizontalPadding.padding(top = 8.dp))
            AssigneeRow(modifier = horizontalPadding)
            AdvancedRow(modifier = horizontalPadding)
        }

        Button(
            modifier = horizontalPadding.fillMaxWidth().padding(vertical = 16.dp).height(64.dp),
            onClick = {},
            shape = RoundedCornerShape(32.dp),
        ) {
            Text("Done", style = MaterialTheme.typography.bodyLarge)
        }
    }

    @Composable
    fun HeaderRow(modifier: Modifier = Modifier) {
        val navigator = LocalBottomSheetNavigator.current

        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(modifier = modifier) {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    text = "Create new task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { navigator.hide() }) {
                    Icon(Icons.Rounded.Close, "")
                }
            }
            Text(modifier = modifier.padding(top = 16.dp), text = "Title")
            OutlinedTextField(modifier = modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                value = model.title.value,
                onValueChange = { model.title.value = it },
                placeholder = { Text("Redesign this app") })
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun TaskTypeRow(modifier: Modifier = Modifier) {
        val types = TaskViewModel.taskMap.value?.keys ?: emptySet()

        fun selectProject(type: TaskType? = null) {
            DialogCoordinator.show(DialogData {
                TaskTypeDialogContent(type) { savedType ->
                    model.taskType.value = savedType
                }
            })
        }

        Row(modifier = modifier.padding(top = 32.dp)) {
            Text(
                "Project",
                Modifier.align(Alignment.CenterVertically).padding(vertical = 8.dp)
            )
            FlowRow(modifier = Modifier.align(Alignment.CenterVertically).weight(1f), horizontalArrangement = Arrangement.End) {
                types.forEach {
                    val color = Color(it.color)
                    HighlightBox(modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                        text = it.name,
                        backgroundColor = if (model.taskType.value == it) color.copy(alpha = .5f) else lightGrey,
                        color = darkGrey,
                        frontIcon = { ColorHintCircle(Modifier.size(24.dp), color) },
                        onClick = { model.taskType.value = it },
                        onLongClick = {
                            selectProject(it)
                        }
                    )
                }
            }
            FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    selectProject()
                }) {
                Icon(Icons.Rounded.Add, "")
            }
        }
    }

    @Composable
    fun DueDateRow(modifier: Modifier) {
        Row(modifier = modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
                    .padding(vertical = 8.dp),
                text = "Due Date"
            )
            HighlightBox(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "Today",
                color = red,
                backgroundColor = lightRed,
                frontIcon = {
                    Icon(
                        painter = rememberVectorPainter(
                            Icons.Default.DateRange
                        ),
                        contentDescription = "",
                        tint = red
                    )
                })
        }
    }

    @Composable
    fun AssigneeRow(modifier: Modifier = Modifier) {
        Text(modifier = modifier.padding(top = 16.dp), text = "Assignee")
        Row(modifier = modifier.padding(top = 16.dp)) {
            ProfileIcon(modifier = Modifier.size(64.dp))
            FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically)
                .padding(start = 16.dp).size(64.dp),
                onClick = { }) {
                Icon(Icons.Rounded.Add, "")
            }
        }
    }

    @Composable
    fun ColumnScope.AdvancedRow(modifier: Modifier) {
        var usingAdvanced by remember { mutableStateOf(false) }

        TextButton(
            modifier = modifier.padding(top = 16.dp).align(Alignment.End),
            onClick = {
                usingAdvanced = usingAdvanced.not()
            }) {
            Row {
                Text(
                    "Advanced Settings",
                    color = blue,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    modifier = Modifier.rotate(animateFloatAsState(if (usingAdvanced) 270f else 90f).value),
                    painter = rememberVectorPainter(
                        Icons.Default.KeyboardArrowRight
                    ),
                    contentDescription = "",
                    tint = blue,
                )
            }
        }

        AnimatedVisibility(usingAdvanced) {
            Column {
                Text(modifier = modifier.padding(top = 16.dp), text = "Notes")
                OutlinedTextField(modifier = modifier.fillMaxWidth().padding(top = 8.dp)
                    .defaultMinSize(minHeight = 120.dp),
                    shape = RoundedCornerShape(8.dp),
                    value = model.notes.value,
                    onValueChange = { model.notes.value = it },
                    placeholder = { Text("Add a description here") })
            }
        }
    }
}