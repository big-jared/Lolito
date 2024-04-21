package screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.materialkolor.ktx.harmonize
import com.materialkolor.ktx.lighten
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import com.materialkolor.ktx.tonalContrastRatio
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import micro.shared.generated.resources.Res
import micro.shared.generated.resources.filter
import models.Task
import models.TaskType
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import screens.create.TaskScreenModel
import screens.create.TaskSheet
import screens.create.TaskTypeDialogContent
import services.TaskService
import utils.AppIconButton
import utils.DialogCoordinator
import utils.DialogData
import utils.FullScreenProgressIndicator
import utils.Lottie
import utils.decreaseContrast
import utils.increaseContrast
import utils.takeIfNotEmpty

object HomeTab : Tab {

    private var showCompleted = mutableStateOf(false)

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val coScope = rememberCoroutineScope()

        LifecycleEffect(onStarted = {
            coScope.launch {
                TaskRepository.update()
            }
        })

        TaskRepository.taskMap.value?.let { taskMap ->
            Column(Modifier.fillMaxSize().padding(all = 8.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TasksHeader()

                    val filteredTasks = taskMap.keys.sortedByDescending { it.name }.mapNotNull { type ->
                        val tasks = taskMap[type]?.filterNot { !showCompleted.value && it.complete }?.takeIfNotEmpty() ?: return@mapNotNull null
                        type to tasks
                    }.toMap()

                    if (filteredTasks.isEmpty()) {
                        Lottie(modifier = Modifier.padding(16.dp), fileName = "empty-state.json")
                    } else {
                        FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
                            filteredTasks.forEach { (type, tasks) ->
                                TypeCard(type, tasks)
                            }
                        }
                    }
                }
            }
        } ?: run {
            FullScreenProgressIndicator()
        }
    }

    @OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun TasksHeader() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        var filterShowing by remember { mutableStateOf(false) }

        Column {
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
                AppIconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter = painterResource(Res.drawable.filter),
                    onClick = { filterShowing = filterShowing.not() },
                    contentColor = if (filterShowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    containerColor = if (filterShowing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                )
            }
            AnimatedVisibility(filterShowing) {
                SingleChoiceSegmentedButtonRow (modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    SegmentedButton(!showCompleted.value, shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2), onClick = { showCompleted.value = false }, label = {
                        Text("Hide Completed")
                    })
                    SegmentedButton(showCompleted.value, onClick = { showCompleted.value = true }, shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2), label = {
                        Text("Show Completed")
                    })
                }
            }
        }
    }

    @Composable
    fun TypeCard(type: TaskType, tasks: List<Task>) {
        val navigator = LocalBottomSheetNavigator.current
        val coScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(4.dp)
                .border(
                    2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = type.derivedColor().decreaseContrast()
                )
                .background(type.derivedColor().copy(alpha = .1f), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable {
                    DialogCoordinator.show(DialogData {
                        TaskTypeDialogContent(type) { savedType ->
                            coScope.launch {
                                TaskService.setTaskType(savedType)
                            }
                        }
                    })
                }.padding(8.dp),
                text = type.name,
                style = MaterialTheme.typography.titleLarge,
                color = type.derivedColor().harmonize(MaterialTheme.colorScheme.onBackground, true)
            )
            Column {
                tasks.sortedBy { it.name }.forEach { task ->
                    Row(Modifier.clip(RoundedCornerShape(16.dp)).clickable {
                        navigator.show(TaskSheet(TaskScreenModel.fromExisting(task, type)))
                    }.padding(end = 8.dp)) {
                        Checkbox(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            checked = task.complete,
                            colors = CheckboxDefaults.colors(checkedColor = type.derivedColor().decreaseContrast(), uncheckedColor = type.derivedColor().increaseContrast()),
                            onCheckedChange = {
                            coScope.launch {
                                TaskRepository.putTask(TaskScreenModel.fromExisting(task.copy(complete = !task.complete), type))
                            }
                        })
                        Text(
                            modifier = Modifier.weight(1f)
                                .align(Alignment.CenterVertically),
                            text = task.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }
}