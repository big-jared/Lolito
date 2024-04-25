package screens.create

import BottomSheetScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import backgroundContainer
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import defaultTone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import lightGrey
import micro.shared.generated.resources.Res
import micro.shared.generated.resources.delete
import micro.shared.generated.resources.repeat
import micro.shared.generated.resources.schedule
import micro.shared.generated.resources.timelapse
import models.AlertTone
import models.RepeatInterval
import models.Task
import models.TaskType
import models.Tone
import models.User
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import randomUUID
import screens.home.TaskRepository
import services.UserService.currentUser
import utils.AppIconButton
import utils.ColorHintCircle
import utils.DialogColumn
import utils.DialogCoordinator
import utils.DialogData
import utils.HighlightBox
import utils.ProfileIcon
import utils.decreaseContrast
import utils.showSimpleDialog
import utils.toHourMinuteString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class TaskScreenModel(
    val existingTask: Task? = null,
    val existingType: TaskType? = null,
    val names: MutableState<List<MutableState<String>>> = mutableStateOf(listOf(mutableStateOf(""))),
    val taskType: MutableState<TaskType?> = mutableStateOf(null),
    val dueDate: MutableState<Instant?> = mutableStateOf(null),
    val assignedTo: MutableState<Set<User>> = mutableStateOf(mutableSetOf(currentUser!!)),
    val notes: MutableState<String> = mutableStateOf(""),
    val dialogText: MutableState<String> = mutableStateOf(""),
    val duration: MutableState<Duration?> = mutableStateOf(null),
    val tone: MutableState<Tone> = mutableStateOf(defaultTone.value),
    val repeatInterval: MutableState<RepeatInterval> = mutableStateOf(RepeatInterval.NONE),
) {
    val existing: Boolean get() = existingTask != null

    fun dueToday(): Boolean =
        now().toLocalDateTime(TimeZone.currentSystemDefault()).date == dueDate.value?.toLocalDateTime(
            TimeZone.currentSystemDefault()
        )?.date

    fun dueDate(): String {
        val selectedDate =
            dueDate.value?.toLocalDateTime(TimeZone.currentSystemDefault()) ?: return ""
        val month = selectedDate.month.name.lowercase().capitalize(Locale.current).take(3)
        val day = selectedDate.dayOfMonth + 1
        return if (dueToday()) "Today" else "$month $day"
    }

    fun dueTime(): String {
        val selectedDate = (dueDate.value ?: now()).toLocalDateTime(TimeZone.currentSystemDefault())
        return selectedDate.time.toHourMinuteString()
    }


    fun setTime(minutes: Int, hours: Int) {
        val date = (dueDate.value ?: now()).toLocalDateTime(TimeZone.currentSystemDefault())
        dueDate.value = LocalDateTime(
            year = date.year,
            month = date.month,
            dayOfMonth = date.dayOfMonth,
            hour = hours,
            minute = minutes
        ).toInstant(
            TimeZone.currentSystemDefault()
        )
    }

    fun setDate(selectedDate: LocalDate) {
        val date = (dueDate.value ?: now()).toLocalDateTime(TimeZone.currentSystemDefault())
        dueDate.value = LocalDateTime(
            year = selectedDate.year,
            month = selectedDate.month,
            dayOfMonth = selectedDate.dayOfMonth,
            hour = date.hour,
            minute = date.minute
        ).toInstant(
            TimeZone.currentSystemDefault()
        )
    }


    fun toTasks(): List<Task> = names.value.map {
        Task(
            id = existingTask?.id ?: randomUUID(),
            name = it.value,
            creator = currentUser!!,
            assignees = assignedTo.value,
            complete = existingTask?.complete ?: false,
            dueDate = dueDate.value,
            notes = notes.value,
            duration = duration.value,
            tone = tone.value,
            repeatInterval = repeatInterval.value
        )
    }

    fun addName() {
        names.value += listOf(mutableStateOf(""))
    }

    fun removeName(index: Int) {
        names.value = names.value.toMutableList().also { it.removeAt(index) }
    }

    fun isValid() = names.value.all { it.value.isNotBlank() }

    companion object {
        fun fromExisting(task: Task, type: TaskType) = TaskScreenModel(
            existingTask = task,
            existingType = type,
            names = mutableStateOf(listOf(mutableStateOf(task.name))),
            taskType = mutableStateOf(type),
            dueDate = mutableStateOf(task.dueDate ?: now()),
            assignedTo = mutableStateOf(task.assignees),
            notes = mutableStateOf(task.notes ?: ""),
            duration = mutableStateOf(task.duration),
            tone = mutableStateOf(task.tone),
            repeatInterval = mutableStateOf(task.repeatInterval)
        )
    }
}

