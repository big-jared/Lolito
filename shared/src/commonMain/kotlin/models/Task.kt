package models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import blue
import green
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import lightBlue
import lightGreen
import lightPurple
import lightRed
import lightYellow
import purple
import red
import yellow
import kotlin.time.Duration.Companion.days

enum class RepeatInterval {
    NONE, DAILY, WEEKLY, MONTHLY
}

data class Task(
    val name: String,
    val creator: User,
    val assignee: User? = null,
    val complete: Boolean,
    val type: TaskType,
    val createdDate: Instant = now(),
    val dueDate: Instant = now(),
    val repeatInterval: RepeatInterval = RepeatInterval.NONE
)

data class TaskType(
    val name: String,
    val lightColor: Int,
    val darkColor: Int,
)

data class User(
    val name: String,
    val group: Group,
)

data class Group(
    val name: String
)

val chores = TaskType("Chores", lightRed.toArgb(), red.toArgb())
val groceries = TaskType("Groceries", lightBlue.toArgb(), blue.toArgb())
val bills = TaskType("Bills", lightGreen.toArgb(), green.toArgb())
val exercise = TaskType("Exercise", lightYellow.toArgb(), yellow.toArgb())
val misc = TaskType("Misc", lightPurple.toArgb(), purple.toArgb())

val defaultTaskTypes = listOf(chores, groceries, bills, misc)

val testGroup = Group(name = "Jareds Group")
val jaredUser = User(name = "Jared", group = testGroup)
val laurenUser = User(name = "Lauren", group = testGroup)
val jaredTasks = listOf(
    Task(name = "Clean room", creator = laurenUser, assignee = jaredUser, complete = false, type = chores, repeatInterval = RepeatInterval.DAILY),
    Task(name = "Dishes", creator = jaredUser, assignee = jaredUser, complete = false, type = chores, repeatInterval = RepeatInterval.DAILY),
    Task(name = "Clean Floors", creator = laurenUser, assignee = jaredUser, complete = false, type = chores, repeatInterval = RepeatInterval.DAILY),

    Task(name = "Go on run", creator = laurenUser, assignee = jaredUser, complete = false, type = exercise, repeatInterval = RepeatInterval.DAILY),
    Task(name = "Walk dogs", creator = laurenUser, assignee = jaredUser, complete = false, type = exercise, repeatInterval = RepeatInterval.DAILY),

    Task(name = "Eggs", creator = laurenUser, assignee = jaredUser, complete = false, type = groceries),
    Task(name = "Bacon", creator = laurenUser, assignee = jaredUser, complete = false, type = groceries),
    Task(name = "Hash browns", creator = laurenUser, assignee = jaredUser, complete = false, type = groceries),

    Task(name = "Expense Internet", creator = jaredUser, assignee = jaredUser, complete = false, type = bills, repeatInterval = RepeatInterval.MONTHLY, dueDate = now() + 1.days),
)

val laurenTasks = listOf(
    Task(name = "Make coffee", creator = laurenUser, assignee = laurenUser, complete = false, type = chores, repeatInterval = RepeatInterval.DAILY),

    Task(name = "Go on run", creator = laurenUser, assignee = laurenUser, complete = false, type = exercise, repeatInterval = RepeatInterval.DAILY),
    Task(name = "Walk dogs", creator = laurenUser, assignee = laurenUser, complete = false, type = exercise, repeatInterval = RepeatInterval.DAILY),

    Task(name = "Expense Internet", creator = jaredUser, assignee = laurenUser, complete = false, type = bills, repeatInterval = RepeatInterval.MONTHLY, dueDate = now() + 1.days),
)

val allTasks: MutableState<List<Task>> = mutableStateOf(jaredTasks + laurenTasks)
val currentUser = jaredUser