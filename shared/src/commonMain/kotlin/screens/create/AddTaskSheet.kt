package screens.create

import BottomSheetScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import blue
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import darkGrey
import dev.gitlive.firebase.firestore.QuerySnapshot
import green
import kotlinx.coroutines.flow.Flow
import lightBlue
import lightGreen
import lightGrey
import lightNavy
import lightOrange
import lightPurple
import lightRed
import lightYellow
import models.Task
import models.User
import navy
import orange
import purple
import red
import screens.uid
import services.TaskService
import services.UserService
import utils.ColorHintCircle
import utils.HighlightBox
import utils.ProfileIcon
import yellow

data class TaskSheetData(
    val types: Flow<QuerySnapshot>
)

class TaskSheet(task: Task? = null) : BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        val navigator = LocalBottomSheetNavigator.current
        var title by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf<Color?>(null) }
        var usingAdvanced by remember { mutableStateOf(false) }

        var user: User? by remember { mutableStateOf(null) }

        LaunchedEffect(null) {
            user = UserService.getUser(uid ?: return@LaunchedEffect)
            val types = TaskService.getTaskTypes()

        }

        val horizontalPadding = Modifier.padding(horizontal = 16.dp)
        Column(modifier = Modifier.padding(vertical = 16.dp).verticalScroll(rememberScrollState())) {
            Row(modifier = horizontalPadding) {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    text = "Create new task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { navigator.hide() }) {
                    Icon(Icons.Rounded.Close, "")
                }
            }
            Text(modifier = horizontalPadding.padding(top = 16.dp), text = "Title")
            OutlinedTextField(modifier = horizontalPadding.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Redesign this app") })
            Row(modifier = horizontalPadding.padding(top = 32.dp)) {
                Text(
                    "Project",
                    Modifier.weight(1f).align(Alignment.CenterVertically).padding(vertical = 8.dp)
                )
                HighlightBox(modifier = Modifier.align(Alignment.CenterVertically),
                    text = "Sani Mobile",
                    backgroundColor = lightGrey,
                    color = darkGrey,
                    frontIcon = { ColorHintCircle(Modifier.size(24.dp), blue) })
                FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { navigator.hide() }) {
                    Icon(Icons.Rounded.Add, "")
                }
            }
            Divider(modifier = horizontalPadding.padding(top = 8.dp))
            Row(modifier = horizontalPadding.padding(top = 16.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
                        .padding(vertical = 8.dp),
                    text = "Due Date"
                )
                HighlightBox(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "Today",
                    color = red,
                    backgroundColor = lightRed,
                    frontIcon = {
                        Icon(
                            painter = rememberVectorPainter(
                                Icons.Default.DateRange
                            ),
                            contentDescription = "",
                            tint = red
                        )
                    })
            }
            Divider(modifier = horizontalPadding.padding(top = 8.dp))
            Text(modifier = horizontalPadding.padding(top = 16.dp), text = "Color")
            Row(modifier = Modifier.padding(top = 16.dp).height(84.dp).horizontalScroll(rememberScrollState())) {
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
                ).forEach {
                    ColorHintCircle(
                        modifier = Modifier.size(animateDpAsState(if (selectedColor == it) 84.dp else 64.dp).value)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .clickable { selectedColor = it }
                            .align(Alignment.CenterVertically)
                        , it
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(modifier = horizontalPadding.padding(top = 16.dp), text = "Assignee")
            Row(modifier = horizontalPadding.padding(top = 16.dp)) {
                ProfileIcon(modifier = Modifier.size(64.dp))
                FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(start = 16.dp).size(64.dp),
                    onClick = { navigator.hide() }) {
                    Icon(Icons.Rounded.Add, "")
                }
            }
            TextButton(
                modifier = horizontalPadding.padding(top = 16.dp).align(Alignment.End),
                onClick = {
                    usingAdvanced = usingAdvanced.not()
                }) {
                Row {
                    Text(
                        "Advanced Settings",
                        color = blue,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        modifier = Modifier.rotate(animateFloatAsState(if (usingAdvanced) 270f else 90f).value),
                        painter = rememberVectorPainter(
                            Icons.Default.KeyboardArrowRight
                        ),
                        contentDescription = "",
                        tint = blue,
                    )
                }
            }

            AnimatedVisibility (usingAdvanced) {
                Column {
                    Text(modifier = horizontalPadding.padding(top = 16.dp), text = "Notes")
                    OutlinedTextField(modifier = horizontalPadding.fillMaxWidth().padding(top = 8.dp).defaultMinSize(minHeight = 120.dp),
                        shape = RoundedCornerShape(8.dp),
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Redesign this app") })
                }
            }

            Button(
                modifier = horizontalPadding.fillMaxWidth().height(64.dp).padding(top = 16.dp),
                onClick = {},
                shape = RoundedCornerShape(32.dp),
            ) {
                Text("Done", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}