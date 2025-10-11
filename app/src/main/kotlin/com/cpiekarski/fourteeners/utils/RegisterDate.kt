package com.cpiekarski.fourteeners.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for using a consistent date/time format across the
 * SummitRegister project. Note, the millisecond field is always forced
 * to zero.
 */
class RegisterDate {
    companion object {
        private const val TAG = "RegisterDate"
        private const val FORMATTER = "yyyy-MM-dd'T'HH:mm:ssZ"
    }

    private val calendar: Calendar
    private val format: SimpleDateFormat

    /**
     * Constructs a RegisterDate object using current system date/time
     */
    constructor() {
        calendar = Calendar.getInstance(Locale.US)
        calendar.set(Calendar.MILLISECOND, 0)
        format = SimpleDateFormat(FORMATTER, Locale.US)
    }

    constructor(date: String) : this() {
        parseStrDate(date)
    }

    /**
     * Set RegisterDate to match formatted string
     * @param date previously formatted date using [getStrDate]
     * @return true if parsing success, otherwise false
     */
    fun parseStrDate(date: String): Boolean {
        return try {
            val parsedDate = format.parse(date)
            calendar.time = parsedDate!!
            calendar.set(Calendar.MILLISECOND, 0)
            true
        } catch (e: ParseException) {
            SRLOG.e(TAG, "Parsing exception: $e")
            false
        }
    }

    /**
     * Get a string representation of the current register date
     * @return String current register date
     */
    fun getStrDate(): String {
        val d = calendar.time
        return format.format(d)
    }

    /**
     * @param month 0-11
     * @param day 1-N
     * @param year real calendar year
     */
    fun setDate(month: Int, day: Int, year: Int) {
        SRLOG.v(TAG, "month: $month day: $day year: $year")
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
    }

    /**
     * @param hour 0-24
     * @param minute 0-59
     * @param second 0-59
     */
    fun setTime(hour: Int, minute: Int, second: Int) {
        SRLOG.v(TAG, "hour: $hour minute: $minute second: $second")
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * Return any calendar int field
     * @param calendarField Calendar.HOUR
     * @return the field value
     */
    fun getIntField(calendarField: Int): Int {
        return calendar.get(calendarField)
    }

    /**
     * @return String formatter used to parse/format ("yyyy-MM-dd'T'HH:mm:ssZ")
     */
    fun getFormatter(): String {
        return FORMATTER
    }
}