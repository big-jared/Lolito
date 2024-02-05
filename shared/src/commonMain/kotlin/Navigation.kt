import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen

// override of Screen with bottom-sheet styling
interface BottomSheetScreen : Screen {
    @Composable
    override fun Content() {
        val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, shape = shape).clip(shape)
                .padding(top = 16.dp)
        ) {
            Box(
                Modifier.height(6.dp).width(48.dp).background(lightGrey, RoundedCornerShape(12.dp))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            BottomSheetContent()
        }
    }

    @Composable
    fun ColumnScope.BottomSheetContent()
}