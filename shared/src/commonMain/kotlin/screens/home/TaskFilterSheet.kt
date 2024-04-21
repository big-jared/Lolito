package screens.home

import BottomSheetScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator

class TaskFilterSheet: BottomSheetScreen {

    @Composable
    override fun ColumnScope.BottomSheetContent() {
        val nav = LocalBottomSheetNavigator.current
        val coScope = rememberCoroutineScope()
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .weight(1f, false)
        ) {
            Text(
                text = "Edit task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
//            HeaderRow(horizontalPadding)
        }
    }
}