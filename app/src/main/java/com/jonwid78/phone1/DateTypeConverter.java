package com.jonwid78.phone1;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Ferit on 26-Oct-17.
 */

public class DateTypeConverter {

    @TypeConverter
    public static Date fromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
