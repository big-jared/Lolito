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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.OnSwipe
import androidx.constraintlayout.compose.SwipeDirection
import androidx.constraintlayout.compose.SwipeSide
import blue
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
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import screens.create.TaskSheet
import utils.AppIconButton
import utils.increaseContrast
import utils.toHourMinuteString

object ScheduleTab : Tab {
    @Composable
    override fun Content() {
        Calender(
            provider = ScheduleProvider(eventsForRange = { range ->
                TaskViewModel.allTasks()
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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

    val weekPagerState = rememberPagerState(initialPage = startingWeekPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingWeekPage * 2 })
    val dayPagerState = rememberPagerState(initialPage = startingDayPage,
        initialPageOffsetFraction = 0f,
        pageCount = { startingDayPage * 2 })

    LaunchedEffect(dayPagerState.currentPage) {
        selectedDate = now.plus(dayPagerState.currentPage - startingDayPage, DateTimeUnit.DAY)
    }

    MotionLayout(
        motionScene = MotionScene {
            val headerBackground = createRefFor("headerBackground")
            val header = createRefFor("header")
            val month = createRefFor("month")
            val week = createRefFor("week")
            val dragBar = createRefFor("dragBar")
            val day = createRefFor("day")

            defaultTransition(from = constraintSet {
                constrain(headerBackground) {
                    top.linkTo(parent.top)
                    bottom.linkTo(dragBar.bottom)
                    height = Dimension.fillToConstraints
                }
                constrain(header) {
                    top.linkTo(parent.top)
                }
                constrain(week) {
                    top.linkTo(header.bottom)
                    height = Dimension.wrapContent
                    alpha = 1f
                }
                constrain(month) {
                    top.linkTo(header.bottom)
                    height = Dimension.value(0.dp)
                    alpha = 0f
                }
                constrain(dragBar) {
                    top.linkTo(week.bottom)
                    centerHorizontallyTo(parent)
                }
                constrain(day) {
                    top.linkTo(headerBackground.bottom)
                }
            }, to = constraintSet {
                constrain(headerBackground) {
                    top.linkTo(parent.top)
                    bottom.linkTo(dragBar.bottom)
                    height = Dimension.fillToConstraints
                }
                constrain(header) {
                    top.linkTo(parent.top)
                }
                constrain(week) {
                    top.linkTo(header.bottom)
                    alpha = 0f
                }
                constrain(month) {
                    top.linkTo(header.bottom)
                    height = Dimension.wrapContent
                    alpha = 1f
                }
                constrain(dragBar) {
                    top.linkTo(month.bottom)
                    centerHorizontallyTo(parent)
                }
                constrain(day) {
                    top.linkTo(headerBackground.bottom)
                }
            }) {
                onSwipe = OnSwipe(
                    anchor = day,
                    side = SwipeSide.Top,
                    direction = SwipeDirection.Down,
                    dragScale = .5f,
                )
            }
        },
        progress = 0f, // OnSwipe handles the progress, so this should be constant to avoid conflict
        modifier = Modifier.fillMaxSize()
    ) {

        // header background
        val background = MaterialTheme.colorScheme.background
        val intermediate = MaterialTheme.colorScheme.primaryContainer.increaseContrast()
        val primary = MaterialTheme.colorScheme.primaryContainer
        Box(modifier.layoutId("headerBackground").fillMaxWidth().height(0.dp).drawBehind {
            this.drawRoundRect(
                Brush.linearGradient(
                    0.0f to background,
                    0.8f to intermediate,
                    1.0f to primary,
                    start = Offset(0.0f, 0.0f),
                    end = Offset(0.0f, size.height),
                ), cornerRadius = CornerRadius(64f)
            )
        })

        // Header
        Header(
            modifier = Modifier.layoutId("header").fillMaxWidth().padding(horizontal = 8.dp)
                .padding(top = 8.dp), startDay = now.minus(
                startingWeekPage - weekPagerState.currentPage, DateTimeUnit.WEEK
            ).startOfWeek()
        )

        // Month content
        Box(modifier.layoutId("month").fillMaxWidth().height(300.dp)) {}

        // Week content
        Box(modifier.layoutId("week")) {
            HorizontalPager(
                modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
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

        // Drag bar
        Box(
            modifier = Modifier.layoutId("dragBar").padding(vertical = 12.dp).size(32.dp, 4.dp)
                .background(
                    MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .nestedScroll(TopAppBarDefaults.enterAlwaysScrollBehavior().nestedScrollConnection)
                .layoutId("day")
        ) {
            repeat(20) {
                Box(modifier = Modifier.height(200.dp).fillMaxWidth().padding(20.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)))
            }
//            HorizontalPager(
//                modifier = modifier.padding(top = 8.dp).fillMaxWidth(),
//                state = dayPagerState,
//                verticalAlignment = Alignment.Top
//            ) { page ->
//                val date = now.plus(startingDayPage - page, DateTimeUnit.DAY)
//                DayColumn(provider.events.value[date] ?: emptyList())
//            }
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
            color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp)
        )
    } else modifier
    backgroundedModifier = if (isToday) {
        backgroundedModifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        )
    } else backgroundedModifier

    Column(backgroundedModifier.weight(1f).clip(RoundedCornerShape(16.dp)).padding(2.dp)
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

