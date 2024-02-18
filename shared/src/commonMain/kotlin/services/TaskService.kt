package services

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import models.Task
import models.TaskType

object TaskService {

    suspend fun getTasks(type: TaskType): List<Task> = withContext(Dispatchers.IO) {
        try {
            val groupPath = GroupService.getActiveGroupPath()
            return@withContext Firebase.firestore.collection("$groupPath/taskTypes/${type.id}/tasks").get().documents.map { it.data() }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun getTaskTypes(): List<TaskType> = withContext(Dispatchers.IO) {
        try {
            val groupPath = GroupService.getActiveGroupPath()
            return@withContext Firebase.firestore.collection("${groupPath}/taskTypes").get().documents.map { it.data() }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun setTask(task: Task, taskType: TaskType) = withContext(Dispatchers.IO) {
        if (taskType.id.isEmpty() || taskType.name.isEmpty() || task.id.isEmpty() || task.name.isEmpty()) return@withContext
        try {
            val groupPath = GroupService.getActiveGroupPath()
            Firebase.firestore.document("$groupPath/taskTypes/${taskType.id}/tasks/${task.id}")
                .set(task)
        } catch (e: Exception) {}
    }

    suspend fun setTaskType(taskType: TaskType) = withContext(Dispatchers.IO) {
        if (taskType.id.isEmpty() || taskType.name.isEmpty()) return@withContext
        try {
            val groupPath = GroupService.getActiveGroupPath()
            Firebase.firestore.document("$groupPath/taskTypes/${taskType.id}").set(taskType)
        } catch (e: Exception) { }
    }
}