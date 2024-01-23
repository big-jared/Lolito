package screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Group
import models.Task
import models.TaskType
import models.User
import screens.create.AddTasksScreen
import screens.create.GroupScreenData
import services.TaskService
import utils.Lottie

data class HomeScreenData(
    val tasks: Map<TaskType, Flow<QuerySnapshot>>,
)

class HomeScreen : Screen {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        var homeScreenData by remember { mutableStateOf<HomeScreenData?>(null) }

        LaunchedEffect(null) {
            withContext(Dispatchers.IO) {
                homeScreenData = HomeScreenData(tasks = TaskService.getTasks())
            }
        }

        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()

        homeScreenData?.let { homeData ->
            Column(Modifier.fillMaxSize().padding(all = 8.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TasksHeader()
                    if (homeData.tasks.isEmpty()) {
                        Lottie(modifier = Modifier.padding(16.dp), fileName = "empty-state.json")
                    } else {
                        FlowRow(modifier = Modifier.padding(top = 8.dp)) {
                            homeData.tasks.toList().sortedByDescending { it.first.name }
                                .forEach { (type, tasks) ->
                                    val taskSnapshots by tasks.collectAsState(null)
                                    TypeCard(type, taskSnapshots?.documents?.map {
                                        it.data()
                                    } ?: emptyList())
                                }
                        }
                    }
                }
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    coScope.launch {
                        navigator.push(AddTasksScreen())
                    }
                }) {
                    Text("Add Tasks")
                }
            }
        } ?: run {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @Composable
    fun TasksHeader() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val navigator = LocalNavigator.currentOrThrow
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
                    bottomSheetNavigator.show(SettingsSheet(navigator))
                }
            ) {
                Icon(Icons.Rounded.Settings, "")
            }
        }
    }

    @Composable
    fun TypeCard(type: TaskType, tasks: List<Task>) {
        val coScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(4.dp)
                .background(Color(type.color), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                modifier = Modifier.background(color = Color(type.color))
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
                                coScope.launch {
                                    TaskService.setTask(task.copy(complete = !task.complete), type)
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (task.complete) {
                                    Color(type.color)
                                } else {
                                    MaterialTheme.colorScheme.background
                                }
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Icon(Icons.Filled.Check, "", tint = Color(type.color))
                        }
                    }
                }
            }
        }
    }
}