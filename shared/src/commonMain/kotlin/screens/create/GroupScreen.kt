package screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import models.Group
import screens.SplashScreen
import screens.home.TaskViewModel
import services.GroupService

data class GroupScreenData(
    val activeGroup: Group?,
    val allGroups: List<Group>,
)

class GroupScreen : Screen {
    @Composable
    override fun Content() {
        val coScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        var groupScreenData by remember { mutableStateOf<GroupScreenData?>(null) }

        LaunchedEffect(null) {
            groupScreenData = GroupScreenData(
                activeGroup = GroupService.getActiveGroup(),
                allGroups = GroupService.getAllGroups()
            )
        }

        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            groupScreenData?.let { groupData ->
                if (groupData.allGroups.isEmpty()) {
                    EmptyState()
                } else {
                    Column {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                            textAlign = TextAlign.Center,
                            text = "Switch groups",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                        LazyColumn {
                            items(groupData.allGroups.size) {
                                GroupItem(
                                    group = groupData.allGroups[it],
                                    groupData.activeGroup == groupData.allGroups[it]
                                ) { groupName ->
                                    groupScreenData = groupData.copy(activeGroup = groupName)
                                }
                            }
                        }
                        if (groupData.activeGroup != null) {
                            Button(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    coScope.launch {
                                        GroupService.setGroupActive(groupData.activeGroup)
                                        TaskViewModel.update()
                                        navigator.popUntilRoot()
                                    }
                                }) {
                                Text("Switch to ${groupData.activeGroup.groupName}")
                            }
                            TextButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    coScope.launch {
                                        navigator.push(CreateGroupScreen())
                                    }
                                }) {
                                Text("Create a Group")
                            }
                        }
                    }
                }
            } ?: run {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @Composable
    fun GroupItem(group: Group, active: Boolean, onClick: (Group) -> Unit) {
        val color =
            if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
        Surface(
            modifier = Modifier.fillMaxSize().padding(8.dp)
                .background(color = color, RoundedCornerShape(16.dp))
                .border(2.dp, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick(group) },
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
        ) {
            Row{
                Text(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    text = group.groupName,
                )
                if (active) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically)
                            .padding(end = 16.dp)
                            .size(20.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    @Composable
    fun BoxScope.EmptyState() {
        val coScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        Column {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                text = "Hello there!",
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                textAlign = TextAlign.Center,
                text = "To get started you need to create or join a group",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Column(modifier = Modifier.align(Alignment.Center)) {
            Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                onClick = {
                    coScope.launch {
                        navigator.push(JoinGroupScreen())
                    }
                }) {
                Text("Join a Group")
            }
            TextButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                coScope.launch {
                    navigator.push(CreateGroupScreen())
                }
            }) {
                Text("Create a Group")
            }
        }
    }
}