package screens.settings

import BottomSheetScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import appStyle
import color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import darkTheme
import green
import lightGreen
import lightPurple
import medGrey
import navy
import pink
import purple
import yellow

class StyleBottomSheet : BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            Header()
        }
    }

    @Composable
    fun Header(modifier: Modifier = Modifier) {
        Column(modifier = modifier.fillMaxWidth().padding(all = 16.dp)) {
            Row() {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = "Make it your",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp),
                        text = "OWN",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                val isDark = darkTheme.value ?: isSystemInDarkTheme()
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Switch(
                        checked = isDark,
                        colors = if (!isDark) SwitchDefaults.colors(
                            uncheckedIconColor = MaterialTheme.colorScheme.onSurface,
                            uncheckedThumbColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) else SwitchDefaults.colors(),
                        onCheckedChange = { checked ->
                            darkTheme.value = checked
                        },
                        thumbContent = {
                            Icon(
                                modifier = Modifier.padding(2.dp),
                                painter = rememberVectorPainter(if (isDark) Icons.Rounded.DarkMode else Icons.Rounded.LightMode),
                                contentDescription = ""
                            )
                        })
                }
            }

            Text(
                modifier = Modifier.align(Alignment.Start).padding(top = 16.dp),
                text = "Style",
                style = MaterialTheme.typography.titleMedium,
            )

            StyleSelection(
                name = "Classic",
                rootColor = navy,
                paletteStyle = PaletteStyle.FruitSalad
            )
            StyleSelection(
                name = "Princess",
                rootColor = pink,
                paletteStyle = PaletteStyle.FruitSalad
            )
            StyleSelection(
                name = "Expressive",
                rootColor = lightGreen,
                paletteStyle = PaletteStyle.Expressive
            )
            StyleSelection(
                name = "Sunshine",
                rootColor = yellow,
                paletteStyle = PaletteStyle.Vibrant
            )
            StyleSelection(name = "Gloomy", rootColor = green, paletteStyle = PaletteStyle.Neutral)
            StyleSelection(
                name = "Grayscale",
                rootColor = medGrey,
                paletteStyle = PaletteStyle.Monochrome
            )
        }
    }
}

@Composable
fun StyleSelection(
    modifier: Modifier = Modifier,
    name: String,
    rootColor: Color,
    paletteStyle: PaletteStyle
) {
    DynamicMaterialTheme(
        seedColor = rootColor,
        useDarkTheme = darkTheme.value ?: isSystemInDarkTheme(),
        style = paletteStyle
    ) {
        val background = MaterialTheme.colorScheme.surfaceContainerLowest
        val intermediate = MaterialTheme.colorScheme.surfaceContainer
        val primary = MaterialTheme.colorScheme.primaryContainer

        Column(
            modifier = modifier.fillMaxWidth().padding(top = 16.dp).background(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
            ).border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ).drawBehind {
                this.drawRoundRect(
                    Brush.linearGradient(
                        0.0f to background,
                        0.5f to intermediate,
                        1.0f to primary,
                        start = Offset(0.0f, 0.0f),
                        end = Offset(0.0f, size.height),
                    ), cornerRadius = CornerRadius(16.dp.toPx())
                )
            }.clickable {
                color.value = rootColor
                appStyle.value = paletteStyle
            }
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}