package screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import green
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import screens.create.TaskSheet
import utils.AppIconButton
import utils.increaseContrast
import utils.toHourMinuteString
import kotlin.time.Duration.Companion.days

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
    val coScope = rememberCoroutineScope()
    val startingWeekPage = 1000
    val startingDayPage = 4000

    val weekPagerState = rememberPagerState(
        initialPage = startingWeekPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingWeekPage * 2 }
    )
    val dayPagerState = rememberPagerState(
        initialPage = startingDayPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingDayPage * 2 }
    )

    LaunchedEffect(dayPagerState.currentPage) {
        selectedDate = now.plus(dayPagerState.currentPage - startingDayPage, DateTimeUnit.DAY)
    }

    Column(modifier.fillMaxSize()) {
        Column(
            modifier = modifier.border(
                2.dp,
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ).padding(bottom = 16.dp)
        ) {
            Header(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                startDay = now.minus(
                    startingWeekPage - weekPagerState.currentPage,
                    DateTimeUnit.WEEK
                )
                    .startOfWeek()
            )
            HorizontalPager(
                modifier = modifier.fillMaxWidth(),
                state = weekPagerState,
                verticalAlignment = Alignment.Top
            ) { page ->
                startOfWeek = now.minus(startingWeekPage - page, DateTimeUnit.WEEK).startOfWeek()

                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    var day = startOfWeek
                    repeat((1..7).count()) {
                        CalendarItem(
                            day = day,
                            selected = day == selectedDate,
                            isToday = day == now,
                            events = provider.events.value[day] ?: emptyList()
                        ) {
                            coScope.launch {
                                selectedDate = it
                                dayPagerState.scrollToPage(startingDayPage - (now - selectedDate).days)
                            }
                        }
                        day = day.plus(1, DateTimeUnit.DAY)
                    }
                }
            }
        }

        var columnSize by remember { mutableStateOf(Size.Zero) }

        HorizontalPager(
            modifier = modifier.padding(top = 8.dp).fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    columnSize = layoutCoordinates.size.toSize()
                },
            state = dayPagerState,
            verticalAlignment = Alignment.Top
        ) { page ->
            val date = now.plus(startingDayPage - page, DateTimeUnit.DAY)
            DayColumn(provider.events.value[date] ?: emptyList(), columnSize)
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
fun DayColumn(events: List<Schedulable>, columnSize: Size) {
    val containerColor = MaterialTheme.colorScheme.primary
    val fontResolver = LocalFontFamilyResolver.current

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(LocalDensity.current.run { (2.5f * columnSize.height).toDp() })
                .drawWithContent {
                    gridLines(fontResolver, containerColor)
                }
        )
    }
}

fun ContentDrawScope.gridLines(fontResolver: FontFamily.Resolver, primaryColor: Color) {
    val interval = size.height / 24
    repeat((1..24).count()) { hours ->
        val y = interval * hours
        drawLine(
            primaryColor,
            Offset(0f, y),
            Offset(size.width, y),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f))
        )

        drawText(
            textMeasurer = TextMeasurer(
                defaultDensity = Density(16.dp.value),
                defaultLayoutDirection = LayoutDirection.Ltr,
                defaultFontFamilyResolver = fontResolver
            ),
            style = TextStyle(
                fontSize = 12.sp
            ),
            text = LocalTime.fromSecondOfDay(hours * (60 * 60))
                .toHourMinuteString(showMinutes = false),
            topLeft = Offset(8.dp.value, y)
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

