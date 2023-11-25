package screens.preAuth

import LocalNavigator
import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import darkGrey
import kotlinx.coroutines.launch
import lightGrey
import medGrey


class Landing : Screen() {
    @Composable
    override fun BoxScope.Content() {
        val navigator = LocalNavigator.current
        val coScope = rememberCoroutineScope()

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                text = "Micro",
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                text = "(management)",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp).size(84.dp)
            .clickable {
                coScope.launch {
                    navigator.changeScreen(SignInScreen())
                }
            }) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(shape = CircleShape, color = medGrey)
            )
            Box(
                modifier = Modifier.fillMaxSize().padding(8.dp)
                    .background(shape = CircleShape, color = lightGrey)
            )
            Icon(
                modifier = Modifier.size(32.dp).align(Alignment.Center),
                painter = rememberVectorPainter(Icons.Rounded.ArrowForward),
                contentDescription = null,
                tint = darkGrey
            )
        }
    }
}
