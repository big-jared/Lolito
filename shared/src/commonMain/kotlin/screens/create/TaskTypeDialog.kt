package screens.create

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import blue
import com.materialkolor.ktx.harmonizeWithPrimary
import green
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import lightBlue
import lightGreen
import lightNavy
import lightOrange
import lightPurple
import lightRed
import lightYellow
import models.TaskType
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.delete
import navy
import orange
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import purple
import red
import screens.home.TaskViewModel
import services.TaskService
import utils.AppIconButton
import utils.ColorHintCircle
import utils.DialogColumn
import utils.DialogCoordinator
import yellow

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TaskTypeDialogContent(type: TaskType?, onSave: (TaskType) -> Unit) {
    var selectedColor: Int by remember { mutableStateOf(type?.color ?: blue.toArgb()) }
    var title: String by remember { mutableStateOf(type?.name ?: "") }
    var itemPosition by remember { mutableStateOf(0f) }
    val scrollState = rememberScrollState()
    val creating = type == null
    val coScope = rememberCoroutineScope()

    LaunchedEffect(key1 = null) {
        scrollState.animateScrollTo(maxOf(0, itemPosition.toInt()))
    }

    val horizontalPadding = Modifier.padding(horizontal = 16.dp)

    DialogColumn {
        Row(modifier = horizontalPadding.fillMaxWidth()) {
            Text(
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                text = if (creating) "Create new project" else "Edit project",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            type?.let {
                AppIconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        coScope.launch(Dispatchers.IO) {
                            TaskViewModel.delete(type)
                            DialogCoordinator.close()
                        }
                    },
                    painter = painterResource(Res.drawable.delete),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                )
            }

            FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { DialogCoordinator.close() }) {
                Icon(Icons.Rounded.Close, "", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Text(modifier = horizontalPadding.padding(top = 16.dp), text = "Title")
        OutlinedTextField(modifier = horizontalPadding.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Redesign this app") })
        Row(modifier = Modifier.padding(top = 16.dp).height(84.dp).horizontalScroll(scrollState)) {
            Spacer(modifier = Modifier.width(16.dp))
            listOf(
                red,
                lightRed,
                orange,
                lightOrange,
                yellow,
                lightYellow,
                blue,
                lightBlue,
                green,
                lightGreen,
                purple,
                lightPurple,
                navy,
                lightNavy
            ).forEach { rawColor ->
                val color = MaterialTheme.colorScheme.harmonizeWithPrimary(rawColor)
                val selected = selectedColor == color.toArgb()
                ColorHintCircle(
                    modifier = Modifier.size(animateDpAsState(if (selected) 84.dp else 64.dp).value)
                        .then(
                            if (selected) Modifier.onGloballyPositioned { layoutCoordinates ->
                                itemPosition =
                                    layoutCoordinates.positionInRoot().x - layoutCoordinates.size.width
                            } else Modifier
                        )
                        .padding(8.dp)
                        .clip(CircleShape)
                        .clickable { selectedColor = color.toArgb() }
                        .align(Alignment.CenterVertically), color
                )
            }
        }
        Button(
            modifier = horizontalPadding.padding(top = 16.dp).align(Alignment.CenterHorizontally),
            onClick = {
                coScope.launch(Dispatchers.IO) {
                    val taskType = type?.copy(name = title, color = selectedColor) ?: TaskType(
                        name = title,
                        color = selectedColor
                    )
                    TaskViewModel.putType(taskType)
                    DialogCoordinator.close()
                    onSave(taskType)
                }
            },
            shape = RoundedCornerShape(32.dp),
        ) {
            Text("Done", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


