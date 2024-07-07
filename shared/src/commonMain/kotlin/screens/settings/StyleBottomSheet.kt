package screens.settings

import BottomSheetScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import darkTheme
import utils.AppIconButton

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
            
        }
    }
}