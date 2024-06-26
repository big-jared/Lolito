package models

import androidx.compose.ui.graphics.toArgb
import com.materialkolor.PaletteStyle
import defaultTone
import kotlinx.serialization.Serializable
import orange

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
    val seedColor: Int? = orange.toArgb(),
    val style: String? = PaletteStyle.FruitSalad.name,
    val tone: Tone = defaultTone.value
)