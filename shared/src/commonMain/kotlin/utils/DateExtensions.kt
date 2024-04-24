package utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil

fun LocalTime.toHourMinuteString(showMinutes: Boolean = true): String {
    val isAm = this.hour < 12
    val minuteText =
        if (this.minute > 10) this.minute else "0${this.minute}"
    val hourText =
        if (this.hour == 0) 12 else if (this.hour < 13) this.hour else this.hour - 12
    return "$hourText${if(showMinutes)":$minuteText" else ""} ${if (isAm) "am" else "pm"}"
}