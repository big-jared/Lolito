import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.materialkolor.AnimatedDynamicMaterialTheme
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

val green = Color(0xff27ae60)
val lightGreen = Color(0xff2ecc71)
val turquoise = Color(0xff16a085)
val lightTurquoise = Color(0xff1abc9c)
val blue = Color(0xff2980b9)
val lightBlue = Color(0xff3498db)
val purple = Color(0xff8e44ad)
val lightPurple = Color(0xff9b59b6)
val navy = Color(0xff2c3e50)
val lightNavy = Color(0xff34495e)
val lightLightNavy = Color(0xff54697e)
val yellow = Color(0xfff39c12)
val lightYellow = Color(0xfff1c40f)
val orange = Color(0xffd35400)
val lightOrange = Color(0xffe67e22)
val red = Color(0xffc0392b)
val lightRed = Color(0xffe74c3c)

val lightGrey = Color(0xffD5D5D5)
val medGrey = Color(0xff929292)
val darkGrey = Color(0xff646464)

var color = mutableStateOf(navy)
var appStyle = mutableStateOf(PaletteStyle.FruitSalad)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    DynamicMaterialTheme(
        animate = true,
        seedColor = color.value,
        style = appStyle.value,
        isExtendedFidelity = false,
        content = content
    )
}