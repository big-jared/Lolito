package models

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    var groupId: String = "",
    val groupName: String
) {
    override fun equals(other: Any?): Boolean {
        return other != null && other is Group && other.groupId == this.groupId
    }
}

@Serializable
data class User(
    val userId: String,
    val displayName: String,
)