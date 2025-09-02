package org.yarhooshmand.smartv3.util

import java.util.*
import java.util.regex.Pattern
import java.text.SimpleDateFormat
import kotlin.math.*

// A lightweight Persian natural-language time parser.
// Supports phrases like:
//  - "امروز ساعت 9", "فردا ساعت 14", "پس‌فردا ساعت 8:30"
//  - "دوشنبه ساعت 9", weekdays (با نام‌های فارسی)
//  - relative times: "در 2 ساعت", "بعد از 30 دقیقه"
//  - "هر روز" (returns null - periodic not implemented)
// Returns epoch millis or null if unable to parse.
//
// This is a heuristic parser intended to cover common cases — can be extended.
object TimeParser {
    private val weekdays = mapOf(
        "شنبه" to Calendar.SATURDAY,
        "یکشنبه" to Calendar.SUNDAY,
        "يكشنبه" to Calendar.SUNDAY,
        "دوشنبه" to Calendar.MONDAY,
        "سه‌شنبه" to Calendar.TUESDAY,
        "سه شنبه" to Calendar.TUESDAY,
        "چهارشنبه" to Calendar.WEDNESDAY,
        "چهار شنبه" to Calendar.WEDNESDAY,
        "پنجشنبه" to Calendar.THURSDAY,
        "پنج شنبه" to Calendar.THURSDAY,
        "جمعه" to Calendar.FRIDAY
    )

    private val numberRegex = Pattern.compile("(\\d{1,2})([:٫:\\.]?(\\d{1,2}))?")

    fun parsePersian(textIn: String, now: Long = System.currentTimeMillis()): Long? {
        val text = textIn.trim().lowercase(Locale("fa"))
        if (text.isEmpty()) return null

        val cal = Calendar.getInstance()
        cal.timeInMillis = now

        // direct explicit pattern: yyyy-MM-dd HH:mm
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val d = sdf.parse(textIn)
            if (d != null) return d.time
        } catch (_: Exception) {}

        // relative minutes/hours: "در 2 ساعت", "بعد از 30 دقیقه"
        val relHour = Regex("(در|بعد از)\\s*(\\d{1,3})\\s*ساعت")
        val relMin = Regex("(در|بعد از)\\s*(\\d{1,3})\\s*دقیقه")
        relHour.find(text)?.let {
            val h = it.groupValues[2].toIntOrNull() ?: 0
            return now + h * 3600_000L
        }
        relMin.find(text)?.let {
            val m = it.groupValues[2].toIntOrNull() ?: 0
            return now + m * 60_000L
        }

        // words: امروز، فردا، پس فردا
        if (text.contains("پس فردا") || text.contains("پس‌فردا")) {
            cal.add(Calendar.DAY_OF_YEAR, 2)
        } else if (text.contains("فردا")) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        } else if (text.contains("امروز") || text.contains("امروز")) {
            // same day
        } else {
            // weekday name?
            for ((name, dayOfWeek) in weekdays) {
                if (text.contains(name)) {
                    // advance to next occurrence of that weekday (including today if matches and time later)
                    val today = cal.get(Calendar.DAY_OF_WEEK)
                    var add = (dayOfWeek - today + 7) % 7
                    if (add == 0) add = 7 // assume next week if not specifying time later
                    cal.add(Calendar.DAY_OF_YEAR, add)
                    break
                }
            }
        }

        // try to find time like "ساعت 9:30" or "ساعت 9" or "9 صبح"
        val timeRegex = Regex("ساعت\\s*(\\d{1,2})([:.:٫](\\d{1,2}))?")
        timeRegex.find(text)?.let {
            val h = it.groupValues[1].toIntOrNull() ?: 0
            val m = it.groupValues[3].toIntOrNull() ?: 0
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            // if result is in past, and parser had "امروز", move to next day
            if (cal.timeInMillis <= now && (text.contains("امروز") || text.contains("فردا").not())) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            return cal.timeInMillis
        }

        // time like "9:30" or "9.30" standalone
        val m = numberRegex.matcher(text)
        if (m.find()) {
            val hh = m.group(1)?.toIntOrNull() ?: 0
            val mm = m.group(3)?.toIntOrNull() ?: 0
            if (hh in 0..23) {
                cal.set(Calendar.HOUR_OF_DAY, hh)
                cal.set(Calendar.MINUTE, mm)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                if (cal.timeInMillis <= now) cal.add(Calendar.DAY_OF_YEAR, 1)
                return cal.timeInMillis
            }
        }

        // fallback null
        return null
    }
}
