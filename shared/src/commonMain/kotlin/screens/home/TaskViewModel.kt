package screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Task
import models.TaskType
import services.TaskService

object TaskViewModel {
    var taskMap: MutableState<Map<TaskType, List<Task>>?> = mutableStateOf(null)

    init {
        CoroutineScope(Dispatchers.IO).launch { update() }
    }

    suspend fun update() = withContext(Dispatchers.IO) {
        taskMap.value = TaskService.getTaskTypes().associateWith { TaskService.getTasks(it) }
    }
}