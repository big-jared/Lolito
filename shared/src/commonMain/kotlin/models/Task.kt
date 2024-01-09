package models

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import randomUUID

enum class RepeatInterval {
    NONE, DAILY, WEEKLY, MONTHLY
}

@Serializable
data class Task(
    val id: String = randomUUID(),
    val name: String,
    val creator: User,
    val assignee: User? = null,
    val complete: Boolean,
    val createdDate: Instant = now(),
    val dueDate: Instant = now(),
    val repeatInterval: RepeatInterval = RepeatInterval.NONE
)

@Serializable
data class TaskType(
    val id: String = randomUUID(),
    val name: String,
    val color: Int,
)