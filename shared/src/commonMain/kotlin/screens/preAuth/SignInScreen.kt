package screens.preAuth

import LocalNavigator
import Screen
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class SignInScreen: Screen() {

    @Composable
    override fun BoxScope.Content() {
        val navigator = LocalNavigator.current

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        val coScope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 24.dp)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                text = "Username/Email"
            )
            OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                value = username,
                onValueChange = {
                    username = it
                })
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                text = "Password"
            )
            OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                value = password,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                onValueChange = {
                    password = it
                })
            Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                onClick = {
                    coScope.launch {
                        Firebase.auth.signInWithEmailAndPassword(username, password)
                    }
                }) {
                Text("Sign In")
            }
            TextButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                coScope.launch {
                    navigator.changeScreen(SignUpScreen())
                }
            }) {
                Text("Sign up")
            }
        }
    }
}