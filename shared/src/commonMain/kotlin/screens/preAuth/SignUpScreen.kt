package screens.preAuth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import models.User
import screens.SplashScreen
import services.UserService
import utils.Lottie

class SignUpScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var dialogText by remember { mutableStateOf("") }
        var displayName by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val coScope = rememberCoroutineScope()

        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        text = "Display Name"
                    )
                    OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                        value = displayName,
                        onValueChange = {
                            displayName = it
                        })
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        text = "Username/Email"
                    )
                    OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                        value = username,
                        onValueChange = {
                            username = it.trim()
                        })
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        text = "Password"
                    )
                    OutlinedTextField(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        value = password,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = {
                            password = it.trim()
                        },
                    )
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp),
                        onClick = {
                            coScope.launch(Dispatchers.IO) {
                                try {
                                    Firebase.auth.createUserWithEmailAndPassword(
                                        username, password
                                    ).user?.let {
                                        UserService.setUser(User(it.uid, displayName))
                                        navigator.replaceAll(SplashScreen())
                                    } ?: run {
                                        dialogText = "Unexpected error occured"
                                    }
                                } catch (e: Exception) {
                                    dialogText = e.message ?: ""
                                }
                            }
                        }) {
                        Text("Sign Up")
                    }
                    TextButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                        coScope.launch {
                            navigator.pop()
                        }
                    }) {
                        Text("Sign In")
                    }
                }
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
