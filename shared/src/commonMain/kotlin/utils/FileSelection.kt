package utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.ProgressFlow
import dev.gitlive.firebase.storage.storage
import green
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lighterGrey
import screens.uid
import toFile

sealed class FileSelectionState {
    data object NotSelecting : FileSelectionState()
    data object PickingFile : FileSelectionState()
    class Uploading(val fileUploadProgress: ProgressFlow) : FileSelectionState()
}

object FileProcessor {
    var state = mutableStateOf<FileSelectionState>(FileSelectionState.NotSelecting)

    suspend fun uploadFile(fileName: String) = withContext(Dispatchers.IO) {
        val uploadingFlow = Firebase.storage.reference("$uid/profile-image").putFileResumable(toFile(fileName))
        state.value = FileSelectionState.Uploading(uploadingFlow)
    }

    fun startFileSelection() {
        state.value = FileSelectionState.PickingFile
    }

    fun completeFileTransfer() {
        state.value = FileSelectionState.NotSelecting
    }
}

@Composable
fun FileSelector() {
    val coScope = rememberCoroutineScope()
    val fileState = FileProcessor.state.value
    FilePicker(show = fileState is FileSelectionState.PickingFile, fileExtensions = listOf("jpg", "png")) { platformFile ->
        coScope.launch {
            FileProcessor.uploadFile(platformFile?.path ?: return@launch)
        }
    }

    if (fileState is FileSelectionState.Uploading) {
        val progress = fileState.fileUploadProgress.collectAsState(null)
        val percentage = (progress.value?.bytesTransferred?.toFloat() ?: 0f) / (progress.value?.totalByteCount?.toFloat() ?: 0f)
        val complete = percentage == 1f

        AlertDialog(
            onDismissRequest = {},
            title = {
                Box(Modifier.fillMaxWidth()) {
                    Text(modifier = Modifier.align(Alignment.Center), text = if (complete) "Upload Complete" else "Uploading Photo")
                }
            },
            text = {
                Box(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    CircularProgress(
                        modifier = Modifier.align(Alignment.Center).size(52.dp),
                        progress = percentage,
                        activeColor = green
                    )
                    AnimatedVisibility (modifier = Modifier.align(Alignment.Center), visible = complete, enter = fadeIn()) {
                        Box(modifier = Modifier.size(52.dp).background(color = green, shape = CircleShape)) {
                            Icon(modifier = Modifier.align(Alignment.Center), imageVector = Icons.Filled.Check, contentDescription = "", tint = lighterGrey)
                        }
                    }
                }
            },
            confirmButton = {
                if (complete) {
                    Button(modifier = Modifier.fillMaxWidth(),
                        onClick = { FileProcessor.completeFileTransfer() },
                        content = {
                            Text("Continue")
                        })
                }
            },
        )
    }
}