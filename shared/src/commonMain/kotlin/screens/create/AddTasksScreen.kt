package screens.create

import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import blue

class AddTasksScreen: Screen() {

    @Composable
    override fun BoxScope.Content() {
        Box(modifier = Modifier.fillMaxSize().background(blue))
    }
}