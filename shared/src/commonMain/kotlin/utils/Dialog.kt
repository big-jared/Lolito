package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

data class DialogData(
    val content: (@Composable () -> Unit)? = null,
)


sealed class DialogState {
    data object NotShowing : DialogState()
    data class Showing(val data: DialogData) : DialogState()
}

object DialogCoordinator {
    var state = mutableStateOf<DialogState>(DialogState.NotShowing)

    fun show(dialog: DialogData) {
        if (state.value is DialogState.Showing) return
        state.value = DialogState.Showing(dialog)
    }

    fun close() {
        state.value = DialogState.NotShowing
    }
}