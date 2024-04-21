package screens.home

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object NotificationsTab: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Notifications"
            val icon = rememberVectorPainter(Icons.Default.Notifications)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {

    }
}