package models

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import blue
import com.materialkolor.ktx.harmonizeWithPrimary
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import randomUUID
import screens.schedule.Schedulable

enum class RepeatInterval {
    NONE, DAILY, WEEKLY, MONTHLY
}

@Serializable
data class Task(
    val id: String = randomUUID(),
    val name: String,
    val creator: User,
    val assignees: List<User> = emptyList(),
    val complete: Boolean = false,
    val createdDate: Instant = now(),
    val dueDate: Instant? = null,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val notes: String? = null,
) : Schedulable {
    override val time: Instant? = dueDate
}

@Serializable
data class TaskType(
    val id: String = randomUUID(),
    val name: String,
    val color: Int,
) {
    override fun equals(other: Any?): Boolean {
        return (other as? TaskType)?.id == this.id
    }

    @Composable
    fun derivedColor(): Color = MaterialTheme.colorScheme.harmonizeWithPrimary(Color(color))
}

val defaultTaskType = TaskType(
    name = "Tasks",
    color = blue.toArgb()
)