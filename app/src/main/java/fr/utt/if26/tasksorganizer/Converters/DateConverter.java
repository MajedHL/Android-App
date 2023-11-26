package fr.utt.if26.tasksorganizer.Converters;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date TimestampToDate(Long value) {
        return value == null ? null : new Date(value);

    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
       return date == null ? null : date.getTime();

    }
}

