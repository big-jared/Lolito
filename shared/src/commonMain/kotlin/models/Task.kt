package models

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import blue
import com.materialkolor.ktx.harmonizeWithPrimary
import defaultTone
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import randomUUID
import kotlin.time.Duration

enum class RepeatInterval(val title: String) {
    NONE("None"), DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly")
}

@Serializable
data class Task(
    val id: String = randomUUID(),
    val name: String,
    val creator: User,
    val assignees: Set<User> = emptySet(),
    val complete: Boolean = false,
    val createdDate: Instant = now(),
    val dueDate: Instant? = null,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val notes: String? = null,
    val duration: Duration? = null,
    val tone: Tone = defaultTone.value
) {
    fun toScheduledTask(): ScheduledTask? {
        return ScheduledTask(
            due = dueDate ?: return null,
            duration = duration ?: return null,
            assignees = assignees,
            completed = complete
        )
    }
}

data class ScheduledTask (
    val due: Instant,
    val duration: Duration,
    val assignees: Set<User>,
    val completed: Boolean,
)

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


enum class AlertTone {
    Casual, Rude, Foul, Gentle, Formal
}

@Serializable
data class Tone (
    val name: String,
    val temperature: Int = 30
)