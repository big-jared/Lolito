package models

import kotlinx.serialization.Serializable

@Serializable
data class TaskType(
    val name: String,
    val lightColor: Int,
    val darkColor: Int,
)