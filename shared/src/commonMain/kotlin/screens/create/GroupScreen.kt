package screens.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch

class GroupScreen : Screen {
    @Composable
    override fun Content() {
        val coScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
}