package screens.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import backgroundContainer
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
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
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import screens.create.TaskSheet
import screens.home.TaskRepository
import utils.AppIconButton
import utils.increaseContrast
import utils.toHourMinuteString

object ScheduleTab : Tab {
    @Composable
    override fun Content() {
        Calender(
            provider = ScheduleProvider(eventsForRange = { range ->
                TaskRepository.allTasks()
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
        events.value = eventsForRange(dateRange).filterNot { it.time == null }
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

// Helper class defining swiping State
enum class SwipingStates {
    EXPANDED,
    COLLAPSED
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
    var startOfWeek by remember { mutableStateOf(selectedDate.startOfWeek()) }
    val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)
    val coScope = rememberCoroutineScope()
    val startingWeekPage = 1000
    val startingDayPage = 4000

    val weekPagerState = rememberPagerState(initialPage = startingWeekPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingWeekPage * 2 })
    val dayPagerState = rememberPagerState(initialPage = startingDayPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingDayPage * 2 })
    val monthState = rememberDatePickerState()
    var monthSelection by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        monthState.selectedDateMillis =
            selectedDate.atTime(0, 0).toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
        val anchorDate = if (selectedDate < now.startOfWeek()) selectedDate.minus(
            6,
            DateTimeUnit.DAY
        ) else selectedDate
        weekPagerState.scrollToPage(startingWeekPage - (now.startOfWeek() - anchorDate).days / 7)
        dayPagerState.scrollToPage(startingDayPage - (now - selectedDate).days)
    }

    LaunchedEffect(dayPagerState.currentPage) {
        selectedDate = now.plus(dayPagerState.currentPage - startingDayPage, DateTimeUnit.DAY)
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
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        0f to SwipingStates.EXPANDED,
                        heightInPx to SwipingStates.COLLAPSED,
                    )
                )
                .nestedScroll(connection)
        ) {
            Column() {
                // header background
                val background = MaterialTheme.colorScheme.surfaceContainerLowest
                val intermediate = MaterialTheme.colorScheme.surfaceContainerLow
                val primary = MaterialTheme.colorScheme.surfaceContainer

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
                                0.8f to intermediate,
                                1.0f to primary,
                                start = Offset(0.0f, 0.0f),
                                end = Offset(0.0f, size.height),
                            ), cornerRadius = CornerRadius(64f)
                        )
                    }
                ) {
                    // Header
                    Header(
                        modifier = Modifier.layoutId("header").fillMaxWidth()
                            .zIndex(4f)
                            .padding(horizontal = 8.dp)
                            .padding(top = 8.dp), startDay = now.minus(
                            startingWeekPage - weekPagerState.currentPage, DateTimeUnit.WEEK
                        ).startOfWeek()
                    )

                    Box {
                        // Month content
                        Box(
                            modifier.layoutId("week")
                                .padding(top = 8.dp)
                                .heightIn(max = 360.dp)
                                .fillMaxHeight(if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction else 1f - swipingState.progress.fraction)
                                .alpha((if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction else 1f - swipingState.progress.fraction))
                        ) {
                            DatePicker(
                                modifier = Modifier.fillMaxWidth(),
                                state = monthState,
                                title = null,
                                headline = null,
                                showModeToggle = false
                            )
                        }

                        // Week content
                        Box(
                            modifier.layoutId("week")
                                .heightIn(max = 84.dp)
//                                .fillMaxHeight(if (swipingState.progress.to == SwipingStates.EXPANDED) swipingState.progress.fraction else 1f - swipingState.progress.fraction)
                                .alpha(if (swipingState.progress.to == SwipingStates.EXPANDED) swipingState.progress.fraction else 1f - swipingState.progress.fraction)
                        ) {
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
                                            events = provider.events.value[day] ?: emptyList()
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
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp)
                                .size(32.dp, 4.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurfaceVariant, shape = RoundedCornerShape(8.dp)
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
                        .layoutId("day")
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

@Composable
fun RowScope.CalendarItem(
    modifier: Modifier = Modifier.padding(2.dp),
    day: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    events: List<Schedulable>,
    onSelected: (LocalDate) -> Unit
) {
    var backgroundedModifier = if (selected) {
        modifier.background(
            color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(16.dp)
        )
    } else modifier
    backgroundedModifier = if (isToday) {
        backgroundedModifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(16.dp)
        )
    } else backgroundedModifier

    Column(backgroundedModifier.weight(1f).clip(RoundedCornerShape(16.dp)).padding(2.dp)
        .clickable { onSelected(day) }) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = day.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = day.dayOfWeek.name.lowercase().capitalize(
                Locale.current
            ).take(3),
            textAlign = TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DayColumn(events: List<Schedulable>) {
    val containerColor = MaterialTheme.colorScheme.primary
    val fontResolver = LocalFontFamilyResolver.current

    Box(modifier = Modifier.fillMaxWidth()
        .height(1000.dp)
        .drawWithContent {
            gridLines(fontResolver, containerColor)
        })
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
            modifier = Modifier.align(Alignment.Center),
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

