package screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import blue
import cafe.adriel.voyager.core.screen.Screen

class AddTasksScreen: Screen {

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(blue))
    }
}