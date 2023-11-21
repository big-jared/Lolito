import androidx.compose.runtime.Composable
import java.util.UUID

actual fun getPlatformName(): String = "Android"

actual fun randomUUID(): String = UUID.randomUUID().toString()

@Composable fun MainView() = App()
