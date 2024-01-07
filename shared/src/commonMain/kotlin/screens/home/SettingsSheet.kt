package screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import screens.SplashScreen

interface BottomSheetScreen : Screen {
    @Composable
    override fun Content() {
        val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, shape = shape).clip(shape)
                .padding(top = 8.dp)
        ) {
            BottomSheetContent()
        }
    }

    @Composable
    fun ColumnScope.BottomSheetContent()
}

class SettingsSheet : BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Show tasks for User")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Button(onClick = {}) { Text("Jared") }
                Button(onClick = {}) { Text("Lauren") }
            }
            Text("Notifications")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Button(onClick = {}) { Text("Upcoming") }
                Button(onClick = {}) { Text("Complete") }
                Button(onClick = {}) { Text("Created") }
            }
            Text("Notifications")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coScope.launch {
                        try {
                            Firebase.auth.signOut()
                            navigator.replaceAll(SplashScreen())
                        } catch (e: Exception) { }
                    }
                },
                content = {
                    Text("Sign out")
                }
            )
        }
    }
}