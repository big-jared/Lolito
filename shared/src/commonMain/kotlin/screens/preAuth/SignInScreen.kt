package screens.preAuth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import screens.SplashScreen
import utils.Lottie

class SignInScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var dialogText by remember { mutableStateOf("") }

        val coScope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                text = "TEst Email"
            )
            OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally)
                .imePadding(),
                value = username,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                maxLines = 1,
                onValueChange = {
                    username = it.trim()
                })
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                text = "Password"
            )
            OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally)
                .imePadding(),
                value = password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = {
                    password = it.trim()
                })
            Button(modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
                onClick = {
                    coScope.launch {
                        try {
                            Firebase.auth.signInWithEmailAndPassword(
                                username,
                                password
                            ).user?.let {
                                navigator.replaceAll(SplashScreen())
                            } ?: run {
                                dialogText = "Unexpected error occurred"
                            }
                        } catch (e: Exception) {
                            dialogText = e.message ?: ""
                        }
                    }
                }) {
                Text("Sign In")
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    coScope.launch {
                        navigator.push(SignUpScreen())
                    }
                }) {
                Text("Sign up")
            }

            if (dialogText.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { dialogText = "" },
                    text = {
                        Text(dialogText)
                    },
                    confirmButton = {
                        Button(modifier = Modifier.fillMaxWidth(),
                            onClick = { dialogText = "" },
                            content = {
                                Text("Ok")
                            })
                    },
                )
            }
        }
    }
}