package services

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import models.Task
import models.TaskType

object TaskService {

    suspend fun getTasks(): Map<TaskType, List<Task>> = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        val types: List<TaskType> = Firebase.firestore.collection("$groupPath/taskTypes").get().documents.map { it.data() }
        types.associateWith { type -> Firebase.firestore.collection("$groupPath/taskTypes/${type.id}/tasks").get().documents.map { it.data() }}
    }

    suspend fun addTask(task: Task, taskType: TaskType) = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        Firebase.firestore.document("$groupPath/taskTypes/${taskType.name}").set(taskType)
        Firebase.firestore.document("$groupPath/taskTypes/${taskType.name}/tasks/${task.name}").set(task)
    }
}