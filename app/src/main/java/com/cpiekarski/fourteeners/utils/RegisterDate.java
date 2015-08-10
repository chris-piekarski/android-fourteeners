package com.cpiekarski.fourteeners.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for using a consistent date/time format across the
 * SummitRegister project. Note, the millisecond field is always forced
 * to zero.
 * 
 */
public class RegisterDate {
    private static final String TAG = "RegisterDate";

    private Calendar mCalendar;
    private SimpleDateFormat mFormat;
    private final String mFormatter = "yyyy-MM-dd'T'HH:mm:ssZ";
    
    /**
     * Constructs a RegisterDate object using current system date/time
     */
    public RegisterDate() {
        mCalendar = Calendar.getInstance(Locale.US);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mFormat = new SimpleDateFormat(mFormatter, Locale.US);
    }
    
    public RegisterDate(String date) {
        this();
        parseStrDate(date);
    }
    
    /**
     * Set RegisterDate to match formatted string
     * @param date previously formated date using {@link #getDate()}
     * @return true if parsing success, otherwise false
     */
    public boolean parseStrDate(String date) {
        boolean result = false;
        try {
            Date parsedDate = mFormat.parse(date);
            mCalendar.setTime(parsedDate);
            mCalendar.set(Calendar.MILLISECOND, 0);
            result = true;
        } catch (ParseException e) {
            SRLOG.e(TAG, "Parsing exception: "+e.toString());
        }
        return result;
    }
    
    /**
     * Get a string representation of the current register date
     * @return String current register date
     */
    public String getStrDate() {
        Date d = mCalendar.getTime();
        return mFormat.format(d);
    }
    
    /**
     * @param month 0-11
     * @param day 1-N
     * @param year real calendar year
     */
    public void setDate(int month, int day, int year) {
        SRLOG.v(TAG, "month: "+month+" day: "+day+" year: "+year );
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        mCalendar.set(Calendar.MONTH, month);
    }
    
    /**
     * @param hour 0-24
     * @param minute 0-59
     * @param second 0-59
     */
    public void setTime(int hour, int minute, int second) {
        SRLOG.v(TAG, "hour: "+hour+" minute: "+minute+" second: "+second);
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, second);
        mCalendar.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Return any calendar int field
     * @param calendarField Calendar.HOUR
     * @return the field value
     */
    public int getIntField(int calendarField) {
        return mCalendar.get(calendarField);
    }
    
    /**
     * @return String formatter used to parse/format ("yyyy-MM-dd'T'HH:mm:ssZ")
     */
    public String getFormatter() {
        return mFormatter;
    }

}