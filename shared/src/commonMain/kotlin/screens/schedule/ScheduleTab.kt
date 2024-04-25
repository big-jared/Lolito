package screens.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
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
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import models.ScheduledTask
import models.Task
import models.TaskType
import models.User
import screens.home.TaskRepository
import utils.toHourMinuteString
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration

object ScheduleTab : Tab {
    @Composable
    override fun Content() {
        Calender(
            provider = ScheduleProvider(eventsForRange = { range ->
                TaskRepository.allTasks().mapNotNull { it.toScheduledTask() }.toSet()
            }),
        )
    }

    override val options: TabOptions
        @Composable get() {
            val title = "Schedulable"
            val icon = rememberVectorPainter(Icons.Default.DateRange)

            return remember {
                TabOptions(
                    index = 1u, title = title, icon = icon
                )
            }
        }
}

class DateRange(start: LocalDate, end: LocalDate)

class ScheduleProvider(
    val eventsForRange: (DateRange) -> Set<ScheduledTask>
) {
    val events = mutableStateOf<Map<LocalDate, List<ScheduledTask>>>(emptyMap())

    suspend fun refresh(dateRange: DateRange) = withContext(Dispatchers.IO) {
        events.value = eventsForRange(dateRange).groupBy { it.due.toLocalDateTime(TimeZone.currentSystemDefault()).date }
    }
}

fun LocalDate.startOfWeek(): LocalDate {
    var start = this
    while (start.dayOfWeek != DayOfWeek.MONDAY) {
        start = start.minus(1, DateTimeUnit.DAY)
    }
    return start
}

fun LocalDate.startOfMonth(): LocalDate {
    var start = this
    while (start.dayOfMonth != 1) {
        start = start.minus(1, DateTimeUnit.DAY)
    }
    return start
}

fun LocalDate.monthDays(): List<LocalDate> {
    var current = this.startOfMonth().startOfWeek()
    val dates = mutableListOf<LocalDate>()
    while (current.month <= this.month || dates.size % 7 != 6) {
        dates.add(current)
        current = current.plus(1, DateTimeUnit.DAY)
    }
    dates.add(current)
    return dates
}

// Helper class defining swiping State
enum class SwipingStates {
    Collapsed,
    Expanded
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun Calender(
    modifier: Modifier = Modifier,
    provider: ScheduleProvider,
) {
    val now = now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var selectedDate by remember { mutableStateOf(now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var shownMonth by remember { mutableStateOf(selectedDate) }
    var startOfWeek by remember { mutableStateOf(selectedDate.startOfWeek()) }
    var startOfMonth by remember { mutableStateOf(selectedDate.startOfMonth()) }
    val swipingState = rememberSwipeableState(initialValue = SwipingStates.Collapsed)
    val swipePercentage = if (swipingState.progress.to == SwipingStates.Expanded) swipingState.progress.fraction else 1f - swipingState.progress.fraction
    val coScope = rememberCoroutineScope()
    val startingWeekPage = 1000
    val startingDayPage = 4000
    val startingMonthPage = 4000

    val monthPagerState = rememberPagerState(initialPage = startingMonthPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingMonthPage * 2 })
    val weekPagerState = rememberPagerState(initialPage = startingWeekPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingWeekPage * 2 })
    val dayPagerState = rememberPagerState(initialPage = startingDayPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingDayPage * 2 })

    LaunchedEffect(selectedDate) {
        val weekAnchorDate = if (selectedDate < now.startOfWeek()) selectedDate.minus(6, DateTimeUnit.DAY) else selectedDate
        weekPagerState.scrollToPage(startingWeekPage + (now.startOfWeek().daysUntil(weekAnchorDate) / 7))
        val monthAnchorDate = if (selectedDate < now.startOfMonth()) selectedDate.minus(1, DateTimeUnit.MONTH) else selectedDate
        monthPagerState.scrollToPage(startingMonthPage + (now.startOfMonth().monthsUntil(monthAnchorDate)))
        dayPagerState.scrollToPage(startingDayPage + (now.daysUntil(selectedDate)))
    }

