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

object TaskRepository {

    var taskMap: MutableState<Map<TaskType, List<Task>>?> = mutableStateOf(null)

    init {
        CoroutineScope(Dispatchers.IO).launch { update() }
    }

    suspend fun update() = withContext(Dispatchers.IO) {
        taskMap.value = TaskService.getTaskTypes().associateWith { TaskService.getTasks(it) }
    }

    suspend fun putType(taskType: TaskType) = withContext(Dispatchers.IO) {
        TaskService.setTaskType(taskType)
        taskMap.value = taskMap.value?.toMutableMap()?.apply {
            put(taskType, this[taskType] ?: emptyList())
        }
    }

    suspend fun putTask(screenTask: TaskScreenModel) = withContext(Dispatchers.IO) {
        val tasks = screenTask.toTasks()
        val type = screenTask.taskType.value ?: return@withContext

        tasks.forEach {
            TaskService.setTask(it, type)
        }
        taskMap.value = taskMap.value?.toMutableMap()?.apply {
            tasks.forEach { task ->
                put(type, this[type]?.toMutableList()?.apply {
                    removeAll { it.id == task.id }
                    add(task)
                }?.toList() ?: emptyList())
            }
        }
    }

    suspend fun delete(taskType: TaskType) = withContext(Dispatchers.IO) {
        TaskService.deleteTaskType(taskType)
        taskMap.value = taskMap.value?.toMutableMap()?.apply {
            this.remove(taskType)
        }
    }

    suspend fun delete(task: Task, taskType: TaskType) = withContext(Dispatchers.IO) {
        TaskService.deleteTask(task, taskType)
        taskMap.value = taskMap.value?.toMutableMap()?.apply {
            put(taskType, this.get(taskType)?.toMutableList()?.apply {
                removeAll { it.id == task.id }
            }?.toList() ?: emptyList())
        }
    }

    fun allTasks() = taskMap.value?.values?.flatten()?.toSet() ?: emptySet()
}