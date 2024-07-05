import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import micro.shared.generated.resources.Res
import micro.shared.generated.resources.firacode_bold
import micro.shared.generated.resources.firacode_light
import micro.shared.generated.resources.firacode_medium
import micro.shared.generated.resources.firacode_regular
import micro.shared.generated.resources.firacode_retina
import models.AlertTone
import models.Tone
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import utils.decreaseContrast

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
var defaultTone = mutableStateOf(Tone(AlertTone.Casual.name))

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FiraFontFamily() = FontFamily(
    Font(Res.font.firacode_light, FontWeight.Light),
    Font(Res.font.firacode_medium, FontWeight.Normal),
    Font(Res.font.firacode_medium, FontWeight.Medium),
    Font(Res.font.firacode_bold, FontWeight.Bold),
    Font(Res.font.firacode_retina, FontWeight.Thin),
)

@Composable
fun FiraTypography() = Typography().run {
    val fontFamily = FiraFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        displayMedium = displayMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        displaySmall = displaySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleMedium = titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleSmall = titleSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily, fontWeight = FontWeight.Medium),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodySmall = bodySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelLarge = labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelMedium = labelMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelSmall = labelSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium)
    )
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    DynamicMaterialTheme(
        animate = true,
        seedColor = color.value,
        style = appStyle.value,
        isExtendedFidelity = false,
        typography = FiraTypography(),
        content = content
    )
}

@Composable
fun backgroundContainer(): Color = MaterialTheme.colorScheme.background.decreaseContrast(1.5f)