    LaunchedEffect(dayPagerState.currentPage) {
        selectedDate = now.plus(dayPagerState.currentPage - startingDayPage, DateTimeUnit.DAY)
    }

    LaunchedEffect(weekPagerState.currentPage) {
        shownMonth = now.plus(weekPagerState.currentPage - startingWeekPage, DateTimeUnit.WEEK).startOfWeek()
    }

    LaunchedEffect(monthPagerState.currentPage) {
        shownMonth = now.plus(monthPagerState.currentPage - startingMonthPage, DateTimeUnit.MONTH).startOfMonth()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val heightInPx = with(LocalDensity.current) { maxHeight.toPx() }
        val connection = remember {
            object : NestedScrollConnection {

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return if (delta < 0) {
                        swipingState.performDrag(delta).toOffset()
                    } else {
                        Offset.Zero
                    }
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return swipingState.performDrag(delta).toOffset()
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    swipingState.performFling(velocity = available.y)
                    return super.onPostFling(consumed, available)
                }

                private fun Float.toOffset() = Offset(0f, this)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .swipeable(
                    state = swipingState,
                    thresholds = { _, _ -> FractionalThreshold(0.8f) },
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        0f to SwipingStates.Collapsed,
                        heightInPx to SwipingStates.Expanded,
                    )
                )
                .nestedScroll(connection)
        ) {
            Column() {
                // header background
                val background = MaterialTheme.colorScheme.surfaceContainerLowest
                val intermediate = MaterialTheme.colorScheme.surfaceContainer
                val primary = MaterialTheme.colorScheme.primaryContainer

                Column(
                    modifier = Modifier.fillMaxWidth().background(
                        shape = RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        ), color = MaterialTheme.colorScheme.surface
                    ).drawBehind {
                        this.drawRoundRect(
                            Brush.linearGradient(
                                0.0f to background,
                                0.5f to intermediate,
                                1.0f to primary,
                                start = Offset(0.0f, 0.0f),
                                end = Offset(0.0f, size.height),
                            ), cornerRadius = CornerRadius(64f)
                        )
                    }
                ) {
                    Header(
                        modifier = Modifier.layoutId("header").fillMaxWidth()
                            .zIndex(4f)
                            .padding(horizontal = 8.dp)
                            .padding(top = 8.dp),
                        startDay = shownMonth
                    )

                    Column {
                        Box {
                            HorizontalPager(
                                modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                state = weekPagerState,
                                verticalAlignment = Alignment.Top
                            ) { page ->
                                startOfWeek =
                                    now.minus(startingWeekPage - page, DateTimeUnit.WEEK)
                                        .startOfWeek()
                                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                    var day = startOfWeek
                                    repeat((1..7).count()) {
                                        CalendarItem(
                                            day = day,
                                            selected = day == selectedDate,
                                            isToday = day == now,
                                            swipingState = swipingState,
                                            events = provider.events.value[day] ?: emptyList(),
                                            showWeekday = true
                                        ) {
                                            coScope.launch {
                                                selectedDate = it
                                            }
                                        }
                                        day = day.plus(1, DateTimeUnit.DAY)
                                    }
                                }
                            }
                        }

                        Box(
                            modifier
                                .padding(vertical = 8.dp)
                                .heightIn(max = 256.dp * swipePercentage)
                                .alpha(swipePercentage)
                        ) {
                            HorizontalPager(
                                modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                state = monthPagerState,
                                beyondBoundsPageCount = 0,
                                verticalAlignment = Alignment.Top
                            ) { page ->
                                startOfMonth = now.minus(startingMonthPage - page, DateTimeUnit.MONTH).startOfMonth()
                                Column {
                                    val days = startOfMonth.monthDays()
                                    days.chunked(7).forEach { week ->
                                        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                            week.forEach { day ->
                                                CalendarItem(
                                                    day = day,
                                                    selected = day == selectedDate,
                                                    isToday = day == now,
                                                    swipingState = null,
                                                    events = provider.events.value[day]
                                                        ?: emptyList(),
                                                    showWeekday = false
                                                ) {
                                                    coScope.launch {
                                                        selectedDate = it
                                                        swipingState.animateTo(SwipingStates.Collapsed)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp)
                                .size(32.dp, 4.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .align(Alignment.Center)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .nestedScroll(TopAppBarDefaults.enterAlwaysScrollBehavior().nestedScrollConnection)
                ) {
                    HorizontalPager(
                        modifier = modifier.padding(top = 8.dp).fillMaxWidth(),
                        state = dayPagerState,
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        val date = now.plus(startingDayPage - page, DateTimeUnit.DAY)
                        DayColumn(provider.events.value[date] ?: emptyList())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RowScope.CalendarItem(
    modifier: Modifier = Modifier,
    day: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    showWeekday: Boolean,
    events: List<ScheduledTask>,
    swipingState: SwipeableState<SwipingStates>? = null,
    onSelected: (LocalDate) -> Unit
) {
    val shape = if (showWeekday) RoundedCornerShape(16.dp) else CircleShape
    val swipePercentage = if (swipingState == null) 1f else
        if (swipingState.progress.to == SwipingStates.Collapsed) swipingState.progress.fraction else 1f - swipingState.progress.fraction
    val containerColor =
        if (showWeekday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = swipePercentage) else MaterialTheme.colorScheme.primary
    val contentColor =
        if (showWeekday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = swipePercentage) else MaterialTheme.colorScheme.onPrimary

    var backgroundedModifier = modifier
    backgroundedModifier = if (selected) {
        modifier.background(
            color = containerColor,
            shape = shape
        )
    } else backgroundedModifier
    backgroundedModifier = if (isToday) {
        backgroundedModifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = swipePercentage),
            shape = shape
        )
    } else backgroundedModifier

    if (showWeekday) {
        Column(backgroundedModifier.clip(shape).padding(4.dp).weight(1f)
            .clickable { onSelected(day) }) {
            Text(
                modifier = Modifier.fillMaxWidth().heightIn(
                    max = 28.dp * swipePercentage,
                    min = if (swipingState == null) 32.dp else Dp.Unspecified
                ),
                text = day.dayOfMonth.toString(),
                fontSize = 16.sp * swipePercentage,
                textAlign = TextAlign.Center,
                color = if (selected) contentColor else MaterialTheme.colorScheme.onSurface
            )
            if (showWeekday) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = day.dayOfWeek.name.lowercase().capitalize(
                        Locale.current
                    ).take(max(1.0, 3.0 * swipePercentage).roundToInt()),
                    textAlign = TextAlign.Center,
                    color = if (selected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight((400f * (1 - swipePercentage)).toInt() + 400)
                )
            }
        }
    } else {
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = backgroundedModifier.padding(2.dp).size(36.dp).align(Alignment.Center).clip(shape).clickable { onSelected(day) }.align(Alignment.Center),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = day.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    color = if (selected) contentColor else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun DayColumn(events: List<ScheduledTask>) {
    val containerColor = MaterialTheme.colorScheme.primary
    val fontResolver = LocalFontFamilyResolver.current

//    LazyColumn {
//        items(events.groupBy { it.due }.size) {
//            events[it]
//        }
//    }

    Box(modifier = Modifier.fillMaxWidth()
        .height(1000.dp)
        .drawWithContent {
            gridLines(fontResolver, containerColor)
        })
}

@Composable
fun TaskRow(type: TaskType, tasks: Set<Task>) {

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
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
            text = startDay.month.name.lowercase().capitalize(Locale.current),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
            text = "${startDay.year}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

