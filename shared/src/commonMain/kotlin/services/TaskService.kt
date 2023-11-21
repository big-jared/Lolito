package services

import models.Task
import models.allTasks

object TaskService {
    fun completeTask(task: Task) {
        val tasks = allTasks.value.toMutableList()
        tasks.remove(task)
        tasks.add(task.copy(complete = !task.complete))

        allTasks.value = tasks
    }
}