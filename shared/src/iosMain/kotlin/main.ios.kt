import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSUUID

actual fun getPlatformName(): String = "iOS"

actual fun randomUUID(): String = NSUUID().UUIDString()

fun MainViewController() = ComposeUIViewController { App() }