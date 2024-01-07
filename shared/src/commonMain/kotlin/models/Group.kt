package models

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String = "",
    val name: String
)