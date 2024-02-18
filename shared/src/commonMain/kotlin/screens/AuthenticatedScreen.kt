package screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import screens.home.ListTab
import screens.home.ScheduleTab
import screens.home.SettingsTab


object AuthenticatedScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(ListTab) {
            Scaffold(
                content = {
                    Box(modifier = Modifier.padding(it)) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        TabNavigationItem(ListTab)
                        TabNavigationItem(ScheduleTab)
                        TabNavigationItem(SettingsTab)
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            Column(Modifier.align(Alignment.CenterVertically)) {
                val color = animateColorAsState(
                    if (tabNavigator.current == tab) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = .4f)
                    }
                ).value

                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    painter = tab.options.icon ?: return@BottomNavigationItem,
                    contentDescription = tab.options.title,
                    tint = color
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 2.dp),
                    text = tab.options.title,
                    color = color,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}