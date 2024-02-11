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
        val groupPath = GroupService.getActiveGroupPath()
        return@withContext Firebase.firestore.collection("$groupPath/taskTypes/${type.name}/tasks").get().documents.map { it.data() }
    }

    suspend fun getTaskTypes(): List<TaskType> = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        return@withContext Firebase.firestore.collection("${groupPath}/taskTypes").get().documents.map { it.data() }
    }

    suspend fun setTask(task: Task, taskType: TaskType) = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        Firebase.firestore.document("$groupPath/taskTypes/${taskType.id}/tasks/${task.name}").set(task)
    }

    suspend fun setTaskType(taskType: TaskType) = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        Firebase.firestore.document("$groupPath/taskTypes/${taskType.id}").set(taskType)
    }
}