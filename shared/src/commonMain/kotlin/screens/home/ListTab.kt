package screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Task
import models.TaskType
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import screens.create.TaskScreenModel
import screens.create.TaskSheet
import services.TaskService
import utils.AppIconButton
import utils.FileSelector
import utils.Lottie
import utils.decreaseContrast
import utils.increaseContrast
import utils.takeIfNotEmpty

object ListTab : Tab {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        LaunchedEffect(null) {
            TaskViewModel.update()
        }

        TaskViewModel.taskMap.value?.let { taskMap ->
            Column(Modifier.fillMaxSize().padding(all = 8.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TasksHeader()
                    if (taskMap.isEmpty()) {
                        Lottie(modifier = Modifier.padding(16.dp), fileName = "empty-state.json")
                    } else {
                        FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            taskMap.keys.sortedByDescending { it.name }.forEach { type ->
                                TypeCard(type, taskMap[type]?.takeIfNotEmpty() ?: return@forEach)
                            }
                        }
                    }
                }
            }
        } ?: run {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun TasksHeader() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

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
                painter = painterResource("filter.xml"),
                onClick = {  bottomSheetNavigator.show(TaskSheet()) },
            )
            AppIconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {  bottomSheetNavigator.show(TaskSheet()) },
            )
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
                .background(type.derivedColor().increaseContrast(2f), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(4.dp).fillMaxWidth(),
                text = type.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                tasks.sortedBy { it.name }.forEach { task ->
                    Row(Modifier.clip(RoundedCornerShape(16.dp)).clickable {
                        navigator.show(TaskSheet(TaskScreenModel.fromExisting(task, type)))
                    }.padding(horizontal = 8.dp)) {
                        Text(
                            modifier = Modifier.weight(1f)
                                .align(Alignment.CenterVertically),
                            text = task.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        FilledTonalButton(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .align(Alignment.CenterVertically),
                            onClick = {
                                coScope.launch {
                                    TaskService.setTask(task.copy(complete = !task.complete), type)
                                    TaskViewModel.update()
                                }
                            },
                            border = if (task.complete) null else BorderStroke(
                                1.dp,
                                type.derivedColor()
                            ),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (task.complete) {
                                    type.derivedColor().increaseContrast(2f)
                                } else {
                                    MaterialTheme.colorScheme.background
                                }
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            if (task.complete) {
                                Icon(Icons.Filled.Check, "", tint = type.derivedColor())
                            } else {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    text = "To do",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
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