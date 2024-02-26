package screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import screens.create.TaskSheet
import utils.AppIconButton
import utils.increaseContrast

object ScheduleTab : Tab {
    @Composable
    override fun Content() {
        Calender(
            provider = ScheduleProvider(
                eventsForRange = { range ->
                    TaskViewModel.allTasks()
                }
            ),
        )
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Schedulable"
            val icon = rememberVectorPainter(Icons.Default.DateRange)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }
}

interface Schedulable {
    val time: Instant?

    @Composable
    open fun color(): Color = MaterialTheme.colorScheme.primary

    fun isScheduled() = time != null
}

class DateRange(start: LocalDate, end: LocalDate)

class ScheduleProvider(
    val eventsForRange: (DateRange) -> Set<Schedulable>
) {
    val events = mutableStateOf<Map<LocalDate, List<Schedulable>>>(emptyMap())

    suspend fun refresh(dateRange: DateRange) = withContext(Dispatchers.IO) {
        events.value = eventsForRange(dateRange)
            .filterNot { it.time == null }
            .groupBy { it.time!!.toLocalDateTime(TimeZone.currentSystemDefault()).date }
    }
}

fun LocalDate.startOfWeek(): LocalDate {
    var start = this
    while (start.dayOfWeek != DayOfWeek.MONDAY) {
        start = start.minus(1, DateTimeUnit.DAY)
    }
    return start
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calender(
    modifier: Modifier = Modifier,
    provider: ScheduleProvider,
) {
    val now = now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var selectedDate by remember { mutableStateOf(now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var startOfWeek by remember { mutableStateOf(selectedDate.startOfWeek()) }
    val startingPage = 1000
    val pagerState = rememberPagerState(
        initialPage = startingPage,
        initialPageOffsetFraction = 0f,
        pageCount = { 2000 }
    )
    Column(modifier.fillMaxSize()) {
        Header(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp),
            startDay = now.minus(startingPage - pagerState.currentPage, DateTimeUnit.WEEK).startOfWeek()
        )
        HorizontalPager(
            modifier = modifier.fillMaxWidth(),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { page ->
            startOfWeek = now.minus(startingPage - page, DateTimeUnit.WEEK).startOfWeek()

            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                var day = startOfWeek
                repeat((1..7).count()) {
                    CalendarItem(
                        day = day,
                        selected = day == selectedDate,
                        isToday = day == now,
                        events = provider.events.value[day] ?: emptyList()
                    ) {
                        selectedDate = it
                    }
                    day = day.plus(1, DateTimeUnit.DAY)
                }
            }
        }
    }
}

@Composable
fun RowScope.CalendarItem(
    modifier: Modifier = Modifier.padding(2.dp),
    day: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    events: List<Schedulable>,
    onSelected: (LocalDate) -> Unit
) {
    val backgroundedModifier = if (selected) {
        modifier.background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(16.dp)
        )
    } else if (isToday) {
        modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(16.dp)
        )
    } else {
        modifier
    }

    Column(
        backgroundedModifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .padding(2.dp)
            .clickable { onSelected(day) }) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = day.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.primary.increaseContrast() else MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = day.dayOfWeek.name.lowercase().capitalize(
                Locale.current
            ).take(3),
            textAlign = TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.primary.increaseContrast() else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, startDay: LocalDate) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    Box(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = startDay.month.name.lowercase().capitalize(Locale.current),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        AppIconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { bottomSheetNavigator.show(TaskSheet()) },
        )
    }
}

