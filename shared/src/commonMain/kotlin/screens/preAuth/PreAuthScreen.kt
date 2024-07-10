package screens.preAuth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import appStyle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import color
import darkTheme
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import models.User
import screens.SplashScreen
import screens.settings.StyleBottomSheet
import screens.uid
import services.UserService
import services.UserService.getUser
import utils.AppIconButton

class SignInScreenModel() {
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var displayName = mutableStateOf("")
    var error = mutableStateOf<String?>(null)

    suspend fun signIn(navigator: Navigator) {
        try {
            Firebase.auth.signInWithEmailAndPassword(
                username.value, password.value
            ).user?.let {
                val currentUser = getUser(it.uid) ?: run {
                    error.value = "Unexpected error occurred"
                    return@let
                }
                UserService.setUser(
                    currentUser.copy(
                        seedColor = color.value.toArgb(),
                        style = appStyle.value.name
                    )
                )
                navigator.replaceAll(SplashScreen())
            } ?: run {
                error.value = "Unexpected error occurred"
            }
        } catch (e: Exception) {
            error.value = e.message ?: ""
        }
    }

    suspend fun signUp(navigator: Navigator) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(
                username.value, password.value
            ).user?.let {
                UserService.setUser(
                    User(
                        it.uid,
                        seedColor = color.value.toArgb(),
                        displayName = displayName.value,
                        style = appStyle.value.name
                    )
                )
                navigator.replaceAll(SplashScreen())
            } ?: run {
                error.value = "Unexpected error occured"
            }
        } catch (e: Exception) {
            error.value = e.message ?: ""
        }
    }
}

class PreAuthScreen : Screen {
    val signInScreenModel = SignInScreenModel()

    @Composable
    override fun Content() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Header()
            Spacer(modifier = Modifier.weight(1f))
            AuthSection()

            signInScreenModel.error.value?.let { error ->
                AlertDialog(
                    onDismissRequest = {
                        signInScreenModel.error.value = null
                    },
                    confirmButton = {
                        Button(modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(top = 24.dp), onClick = {
                            signInScreenModel.error.value = null
                        }) {
                            Text(
                                "Ok",
                            )
                        }
                    },
                    modifier = Modifier,
                    title = {
                        Text("Error Occurred")
                    },
                    text = {
                        Text(error)
                    },
                )
            }
        }
    }

    @Composable
    fun Header() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Box(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = "Welcome to",
                    style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                )
                Text(
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp),
                    text = "Micro",
                    style = MaterialTheme.typography.displayLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                )
                TextButton(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), onClick = {}) {
                    Text(
                        "\"I didn't hate using this app\"\n- Someone",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                AppIconButton(modifier = Modifier.align(Alignment.TopEnd),
                    painter = rememberVectorPainter(Icons.Rounded.Palette),
                    onClick = {
                        bottomSheetNavigator.show(StyleBottomSheet())
                    })
            }
        }
    }

    @Composable
    fun ColumnScope.AuthSection() {
        val coScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        var animateIn by remember { mutableStateOf(false) }
        var isSignIn by remember { mutableStateOf(true) }

        LaunchedEffect(null) {
            animateIn = true
        }

        val focusManager = LocalFocusManager.current

        AnimatedVisibility(animateIn, enter = slideInVertically(initialOffsetY = { it / 2 })) {
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = if (darkTheme.value
                        ?: isSystemInDarkTheme()
                ) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceDim,
            ) {
                Column(modifier = Modifier.padding(top = 16.dp).fillMaxWidth()) {

                    FilledTonalButton(modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            coScope.launch {
                                signInScreenModel.error.value = "Not yet implemented"
                            }
                        }) {
                        Text(
                            "Sign ${if (isSignIn) "In" else "Up"} with Google",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        modifier = Modifier.padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Or",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(modifier = Modifier.padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                        value = signInScreenModel.username.value,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        label = {
                            Text("Email")
                        },
                        maxLines = 1,
                        onValueChange = {
                            signInScreenModel.username.value = it.trim()
                        })
                    OutlinedTextField(modifier = Modifier.padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                        value = signInScreenModel.password.value,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isSignIn) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            },
                            onDone = {
                                coScope.launch {
                                    focusManager.clearFocus()
                                    signInScreenModel.signIn(navigator)
                                }
                            }
                        ),
                        label = {
                            Text("Password")
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = {
                            signInScreenModel.password.value = it.trim()
                        })
                    AnimatedVisibility(!isSignIn) {
                        Column(Modifier.fillMaxWidth()) {
                            OutlinedTextField(modifier = Modifier.padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                                value = signInScreenModel.confirmPassword.value,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                ),
                                label = {
                                    Text("Confirm Password")
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                onValueChange = {
                                    signInScreenModel.confirmPassword.value = it.trim()
                                })

                            OutlinedTextField(modifier = Modifier.padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                                value = signInScreenModel.displayName.value,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        coScope.launch {
                                            focusManager.clearFocus()
                                            signInScreenModel.signUp(navigator)
                                        }
                                    }
                                ),
                                label = {
                                    Text("Display Name")
                                },
                                onValueChange = {
                                    signInScreenModel.displayName.value = it.trim()
                                })
                        }
                    }
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp), onClick = {
                        coScope.launch {
                            focusManager.clearFocus()
                            if (isSignIn) {
                                signInScreenModel.signIn(navigator)
                            } else {
                                signInScreenModel.signUp(navigator)
                            }
                        }
                    }) {
                        Text(
                            if (isSignIn) "Sign In" else "Sign Up",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    TextButton(modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp), onClick = {
                        coScope.launch {
                            isSignIn = isSignIn.not()
                        }
                    }) {
                        Text(
                            if (isSignIn) "Sign Up" else "Sign In",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}