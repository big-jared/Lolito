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
import screens.create.TaskScreenModel
import services.TaskService

object TaskViewModel {
    var taskMap: MutableState<Map<TaskType, List<Task>>?> = mutableStateOf(null)

    init {
        CoroutineScope(Dispatchers.IO).launch { update() }
    }

    suspend fun update() = withContext(Dispatchers.IO) {
        taskMap.value = TaskService.getTaskTypes().associateWith { TaskService.getTasks(it) }
    }

    suspend fun putType(taskType: TaskType) = withContext(Dispatchers.IO) {
        TaskService.setTaskType(taskType)
        update()
    }

    suspend fun putTask(screenTask: TaskScreenModel) = withContext(Dispatchers.IO) {
        screenTask.toTasks().forEach {
            TaskService.setTask(it, screenTask.taskType.value ?: return@withContext)
        }
        update()
    }

    suspend fun delete(taskType: TaskType) = withContext(Dispatchers.IO) {
        TaskService.deleteTaskType(taskType)
        update()
    }

    suspend fun delete(task: Task, taskType: TaskType) = withContext(Dispatchers.IO) {
        TaskService.deleteTask(task, taskType)
        update()
    }

    fun allTasks() = taskMap.value?.values?.flatten()?.toSet() ?: emptySet()
}