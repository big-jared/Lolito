package services

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.QuerySnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import models.Task
import models.TaskType

object TaskService {

    suspend fun getTasks(): Map<TaskType, Flow<QuerySnapshot>> = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        val types: List<TaskType> = Firebase.firestore.collection("$groupPath/taskTypes").get().documents.map { it.data() }
        types.associateWith { type -> Firebase.firestore.collection("$groupPath/taskTypes/${type.name}/tasks").snapshots() }
    }

    suspend fun getTaskTypes(): Flow<QuerySnapshot> = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        return@withContext Firebase.firestore.collection("$groupPath/taskTypes").snapshots()
    }

    suspend fun setTask(task: Task, taskType: TaskType) = withContext(Dispatchers.IO) {
        val groupPath = GroupService.getActiveGroupPath()
        Firebase.firestore.document("$groupPath/taskTypes/${taskType.name}").set(taskType)
        Firebase.firestore.document("$groupPath/taskTypes/${taskType.name}/tasks/${task.name}").set(task)
    }
}