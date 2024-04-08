package utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

fun showSimpleDialog(
    dialogText: String
) {
    DialogCoordinator.show(
        DialogData {
            DialogColumn(Modifier.padding(horizontal = 16.dp)) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(dialogText)
                    Button(modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        onClick = { DialogCoordinator.close() },
                        content = {
                            Text("Ok")
                        })
                }
            }
        }
    )
}

@Composable
fun DialogColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp)).padding(vertical = 16.dp)) {
        content()
    }
}