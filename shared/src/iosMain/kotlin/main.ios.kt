import androidx.compose.ui.window.ComposeUIViewController
import dev.gitlive.firebase.storage.File
import platform.Foundation.NSURL.Companion.URLWithString
import platform.Foundation.NSUUID

actual fun getPlatformName(): String = "iOS"

actual fun randomUUID(): String = NSUUID().UUIDString()

fun MainViewController() = ComposeUIViewController { App() }

actual fun toFile(path: String): File = File(URLWithString(path)!!)