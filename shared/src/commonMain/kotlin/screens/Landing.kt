package screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import darkGrey
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import lightGrey
import medGrey
import screens.home.HomeScreen

class Landing : Screen {

    enum class LandingScreens() {
        INTRO, SIGN_IN, SIGN_UP
    }

    @Composable
    override fun Content() {
        var currentScreen by remember { mutableStateOf(LandingScreens.INTRO) }
        val animatePercent = remember { Animatable(0f) }
        LaunchedEffect(animatePercent, currentScreen) {
            animatePercent.animateTo(
                targetValue = if (animatePercent.value > 0f) 0f else 2f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(lightGrey)) {
            Box(modifier = Modifier.fillMaxSize().clipToBounds().drawWithContent {
                val centerY = size.height * 3 * ((animatePercent.value / 4f) + .25f)
                drawCircle(
                    color = darkGrey,
                    radius = centerY,
                    center = Offset(size.width / 2, size.height * 2),
                    style = Fill
                )
            })

            when (currentScreen) {
                LandingScreens.INTRO -> IntroScreen(animatePercent) { currentScreen = it }
                LandingScreens.SIGN_IN -> LoginScreen(animatePercent) { currentScreen = it }
                LandingScreens.SIGN_UP -> LoginScreen(animatePercent) { currentScreen = it }
            }
        }
    }

    @Composable
    fun BoxScope.IntroScreen(animatePercent: Animatable<Float, AnimationVector1D>, setScreen: (LandingScreens) -> Unit) {
        Column(modifier = Modifier.align(Center)) {
            Text(
                modifier = Modifier.align(CenterHorizontally).padding(top = 24.dp),
                text = "Micro",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                modifier = Modifier.align(CenterHorizontally).padding(bottom = 8.dp),
                text = "(management)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Box(modifier = Modifier.fillMaxSize().drawWithContent {
            val centerY = size.height * 3 * ((animatePercent.value / 4f) + .25f)
            clipPath(path = Path().apply {
                this.addOval(
                    Rect(
                        left = (size.width / 2) - centerY,
                        right = (size.width / 2) + centerY,
                        bottom = (size.height * 2) + centerY,
                        top = (size.height * 2) - centerY,
                    )
                )
            }) {
                this@drawWithContent.drawContent()
            }
        }) {
            Column(modifier = Modifier.align(Center)) {
                Text(
                    modifier = Modifier.align(CenterHorizontally).padding(top = 24.dp),
                    text = "Micro",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    modifier = Modifier.align(CenterHorizontally).padding(bottom = 8.dp),
                    text = "(management)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Box(
            modifier = Modifier.align(BottomCenter).padding(bottom = 20.dp).size(84.dp)
                .clickable {
                    setScreen(LandingScreens.SIGN_IN)
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
                modifier = Modifier.size(32.dp).align(Center),
                painter = rememberVectorPainter(Icons.Rounded.ArrowForward),
                contentDescription = null,
                tint = darkGrey
            )
        }
    }

    @Composable
    fun BoxScope.LoginScreen(animatePercent: Animatable<Float, AnimationVector1D>, setScreen: (LandingScreens) -> Unit) {
        val navigator = LocalNavigator.currentOrThrow

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        val coScope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.fillMaxWidth().align(BottomCenter).padding(bottom = 24.dp)) {
                Text(modifier = Modifier.align(CenterHorizontally).padding(vertical = 8.dp), text = "Username/Email")
                OutlinedTextField(
                    modifier = Modifier.align(CenterHorizontally),
                    value = username,
                    onValueChange = {
                        username = it
                    })
                Text(modifier = Modifier.align(CenterHorizontally).padding(vertical = 8.dp), text = "Password")
                OutlinedTextField(
                    modifier = Modifier.align(CenterHorizontally),
                    value = password,
                    onValueChange = {
                        password = it
                    })
                Button(modifier = Modifier.align(CenterHorizontally).padding(top = 24.dp), onClick = {
                    coScope.launch {
                        Firebase.auth.signInWithEmailAndPassword(username, password)
                    }
                }) {
                    Text("Sign In")
                }
                TextButton(modifier = Modifier.align(CenterHorizontally), onClick = {
                    navigator.push(SignUp())
                }) {
                    Text("Sign up")
                }
            }
        }
    }
}

class SignUp : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var dialogText by remember { mutableStateOf("") }

        val coScope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Email")
                OutlinedTextField(username, {
                    username = it
                })
                Text("Password")
                OutlinedTextField(password, {
                    password = it
                })
                Button({
                    coScope.launch {
                        Firebase.auth.createUserWithEmailAndPassword(
                            username,
                            password
                        ).user?.let {
                            navigator.replaceAll(HomeScreen())
                        } ?: run {
                            dialogText = ""
                        }
                    }
                }) {
                    Text("Sign Up")
                }
            }

            if (dialogText.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { dialogText = "" },
                    text = {
                        Text(dialogText)
                    },
                    confirmButton = {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { dialogText = "" },
                            content = {
                                Text("Ok")
                            }
                        )
                    },
                )
            }
        }
    }
}