class TaskSheet(val model: TaskScreenModel = TaskScreenModel()) : BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        val nav = LocalBottomSheetNavigator.current
        val coScope = rememberCoroutineScope()
        val horizontalPadding = Modifier.padding(horizontal = 16.dp)
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .weight(1f, false)
        ) {
            HeaderRow(horizontalPadding)
            TaskTypeRow(horizontalPadding)
            HorizontalDivider(modifier = horizontalPadding.padding(top = 8.dp))
            DueDateRow(modifier = horizontalPadding)

            HorizontalDivider(modifier = horizontalPadding.padding(top = 8.dp))
            AssigneeRow(modifier = horizontalPadding)
            AdvancedRow(modifier = horizontalPadding)
        }

        Button(
            modifier = horizontalPadding.fillMaxWidth().padding(vertical = 16.dp)
                .height(64.dp),
            onClick = {
                if (!model.isValid()) {
                    showSimpleDialog("Failed to save task, Please check required fields [Name]")
                    return@Button
                }
                coScope.launch {
                    TaskRepository.putTask(model)
                    nav.hide()
                }
            },
            shape = RoundedCornerShape(32.dp),
        ) {
            Text("Done", style = MaterialTheme.typography.bodyLarge)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun HeaderRow(modifier: Modifier = Modifier) {
        val navigator = LocalBottomSheetNavigator.current
        val coScope = rememberCoroutineScope()

        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(modifier = modifier) {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    text = if (model.existing) "Edit task" else "Create new task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                model.existingTask?.let {
                    AppIconButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = {
                            coScope.launch {
                                TaskRepository.delete(it, model.taskType.value ?: return@launch)
                                navigator.hide()
                            }
                        },
                        painter = painterResource(Res.drawable.delete),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                    )
                }
                AppIconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { navigator.hide() },
                    rememberVectorPainter(Icons.Rounded.Close)
                )
            }
            Text(modifier = modifier.padding(top = 16.dp), text = "Name")
            model.names.value.forEachIndexed { i, _ ->
                NameTextInput(modifier, i)
            }
            TextButton(modifier = modifier.align(Alignment.End), onClick = { model.addName() }) {
                Row {
                    Text(
                        "Add another task",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    @Composable
    fun ColumnScope.NameTextInput(modifier: Modifier = Modifier, index: Int) {
        val coScope = rememberCoroutineScope()
        val state = remember {
            MutableTransitionState(index == 0).apply {
                targetState = true
            }
        }

        AnimatedVisibility(
            visibleState = state,
        ) {
            Row {
                OutlinedTextField(modifier = modifier.weight(1f).padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    value = model.names.value[index].value,
                    onValueChange = { model.names.value[index].value = it },
                    placeholder = { Text("Redesign this app") })
                if (index > 0) {
                    AppIconButton(
                        modifier = Modifier.align(Alignment.CenterVertically)
                            .padding(top = 8.dp, end = 16.dp),
                        onClick = {
                            coScope.launch(Dispatchers.IO) {
                                state.targetState = false
                                while (!state.isIdle) {
                                }
                                model.removeName(index)
                            }
                        },
                        rememberVectorPainter(Icons.Rounded.Close)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun TaskTypeRow(modifier: Modifier = Modifier) {
        val types = TaskRepository.taskMap.value?.keys ?: emptySet()

        fun selectProject(type: TaskType? = null) {
            DialogCoordinator.show(DialogData {
                TaskTypeDialogContent(type) { savedType ->
                    model.taskType.value = savedType
                }
            })
        }

        Row(modifier = modifier.padding(top = 24.dp)) {
            Text(
                "Project",
                Modifier.align(Alignment.CenterVertically).padding(vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                types.forEach {
                    val color = it.derivedColor()
                    HighlightBox(modifier = Modifier.padding(4.dp)
                        .align(Alignment.CenterVertically),
                        text = it.name,
                        backgroundColor = if (model.taskType.value == it) color.copy(alpha = .5f) else backgroundContainer(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        frontIcon = { ColorHintCircle(Modifier.size(24.dp), color) },
                        onClick = { model.taskType.value = it },
                        onLongClick = {
                            selectProject(it)
                        }
                    )
                }
            }
            AppIconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { selectProject() })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    fun DateSelectionRow(modifier: Modifier = Modifier) {
        Row(modifier = modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
                    .padding(vertical = 8.dp),
                text = "Due Date"
            )
            HighlightBox(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = if (model.dueDate.value == null) "Select Date" else model.dueDate(),
                color = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                frontIcon = {
                    Icon(
                        painter = rememberVectorPainter(
                            Icons.Default.DateRange
                        ),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    DialogCoordinator.show(DialogData {
                        val state =
                            rememberDatePickerState(initialSelectedDateMillis = model.dueDate.value?.toEpochMilliseconds())
                        DatePickerDialog(
                            onDismissRequest = { DialogCoordinator.close() },
                            confirmButton = {
                                TextButton(onClick = {
                                    model.setDate(
                                        Instant.fromEpochMilliseconds(
                                            state.selectedDateMillis ?: now().toEpochMilliseconds()
                                        ).toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    )
                                    DialogCoordinator.close()
                                }) {
                                    Text("Ok")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { DialogCoordinator.close() }) {
                                    Text("Cancel")
                                }
                            },
                        ) {
                            DatePicker(state = state)
                        }
                    })
                })
            HighlightBox(
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                text = if (model.dueDate.value == null) "Select Time" else model.dueTime(),
                color = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                frontIcon = {
                    Icon(
                        painterResource(Res.drawable.schedule),
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    DialogCoordinator.show(DialogData {
                        val dateTime = (model.dueDate.value
                            ?: now()).toLocalDateTime(TimeZone.currentSystemDefault())
                        val state = rememberTimePickerState(
                            initialHour = dateTime.hour,
                            initialMinute = dateTime.minute,
                        )
                        Column(
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(16.dp)
                            ).padding(16.dp)
                        ) {
                            TimePicker(
                                state = state,
                            )
                            FilledTonalButton(onClick = {
                                model.setTime(state.minute, state.hour)
                                DialogCoordinator.close()
                            }) {
                                Text("Set Time")
                            }
                        }
                    })
                })
        }
    }

    @OptIn(ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
    @Composable
    fun ColumnScope.DurationSelectionRow(modifier: Modifier = Modifier) {
        AnimatedVisibility(model.dueDate.value != null) {
            FlowRow(
                modifier = modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                HighlightBox(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = if (model.duration.value == null) "Duration" else model.duration.value.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    frontIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.timelapse),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        DialogCoordinator.show(DialogData {
                            val duration = model.duration.value
                            var hours by remember {
                                mutableStateOf(
                                    duration?.inWholeHours?.toInt() ?: 0
                                )
                            }
                            var minutes by remember {
                                mutableStateOf(
                                    duration?.minus(duration.inWholeHours.hours)?.inWholeMinutes?.toInt()
                                        ?: 0
                                )
                            }

                            DialogColumn {
                                val focusRequester = remember { FocusRequester() }
                                LaunchedEffect(Unit) {
                                    focusRequester.requestFocus()
                                }
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                        .focusRequester(focusRequester)
                                ) {
                                    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.displayLarge) {
                                        TextField(
                                            modifier = Modifier.weight(.5f).padding(end = 8.dp),
                                            value = "$hours",
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number
                                            ),
                                            suffix = { Text("hr") },
                                            onValueChange = {
                                                val newHours = if (hours == 0) {
                                                    it.removeSuffix("0")
                                                } else it
                                                hours = (newHours.toIntOrNull() ?: 0)
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent
                                            )
                                        )
                                        TextField(
                                            modifier = Modifier.weight(.5f).padding(start = 8.dp),
                                            value = "$minutes",
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number
                                            ),
                                            suffix = { Text("min") },
                                            onValueChange = {
                                                val min = if (minutes == 0) {
                                                    it.removeSuffix("0")
                                                } else it
                                                val tempMinutes = (min.toIntOrNull() ?: 0)
                                                if (tempMinutes < 60) {
                                                    minutes = tempMinutes
                                                }
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent
                                            )
                                        )
                                    }
                                }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    FilledTonalButton(
                                        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                                            .align(Alignment.Center),
                                        onClick = {
                                            model.duration.value = hours.hours + minutes.minutes
                                            DialogCoordinator.close()
                                        }) {
                                        Text("Set Duration")
                                    }
                                    if (model.duration.value != null) {
                                        AppIconButton(modifier = Modifier.align(Alignment.CenterEnd)
                                            .padding(top = 16.dp, end = 16.dp),
                                            painter = painterResource(Res.drawable.delete),
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.error,
                                            onClick = {
                                                model.duration.value = null
                                                DialogCoordinator.close()
                                            })
                                    }
                                }
                            }
                        })
                    })
                HighlightBox(
                    modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
                    text = if (model.repeatInterval.value == RepeatInterval.NONE) "Repeat" else model.repeatInterval.value.title,
                    color = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    frontIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.repeat),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        DialogCoordinator.show(DialogData {
                            var repeatInterval by remember { mutableStateOf(model.repeatInterval.value) }
                            DialogColumn {
                                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    FlowRow {
                                        RepeatInterval.values().forEach {
                                            HighlightBox(modifier = Modifier.padding(4.dp)
                                                .align(Alignment.CenterVertically),
                                                text = it.title,
                                                backgroundColor = if (repeatInterval == it) MaterialTheme.colorScheme.primaryContainer else lightGrey,
                                                color = if (repeatInterval == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                                                textStyle = MaterialTheme.typography.bodyLarge,
                                                onClick = { repeatInterval = it }
                                            )
                                        }
                                    }
                                    FilledTonalButton(modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally), onClick = {
                                        model.repeatInterval.value = repeatInterval
                                        DialogCoordinator.close()
                                    }) {
                                        Text("Set Repeating")
                                    }
                                }
                            }
                        })
                    })
            }
        }
    }

    @Composable
    fun DueDateRow(modifier: Modifier) {
        Row(modifier = modifier) {
            Column(modifier = Modifier.weight(1f)) {
                DateSelectionRow()
                DurationSelectionRow()
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterVertically),
                visible = model.dueDate.value != null,
            ) {
                AppIconButton(
                    modifier = Modifier.padding(start = 4.dp).size(24.dp)
                        .align(Alignment.CenterVertically),
                    painter = rememberVectorPainter(Icons.Rounded.Close),
                    onClick = {
                        model.dueDate.value = null
                    })
            }
        }
    }

    @Composable
    fun AssigneeRow(modifier: Modifier = Modifier) {
        Text(modifier = modifier.padding(top = 16.dp), text = "Assignee")
        Row(modifier = modifier.padding(top = 16.dp)) {
            ProfileIcon(modifier = Modifier.size(64.dp))
            AppIconButton(
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp)
                    .size(64.dp), onClick = {

                })
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
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
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    modifier = Modifier.rotate(animateFloatAsState(if (usingAdvanced) 270f else 90f).value),
                    painter = rememberVectorPainter(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                    ),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        AnimatedVisibility(usingAdvanced) {
            Column {
                Text(modifier = modifier.padding(top = 16.dp), text = "Tone")
                Row(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
                    FlowRow(modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
                        AlertTone.values().forEach { tone ->
                            HighlightBox(
                                modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                                text = tone.name,
                                color = if (model.tone.value.name == tone.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                backgroundColor = if (model.tone.value.name == tone.name) MaterialTheme.colorScheme.primaryContainer else backgroundContainer(),
                                onClick = {
                                    model.tone.value = Tone(name = tone.name)
                                },
                                textModifier = Modifier.padding(2.dp).padding(horizontal = 2.dp)
                            )
                        }
                    }
                    AppIconButton(
                        modifier = Modifier.padding(top = 4.dp, end = 4.dp).align(Alignment.CenterVertically),
                        onClick = {
                            // add tone
                        })
                }
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