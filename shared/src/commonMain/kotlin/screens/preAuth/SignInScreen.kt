package screens.preAuth

import KottieAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import animateKottieCompositionAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import rememberKottieComposition
import screens.home.HomeScreen


class SignInScreen: Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun Lottie(modifier: Modifier = Modifier) {
        val composition = rememberKottieComposition(
            KottieCompositionSpec.File(resource("flower-animation.json"))
        )

        val animationState by animateKottieCompositionAsState(
            composition = composition,
            speed = 1f,
            iterations = Int.MAX_VALUE
        )

        KottieAnimation(
            composition = composition,
            progress = { animationState.progress },
            modifier = modifier.fillMaxSize(),
        )

        LaunchedEffect(
            key1 = animationState.isPlaying
        ) {
            if (animationState.isPlaying) {
                println("Animation Playing")
            }
            if (animationState.isCompleted) {
                println("Animation Completed")
            }
        }

    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var dialogText by remember { mutableStateOf("") }

        val coScope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().aspectRatio(.9f)) {
                Lottie()
            }
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = "Email"
                )
                OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = username,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    maxLines = 1,
                    onValueChange = {
                        username = it
                })
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = "Password"
                )
                OutlinedTextField(modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = password,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        password = it
                    })
                Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                    onClick = {
                        coScope.launch {
                            try {
                                Firebase.auth.signInWithEmailAndPassword(username, password).user?.let {
                                    navigator.replaceAll(HomeScreen())
                                } ?: run {
                                    dialogText = "Unexpected error occured"
                                }
                            } catch (e: Exception) {
                                dialogText = e.message ?: ""
                            }
                        }
                    }) {
                    Text("Sign In")
                }
                TextButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                    coScope.launch {
                        navigator.push(SignUpScreen())
                    }
                }) {
                    Text("Sign up")
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