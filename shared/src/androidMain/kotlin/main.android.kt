import android.net.Uri
import androidx.compose.runtime.Composable
import dev.gitlive.firebase.storage.File
import java.util.UUID

actual fun getPlatformName(): String = "Android"

actual fun randomUUID(): String = UUID.randomUUID().toString()
actual fun toFile(path: String): File = File(Uri.parse(path))

@Composable fun MainView() = App()
